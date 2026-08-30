package org.dreambot.behaviour.method.revs.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.scriptdata.RevenantSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class LootRevs extends Fractal {
    public static final int[] LOOT = new int[]{
            ItemID.RUNITE_ORE,
            ItemID.DRAGONSTONE_BOLT_TIPS,
            ItemID.BLACK_MASK,
            ItemID.ADAMANTITE_BAR,
            ItemID.MAHOGANY_PLANK,
            ItemID.BLACK_DRAGONHIDE,
            ItemID.YEW_LOGS,
            ItemID.COAL,
            ItemID.DRAGONSTONE_BOLTS,
            ItemID.RUNITE_BAR,
            ItemID.SAPPHIRE_RING,
            ItemID.EMERALD_RING,
            ItemID.RING_OF_DUELING8,
            ItemID.RING_OF_RECOIL,
            ItemID.DRAGON_2H_SWORD,
            ItemID.DRAGON_PICKAXE,
            ItemID.SUPER_RESTORE4,
            ItemID.PLANK,
            ItemID.DRAGON_BONES,
            ItemID.OAK_PLANK,
            ItemID.ANCIENT_STAFF,

            ItemID.DRAGON_MED_HELM,
            ItemID.DRAGON_PLATELEGS,
            ItemID.DRAGON_PLATESKIRT,
            ItemID.DRAGON_LONGSWORD,
            ItemID.DRAGON_DAGGER,

            ItemID.RUNE_FULL_HELM,
            ItemID.BRACELET_OF_ETHEREUM_UNCHARGED,
            ItemID.RUNE_PLATELEGS,
            ItemID.RUNE_PLATEBODY,
            ItemID.RUNE_WARHAMMER,
            ItemID.RUNE_KITESHIELD,
            ItemID.BATTLESTAFF,

            ItemID.REVENANT_ETHER,
            ItemID.ANCIENT_EMBLEM,
            ItemID.ANCIENT_TOTEM,
            ItemID.ANCIENT_CRYSTAL,
            ItemID.ANCIENT_STATUETTE,
            ItemID.ANCIENT_MEDALLION,
            ItemID.ANCIENT_EFFIGY,
            ItemID.ANCIENT_RELIC,

            ItemID.PRAYER_POTION3,
            ItemID.PRAYER_POTION2,
            ItemID.PRAYER_POTION1,

            ItemID.STAMINA_POTION3,
            ItemID.STAMINA_POTION2,
            ItemID.STAMINA_POTION1,

            ItemID.SUPERCOMPOST,
            ItemID.WINE_OF_ZAMORAK,
            ItemID.WINE_OF_ZAMORAK_23489,
            ItemID.SANFEW_SERUM4,

            ItemID.DARK_CRAB,
            ItemID.SUPER_RESTORE4,
            ItemID.RUNE_PICKAXE,
            ItemID.RUNE_KNIFE,
            ItemID.RUNE_2H_SWORD,
            ItemID.CHAOS_RUNE,
            ItemID.BLOOD_RUNE,
            ItemID.DEATH_RUNE,
            ItemID.DIAMOND_BOLTS_E,
            ItemID.CANNONBALL,
            ItemID.UNCUT_DIAMOND,
            ItemID.GOLD_ORE,
            ItemID.MAGIC_LOGS,
            ItemID.LIMPWURT_ROOT,
            ItemID.ONYX_BOLT_TIPS,
            ItemID.RED_SPIDERS_EGGS,
            ItemID.UNCUT_DRAGONSTONE,
            ItemID.UNCUT_RUBY,
            ItemID.GRIMY_SNAPDRAGON,
            ItemID.UNICORN_HORN,
            ItemID.ANTIDOTE4_5952,
            ItemID.RANGING_POTION2,
            ItemID.MAGIC_SEED,
            ItemID.PALM_TREE_SEED,
            ItemID.COMBAT_POTION2,
            ItemID.TREASONOUS_RING,
            ItemID.VOIDWAKER_GEM,
            ItemID.FANGS_OF_VENENATIS,
            ItemID.AMULET_OF_GLORY,
            ItemID.RING_OF_WEALTH,
            ItemID.ABYSSAL_WHIP,
            ItemID.OBSIDIAN_CAPE,
            ItemID.RING_OF_THE_GODS,
            ItemID.VOIDWAKER_BLADE,
            ItemID.YEW_SEED,
            ItemID.VOIDWAKER_HILT,
            ItemID.VOIDWAKER,
            ItemID.RING_OF_DUELING8,
            ItemID.RING_OF_RECOIL,
            ItemID.EMERALD_RING,
            ItemID.SAPPHIRE_RING,
            ItemID.GRIMY_RANARR_WEED,
            ItemID.MORT_MYRE_FUNGUS,
            ItemID.REVENANT_ETHER,
            ItemID.BLIGHTED_ENTANGLE_SACK,
            ItemID.BLIGHTED_ANCIENT_ICE_SACK,
            ItemID.BLIGHTED_TELEPORT_SPELL_SACK
    };

    // the loot we pick up
    public static final int[] PICKABLE_LOOT = new int[]{
            ItemID.BLIGHTED_ENTANGLE_SACK,
            ItemID.BLIGHTED_ANCIENT_ICE_SACK,
            ItemID.BLIGHTED_TELEPORT_SPELL_SACK,

            ItemID.REVENANT_ETHER,
            ItemID.DRAGON_MED_HELM,
            ItemID.DRAGON_PLATELEGS,
            ItemID.DRAGON_PLATESKIRT,
            ItemID.DRAGON_LONGSWORD,
            ItemID.DRAGON_DAGGER,

            ItemID.RUNE_FULL_HELM,
            ItemID.BRACELET_OF_ETHEREUM_UNCHARGED,
            ItemID.RUNE_PLATELEGS,
            ItemID.RUNE_PLATEBODY,
            ItemID.RUNE_WARHAMMER,
            ItemID.RUNE_KITESHIELD,
            ItemID.BATTLESTAFF,

            ItemID.REVENANT_ETHER,
            ItemID.ANCIENT_EMBLEM,
            ItemID.ANCIENT_TOTEM,
            ItemID.ANCIENT_CRYSTAL,
            ItemID.ANCIENT_STATUETTE,
            ItemID.ANCIENT_MEDALLION,
            ItemID.ANCIENT_EFFIGY,
            ItemID.ANCIENT_RELIC,
            ItemID.DRAGON_2H_SWORD,
            ItemID.DRAGON_PICKAXE,
            ItemID.SUPER_RESTORE4,
            ItemID.VOIDWAKER_GEM,
            ItemID.FANGS_OF_VENENATIS,
            ItemID.TREASONOUS_RING,

            ItemID.PRAYER_POTION3,
            ItemID.PRAYER_POTION2,
            ItemID.PRAYER_POTION1,

            ItemID.STAMINA_POTION3,
            ItemID.STAMINA_POTION2,
            ItemID.STAMINA_POTION1,

            ItemID.RANGING_POTION3,
            ItemID.RANGING_POTION2,
            ItemID.RANGING_POTION1,

            ItemID.DARK_CRAB,
            ItemID.SUPER_RESTORE4,
            ItemID.RUNE_PICKAXE,
            ItemID.RUNE_KNIFE,
            ItemID.RUNE_2H_SWORD,
            ItemID.CHAOS_RUNE,
            ItemID.BLOOD_RUNE,
            ItemID.DEATH_RUNE,
            ItemID.DIAMOND_BOLTS_E,
            ItemID.CANNONBALL,
            ItemID.UNCUT_DIAMOND,
            ItemID.GOLD_ORE,
            ItemID.MAGIC_LOGS,
            ItemID.LIMPWURT_ROOT,
            ItemID.ONYX_BOLT_TIPS,
            ItemID.RED_SPIDERS_EGGS,
            ItemID.UNCUT_DRAGONSTONE,
            ItemID.UNCUT_RUBY,
            ItemID.GRIMY_SNAPDRAGON,
            ItemID.UNICORN_HORN,
            ItemID.ANTIDOTE4_5952,
            ItemID.MAGIC_SEED,
            ItemID.PALM_TREE_SEED,
            ItemID.BLIGHTED_SUPER_RESTORE3,
            ItemID.BLIGHTED_SUPER_RESTORE4,
            ItemID.COMBAT_POTION2,
            ItemID.COINS_995
    };

    public static final List<Integer> droppables = Arrays.asList(
            ItemID.VIAL,
            ItemID.BLIGHTED_MANTA_RAY
    );

    private static boolean shouldUseLootingBag() {
        return SettingsRepository.findInstanceOf(new RevenantSettings()).useLootingBag;
    }

    public static Filter<GroundItem> lootFilter = x -> {
        if (droppables.contains(x.getId())) return false;
        if (!SettingsRepository.findInstanceOf(new RevenantSettings()).targetRevenant.getArea().contains(x))
            return false;
        if (shouldUseLootingBag() && x.getId() == ItemID.LOOTING_BAG_CLOSED) return true;
        if (x.getAmount() * x.getItem().getLivePrice() > SettingsRepository.findInstanceOf(new RevenantSettings()).minLootValue)
            return true;
        if (Arrays.stream(PICKABLE_LOOT).anyMatch(i -> i == x.getId())) return true;
        return false;
    };

    Timer interactTimer = new Timer(800);

    @Override
    public boolean isValid() {
        GroundItem loot = GroundItems.closest(lootFilter);
        return loot != null;
    }

    @Override
    public int onLoop() {
        GroundItem loot = GroundItems.closest(lootFilter);
        if (loot == null || !loot.exists()) {
            Logger.info("No loot");
            return ReactionGenerator.getQuick();
        }

        if (Inventory.isFull() && !droppables.contains(loot.getId()) && Inventory.contains(x -> droppables.contains(x.getId()))) {
            Logger.info("Drop something to make space for loot");
            Inventory.drop(x -> droppables.contains(x.getId()));
            return ReactionGenerator.getQuick();
        }

        if (Inventory.isFull()) {
            Logger.info("Force leave");
            ExitRevs.setForceLeave(true);
            return ReactionGenerator.getQuick();
        }

        if (!Walking.isRunEnabled() && Walking.getRunEnergy() >= 5) {
            Walking.toggleRun();
        }

        if (interactTimer.finished()) {
            interactTimer.reset();
            loot.interact("Take");
        }
        return ReactionGenerator.getQuick();
    }
}
