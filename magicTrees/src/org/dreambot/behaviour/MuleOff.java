package org.dreambot.behaviour;


import lombok.SneakyThrows;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
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

    public MuleOff(int hoursUntilMuleOff, int moneyLeftAfterMuling) {
        this.hoursUntilMuleOff = hoursUntilMuleOff;
        this.moneyLeftAfterMuling = moneyLeftAfterMuling;
    }

    @Override
    public boolean isValid() {
        if (timer == null) timer = new Timer((long) hoursUntilMuleOff * 1000 * 60 * 60);
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
            ItemID.YEW_LOGS,
            ItemID.MAGIC_LOGS,
            ItemID.OAK_LOGS,
            ItemID.LOGS
    };
}
