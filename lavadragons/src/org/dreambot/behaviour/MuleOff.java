package org.dreambot.behaviour;


import lombok.SneakyThrows;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.DecantEvent;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.text.DecimalFormat;

/**
 * Mule off after x hours
 */
public class MuleOff extends Fractal {
    public static Timer timer;
    final DecimalFormat DF = new DecimalFormat("###,###,###");

    @Override
    public boolean isValid() {
        if (timer == null)
            timer = new Timer((long) ScriptSettings.getSettingsData().hoursUntilMuleOff * 1000 * 60 * 60);
        return !Combat.isInWild() && timer.finished();
    }

    @SneakyThrows
    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.open()) Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (DecantEvent.shouldDecant() && ScriptSettings.getSettingsData().decantpotions) {
            Logger.info(new DecantEvent().executed());
        }

        if (OwnedItems.containsAnyUnworn(LOOT)) {
            Logger.info("Selling all items");
            new SellAllEvent(LOOT)
                    .setInterruptCondition(Combat::isInWild)
                    .execute();
            return ReactionGenerator.getNormal();
        }

        if (!Trade.isOpen()
                && OwnedItems.count(ItemID.COINS_995) <= ScriptSettings.getSettingsData().moneyLeftAfterMuling) {
            timer.reset();
            return ReactionGenerator.getNormal();
        }

        int quantity = OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getSettingsData().moneyLeftAfterMuling;
        Logger.info("Making mule request " + quantity);
        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.COINS_995, quantity)
                .execute();
        return ReactionGenerator.getNormal();
    }

    private static final int[] LOOT = new int[]{
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

            ItemID.DRAGON_JAVELIN_HEADS,
            ItemID.FIRE_ORB,
            ItemID.ADAMANTITE_BAR,
            ItemID.LOOP_HALF_OF_KEY,
            ItemID.TOOTH_HALF_OF_KEY,
            ItemID.AMULET_OF_GLORY,

            ItemID.GRIMY_DWARF_WEED,
            ItemID.GRIMY_AVANTOE,
            ItemID.GRIMY_CADANTINE,
            ItemID.STEEL_ARROW,
            ItemID.RUNE_ARROW,
            ItemID.SILVER_ORE,
            ItemID.FIRE_TALISMAN,
            ItemID.DRAGON_MED_HELM,
            ItemID.DRAGON_SPEAR,
            ItemID.DRAGONSTONE,
            ItemID.UNCUT_DIAMOND,
            ItemID.UNCUT_RUBY,
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
            ItemID.RING_OF_RECOIL,
            ItemID.DRACONIC_VISAGE
    };
}
