package org.dreambot.behaviour;

import org.dreambot.Filler;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * Mule off the Cannonballs and request gold from the mule
 */
public class ItemMuleOff extends Fractal {
    public static Timer timer;
    Area GE = BankLocation.GRAND_EXCHANGE.getArea(8);

    @Override
    public boolean isValid() {
        if (timer == null)
            timer = new Timer((long) ScriptSettings.getSettingsData().hoursUntilMuleOff * 1000 * 60 * 60);
        if (!Filler.isUnrestricted()) return false;
        return !Combat.isInWild() && ScriptSettings.getSettingsData().itemMuleOff && timer.finished();
    }

    @Override
    public int onLoop() {
        if (!GE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(GE);
            return ReactionGenerator.getNormal();
        }


        if (!Bank.isCached()) {
            Bank.open();
            Bank.updateCache();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.JUG_OF_WATER)) {
            log("Deposit unnoted jugs");
            if (!Bank.open()) return ReactionGenerator.getNormal();
            Bank.depositAll(ItemID.JUG_OF_WATER);
            return ReactionGenerator.getNormal();
        }

        if (Bank.contains(ItemID.JUG_OF_WATER)) {
            log("Get all noted jugs");
            if (!Bank.open()) return ReactionGenerator.getNormal();
            if (Bank.getWithdrawMode() != BankMode.NOTE) {
                Bank.setWithdrawMode(BankMode.NOTE);
                return ReactionGenerator.getNormal();
            }

            if (Inventory.contains(x -> x.getId() != ItemID.JUG_OF_WATER + 1)) {
                Bank.depositAll(x -> x.getId() != ItemID.JUG_OF_WATER + 1);
                return ReactionGenerator.getNormal();
            }

            Bank.withdrawAll(ItemID.JUG_OF_WATER);
            return ReactionGenerator.getNormal();

        }


        if (!Trade.isOpen()
                && !OwnedItems.contains(ItemID.JUG_OF_WATER) && !OwnedItems.contains(ItemID.JUG_OF_WATER + 1)) {
            timer.reset();
            return ReactionGenerator.getNormal();
        }
        int requiredGP = 9000;

        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.JUG_OF_WATER + 1, OwnedItems.count(ItemID.JUG_OF_WATER + 1))
//                .addRequiredItem(ItemID.COINS_995, requiredGP)
                .execute()
        ;
        return ReactionGenerator.getNormal();
    }
}
