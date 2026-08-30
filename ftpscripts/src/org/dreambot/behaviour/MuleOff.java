package org.dreambot.behaviour;


import lombok.SneakyThrows;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.settings.Varcs;
import org.dreambot.api.methods.skills.Skills;
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
import org.dreambot.settings.timing.ReactionGenerator;

import java.text.DecimalFormat;

/**
 * Mule off after x hours
 */
public class MuleOff extends Fractal {
    public static Timer timer;
    final DecimalFormat DF = new DecimalFormat("###,###,###");
    final int hoursUntilMuleOff;
    final int moneyLeftAfterMuling;

    final static int PLAY_TIME_VARCINT = 526;

    public static boolean isUnrestricted() {
        return Skills.getTotalLevel() >= 100 && Quests.getQuestPoints() >= 10 && Varcs.getInt(PLAY_TIME_VARCINT) >= 1200;
    }

    public MuleOff(int hoursUntilMuleOff, int moneyLeftAfterMuling) {
        this.hoursUntilMuleOff = hoursUntilMuleOff;
        this.moneyLeftAfterMuling = moneyLeftAfterMuling;
    }

    @Override
    public boolean isValid() {
        if (timer == null) timer = new Timer((long) hoursUntilMuleOff * 1000 * 60 * 60);
        if (!isUnrestricted()) return false;
        return !Combat.isInWild() && timer.finished();
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

        if (OwnedItems.containsAnyUnworn(LOOT)) {
            Logger.info("Selling all items");
            new SellAllEvent(LOOT)
                    .setInterruptCondition(Combat::isInWild)
                    .execute();
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.isReadyToCollect()) {
            log("Collect from ge");
            if (!GrandExchange.isOpen()) {
                if (Walking.shouldWalk()) GrandExchange.open();
                return ReactionGenerator.getNormal();
            }
            GrandExchange.collect();
            return ReactionGenerator.getNormal();
        }

        if (!Trade.isOpen()
                && OwnedItems.count(ItemID.COINS_995) <= moneyLeftAfterMuling) {
            timer.reset();
            return ReactionGenerator.getNormal();
        }

        Logger.info("Making mule request");
        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995) - moneyLeftAfterMuling)
                .execute();
        return ReactionGenerator.getNormal();
    }

    public static int[] LOOT = new int[]{
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
            ItemID.DRACONIC_VISAGE,

            ItemID.GRANITE_MAUL,
            ItemID.MYSTIC_ROBE_TOP,
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
            ItemID.LOGS,
            ItemID.STEEL_BAR,
            ItemID.GOLD_BAR,
            ItemID.MITHRIL_BAR,
            ItemID.RUNITE_ORE
    };
}
