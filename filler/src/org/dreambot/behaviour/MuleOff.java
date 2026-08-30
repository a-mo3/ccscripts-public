package org.dreambot.behaviour;


import lombok.SneakyThrows;
import org.dreambot.Filler;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
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

    @Override
    public boolean isValid() {
        if (timer == null)
            timer = new Timer((long) ScriptSettings.getSettingsData().hoursUntilMuleOff * 1000 * 60 * 60);
        if (!Filler.isUnrestricted()) return false;
        return !Combat.isInWild() && timer.finished();
    }

    @SneakyThrows
    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.open()) Bank.close();
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
                && OwnedItems.count(ItemID.COINS_995) <= ScriptSettings.getSettingsData().moneyLeftAfterMuling) {
            timer.reset();
            return ReactionGenerator.getNormal();
        }

        Logger.info("Making mule request");
        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
//                .addOfferedItem(ItemID.JUG_OF_WATER + 1, OwnedItems.count(ItemID.JUG_OF_WATER, true))
                .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getSettingsData().moneyLeftAfterMuling)
                .execute();
        return ReactionGenerator.getNormal();
    }

    public static final int[] LOOT = new int[]{
            ItemID.JUG_OF_WATER,
            ItemID.BUCKET_OF_WATER,
            ItemID.BOWL_OF_WATER,
            ItemID.VIAL_OF_WATER,
            ItemID.RAW_SHRIMPS,
            ItemID.RAW_ANCHOVIES,
            ItemID.LOGS,
            ItemID.OAK_LOGS,
            ItemID.COAL,
            ItemID.IRON_ORE,
            ItemID.LOBSTER,
            ItemID.IRON_KITESHIELD,
            ItemID.IRON_PLATEBODY,
            ItemID.IRON_PLATELEGS,
    };
}
