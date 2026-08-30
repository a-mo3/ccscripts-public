package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
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
    Area GE = BankLocation.GRAND_EXCHANGE.getArea(8);

    @Override
    public boolean isValid() {
        return OwnedItems.count(ItemID.MAGIC_LOGS, true) + OwnedItems.count(ItemID.REDWOOD_LOGS, true) + OwnedItems.count(ItemID.YEW_LOGS, true) > ScriptSettings.getSettingsData().muleOffQuantity
                && Bank.getLastBankHistoryCacheTime() > 1;
    }

    @Override
    public int onLoop() {
        if (!GE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(GE);
            return ReactionGenerator.getNormal();
        }

        if (Bank.contains(ItemID.REDWOOD_LOGS, ItemID.MAGIC_LOGS, ItemID.YEW_LOGS)) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Bank.setWithdrawMode(BankMode.NOTE);

            Bank.withdrawAll(ItemID.REDWOOD_LOGS);
            Bank.withdrawAll(ItemID.MAGIC_LOGS);
            Bank.withdrawAll(ItemID.YEW_LOGS);
            return ReactionGenerator.getNormal();
        }

        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.REDWOOD_LOGS + 1, Inventory.count(ItemID.REDWOOD_LOGS))
                .addOfferedItem(ItemID.MAGIC_LOGS + 1, Inventory.count(ItemID.MAGIC_LOGS))
                .addOfferedItem(ItemID.YEW_LOGS + 1, Inventory.count(ItemID.YEW_LOGS))
                .execute()
        ;
        return ReactionGenerator.getNormal();
    }
}
