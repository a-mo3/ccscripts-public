package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
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
    Area GE = BankLocation.GRAND_EXCHANGE.getArea(8);
    public static Timer muleTimer = new Timer((long) ScriptSettings.getSettingsData().hoursUntilMuleOff * 60 *  60 * 1000);

    @Override
    public boolean isValid() {
        return muleTimer.finished() && OwnedItems.contains(ItemID.RAW_ANGLERFISH) &&  Bank.isCached() ;
    }

    @Override
    public int onLoop() {
        if (!GE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(GE);
            return ReactionGenerator.getNormal();
        }

        int requiredGP = 10_000;

            new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.RAW_ANGLERFISH, OwnedItems.count(ItemID.RAW_ANGLERFISH))
                .addRequiredItem(ItemID.COINS_995, requiredGP)
                .execute()
        ;
        return ReactionGenerator.getNormal();
    }
}
