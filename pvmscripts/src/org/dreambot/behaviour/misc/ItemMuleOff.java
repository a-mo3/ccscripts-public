package org.dreambot.behaviour.misc;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * Mule off the Cannonballs and request gold from the mule
 */
public class ItemMuleOff extends Fractal {
    Area GE = BankLocation.GRAND_EXCHANGE.getArea(8);

    @Override
    public boolean isValid() {
        return OwnedItems.contains(ItemID.CANNONBALL)
                && OwnedItems.count(ItemID.STEEL_BAR) < 27
                && !Inventory.contains(ItemID.STEEL_BAR)
                && Bank.getLastBankHistoryCacheTime() > 1;
    }

    @Override
    public int onLoop() {
        if (!GE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(GE);
            return ReactionGenerator.getNormal();
        }

        int requiredGP = 9;

        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.CANNONBALL, OwnedItems.count(ItemID.CANNONBALL))
                .addRequiredItem(ItemID.COINS_995, requiredGP)
                .execute()
        ;
        return ReactionGenerator.getNormal();
    }
}
