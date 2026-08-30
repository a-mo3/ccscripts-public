package org.dreambot.behaviour.misc;


import lombok.SneakyThrows;
import org.dreambot.PvmMain;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.muling.impl.MuleState;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Supplier;

/**
 * Mule off after x hours
 */
public class MuleOff extends Fractal implements ConfigurableFractal<MuleOffSettings> {
    public static Timer timer;
    final DecimalFormat DF = new DecimalFormat("###,###,###");
    int hoursUntilMuleOff;
    int moneyLeftAfterMuling;
    public static int minMuleOff = 250_000;

    public MuleOff(int hoursUntilMuleOff, int moneyLeftAfterMuling) {
        super(() -> {
            if (timer == null) timer = new Timer((long) hoursUntilMuleOff * 1000 * 60 * 60);
            if (PvmMain.scriptName.contains("zulrah") && BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) > 50)
                return false;
            return !Combat.isInWild() && timer.finished();
        });
        this.hoursUntilMuleOff = hoursUntilMuleOff;
        this.moneyLeftAfterMuling = moneyLeftAfterMuling;
        // on script start set the mule state IP address that will globally affect all mule events
        MuleState.MULE_SERVER_ADDRESS = getSettings().ipAddress + ":" + getSettings().port;
        MuleOffSettings settings = getSettings();
        if (settings != null) minMuleOff = settings.minMuleOnAmount;
        // todo idk where port is actually meant to be used
    }

    public MuleOff() {
        super(null);
        MuleOffSettings settings = getSettings();
        this.hoursUntilMuleOff = settings.hoursUntilMuleOff;
        this.moneyLeftAfterMuling = settings.moneyLeftAfterMuling;
        timer = new Timer((long) settings.hoursUntilMuleOff * 60 * 60 * 1000);
        // on script start set the mule state IP address that will globally affect all mule events
        MuleState.MULE_SERVER_ADDRESS = getSettings().ipAddress + ":" + getSettings().port;
        // todo idk where port is actually meant to be used
        minMuleOff = settings.minMuleOnAmount;
    }

    @Override
    public boolean isValid() {
        // only the no args constructor should consider accept conditions
        if (acceptCondition != null) return super.isValid();
        if (Inventory.contains("Loot key")) {
            log("Preventing muling, we have a loot key");
            return false;
        }
        // isValid is overriden to accomidate new settings framework, hours until muleoff is not dep injected so can be considered in super
        if (timer == null) timer = new Timer((long) hoursUntilMuleOff * 1000 * 60 * 60);
        return !Combat.isInWild() && timer.finished();
    }

    public MuleOff(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        // on script start set the mule state IP address that will globally affect all mule events
        MuleState.MULE_SERVER_ADDRESS = getSettings().ipAddress + ":" + getSettings().port;
    }

    @SneakyThrows
    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            Logger.info("Bank all mule off");
            new BankAllInventoryEvent().execute();
        }

        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (BankUtil.openClosest()) Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (containsAnyShouldSell()) {
            Logger.info("Selling all items");
            new SellAllEvent(getMuleItems())
                    .setInterruptCondition(Combat::isInWild)
                    .execute();
            return ReactionGenerator.getNormal();
        }
        int ownedCoins = OwnedItems.count(ItemID.COINS_995);

        if (GrandExchange.isReadyToCollect()) {
            log("Collect from ge");
            if (!GrandExchange.isOpen()) {
                if (Walking.shouldWalk()) GrandExchange.open();
                return ReactionGenerator.getNormal();
            }
            GrandExchange.collect();
            return ReactionGenerator.getNormal();
        }

        log("Owned coins " + ownedCoins);
        if (!Trade.isOpen()
                && ownedCoins <= moneyLeftAfterMuling) {
            if (getSettings().hourVariation == 0) {
                timer.reset();
                return ReactionGenerator.getNormal();
            }
            long l = (long) hoursUntilMuleOff * 1000 * 60 * 60
                    + (Calculations.random(getSettings().hourVariation) * 1000 * 60 * 60 * (Calculations.random(2) * -1)
            );
            log("Mule off time " + l);
            timer = new Timer(l);
            return ReactionGenerator.getNormal();
        }

        Logger.info("Making mule request remaining " + moneyLeftAfterMuling);
        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.COINS_995, ownedCoins - moneyLeftAfterMuling)
                .execute();
        return ReactionGenerator.getNormal();
    }

    /**
     * checks your list of muleitems for items that are enabled and have more than their remaining
     *
     * @return true if you should sell
     */
    private boolean containsAnyShouldSell() {
        List<MuleOffItem> items = getMuleItems();

        // contains any with include noted ignores equipment implicitly
        return OwnedItems.containsAny(true, items.stream()
                .filter(MuleOffItem::shouldSell) // this checks from owns enough and enabled
                .mapToInt(MuleOffItem::getItemID)
                .toArray()
        );
    }

    // combines loot and mule off items
    private static List<MuleOffItem> getMuleItems() {
        // todo add a failsafe to remove anything from loot that is in muleoffitems as well,
        //  muleoffitems contains more information so should be preferred
        return MuleOffItem.makeMuleItems(LOOT, muleOffItems);
    }

    public static MuleOffItem[] muleOffItems;

    public static int[] LOOT = new int[]{
            ItemID.PRAYER_POTION2,
            ItemID.EMERALD_RING,
            ItemID.SAPPHIRE_RING,
            ItemID.IRON_ORE,
            ItemID.COAL,
            ItemID.VILE_ASHES,
            ItemID.BLIGHTED_ANCIENT_ICE_SACK,
            ItemID.BLIGHTED_ANGLERFISH,
            ItemID.RING_OF_WEALTH,
            ItemID.WYRM_BONES,
            ItemID.RUNE_FULL_HELM,
            ItemID.RUNE_DART,
            ItemID.RUNE_DAGGER,
            ItemID.ADAMANT_FULL_HELM,
            ItemID.MITHRIL_KITESHIELD,
            ItemID.RUNE_SPEAR,
            ItemID.DRAGON_BONES,
            ItemID.MITHRIL_SPEAR,
            ItemID.GREEN_DRAGONHIDE,
            ItemID.ADAMANTITE_ORE,
            ItemID.RUNE_DART,
            ItemID.RUNE_KNIFE,
            ItemID.ADAMANT_2H_SWORD,
            ItemID.ADAMANT_PLATEBODY,
            ItemID.RUNE_AXE,
            ItemID.RUNE_KITESHIELD,
            ItemID.RUNE_LONGSWORD,
            ItemID.RUNE_MED_HELM,
            ItemID.RUNE_FULL_HELM,
            ItemID.LAVA_BATTLESTAFF,

            ItemID.RUNE_JAVELIN,
            ItemID.BLOOD_RUNE,
            ItemID.RUNITE_BOLTS,
            ItemID.LAW_RUNE,
            ItemID.LAVA_RUNE,

            ItemID.GRIMY_RANARR_WEED,
            ItemID.GRIMY_IRIT_LEAF,
            ItemID.GRIMY_KWUARM,
            ItemID.GRIMY_LANTADYME,
            ItemID.GRIMY_DWARF_WEED,
            ItemID.GRIMY_AVANTOE,
            ItemID.GRIMY_CADANTINE,
            ItemID.GRIMY_GUAM_LEAF,
            ItemID.GRIMY_HARRALANDER,

            ItemID.DRAGON_JAVELIN_HEADS,
            ItemID.FIRE_ORB,
            ItemID.ADAMANTITE_BAR,
            ItemID.LOOP_HALF_OF_KEY,
            ItemID.TOOTH_HALF_OF_KEY,
            ItemID.AMULET_OF_GLORY,


            ItemID.STEEL_ARROW,
            ItemID.RUNE_ARROW,
            ItemID.SILVER_ORE,
            ItemID.FIRE_TALISMAN,
            ItemID.DRAGON_MED_HELM,
            ItemID.DRAGON_SPEAR,
            ItemID.DRAGONSTONE,
            ItemID.UNCUT_DIAMOND,
            ItemID.UNCUT_RUBY,
            ItemID.UNCUT_EMERALD,
            ItemID.ADAMANT_JAVELIN,
            ItemID.RUNE_BATTLEAXE,
            ItemID.RUNE_SQ_SHIELD,
            ItemID.RUNE_2H_SWORD,
            ItemID.RUNE_SPEAR,
            ItemID.RUNITE_BAR,
            ItemID.DEATH_RUNE,
            ItemID.NATURE_RUNE,
            ItemID.SHIELD_LEFT_HALF,

            ItemID.ENSOULED_DRAGON_HEAD_13511,
            ItemID.ENSOULED_DRAGON_HEAD,
            ItemID.LAVA_DRAGON_BONES,
            ItemID.BLACK_DRAGONHIDE,
            ItemID.ONYX_BOLT_TIPS,
            ItemID.LAVA_SCALE,
            ItemID.SAPPHIRE_RING,
            ItemID.UNCUT_SAPPHIRE,
            ItemID.RING_OF_RECOIL,
            ItemID.DRACONIC_VISAGE,

            ItemID.GRANITE_MAUL,
            ItemID.MYSTIC_ROBE_TOP,
            ItemID.MYSTIC_ROBE_TOP_LIGHT,
            ItemID.MYSTIC_ROBE_BOTTOM_LIGHT,
            ItemID.ADAMANT_PLATEBODY,
            ItemID.ADAMANT_PLATELEGS,
            ItemID.RUNE_FULL_HELM,
            ItemID.RUNE_2H_SWORD,
            ItemID.ADAMANT_BOOTS,
            ItemID.RUNE_BATTLEAXE,
            ItemID.RUNE_PLATELEGS,

            ItemID.CHAOS_RUNE,
            ItemID.DEATH_RUNE,
            ItemID.GOLD_ORE,
            ItemID.PURE_ESSENCE,
            ItemID.STEEL_BAR,
            ItemID.GOLD_BAR,
            ItemID.MITHRIL_BAR,
            ItemID.RUNITE_ORE,

            ItemID.GUTHANS_CHAINSKIRT_0,
            ItemID.GUTHANS_HELM_0,
            ItemID.GUTHANS_PLATEBODY_0,
            ItemID.GUTHANS_WARSPEAR_0,
            ItemID.FIRE_RUNE,
            ItemID.MYSTIC_ROBE_TOP_DARK,
            ItemID.MYSTIC_ROBE_BOTTOM_DARK,
            ItemID.LIMPWURT_ROOT,
            ItemID.TREASONOUS_RING,
            ItemID.COCONUT,
            ItemID.PAPAYA_FRUIT,
            ItemID.PLANK,
            ItemID.WHITE_BERRIES,
            ItemID.FLAX,
            ItemID.BIG_BONES,
            ItemID.MARIGOLD_SEED,
            ItemID.ROSEMARY_SEED,
            ItemID.NASTURTIUM_SEED,
            ItemID.WOAD_SEED,
            ItemID.LIMPWURT_SEED,
            ItemID.REDBERRY_SEED,
            ItemID.CADAVABERRY_SEED,
            ItemID.DWELLBERRY_SEED,
            ItemID.JANGERBERRY_SEED,
            ItemID.WHITEBERRY_SEED,
            ItemID.POISON_IVY_SEED,
            ItemID.SEEDS,
            ItemID.CACTUS_SEED,
            ItemID.BELLADONNA_SEED,
            ItemID.MUSHROOM_SPORE,
            ItemID.RED_SPIDERS_EGGS,
            ItemID.APPLE_TREE_SEED,
            ItemID.BANANA_TREE_SEED,
            ItemID.ORANGE_TREE_SEED,
            ItemID.CURRY_TREE_SEED,
            ItemID.PINEAPPLE_SEED,
            ItemID.PAPAYA_TREE_SEED,
            ItemID.PALM_TREE_SEED,
            ItemID.CALQUAT_TREE_SEED,
            ItemID.GUAM_SEED,
            ItemID.MARRENTILL_SEED,
            ItemID.TARROMIN_SEED,
            ItemID.HARRALANDER_SEED,
            ItemID.RANARR_SEED,
            ItemID.TOADFLAX_SEED,
            ItemID.IRIT_SEED,
            ItemID.AVANTOE_SEED,
            ItemID.KWUARM_SEED,
            ItemID.SNAPDRAGON_SEED,
            ItemID.CADANTINE_SEED,
            ItemID.LANTADYME_SEED,
            ItemID.DWARF_WEED_SEED,
            ItemID.TORSTOL_SEED,
            ItemID.BARLEY_SEED,
            ItemID.JUTE_SEED,
            ItemID.HAMMERSTONE_SEED,
            ItemID.ASGARNIAN_SEED,
            ItemID.YANILLIAN_SEED,
            ItemID.KRANDORIAN_SEED,
            ItemID.WILDBLOOD_SEED,
            ItemID.ACORN,
            ItemID.WILLOW_SEED,
            ItemID.MAPLE_SEED,
            ItemID.YEW_SEED,
            ItemID.MAGIC_SEED,
            ItemID.POTATO_SEED,
            ItemID.ONION_SEED,
            ItemID.SWEETCORN_SEED,
            ItemID.WATERMELON_SEED,
            ItemID.TOMATO_SEED,
            ItemID.STRAWBERRY_SEED,
            ItemID.CABBAGE_SEED,
            ItemID.SUPER_COMBAT_POTION1,
            ItemID.SUPER_COMBAT_POTION2,
            ItemID.SUPER_COMBAT_POTION3,
            ItemID.AMULET_OF_GLORY_UNCHARGED,
            ItemID.RING_OF_WEALTH
    };

    @Override
    public MuleOffSettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new MuleOffSettings());
    }

    @Override
    public String settingName() {
        return "Muling";
    }
}
