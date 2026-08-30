package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.text.DecimalFormat;

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

        int requiredGP = (int) (ScriptSettings.getSettingsData().steelBarRestockQuantity * LivePrices.get(ItemID.STEEL_BAR) * 1.2);

        DecimalFormat df = new DecimalFormat("###,###,###");
        Logger.info(String.format("Requesting %s GP to buy %s steel bars after muling, you can change this in /cCCannonballerAIO/settings.json",
                df.format(requiredGP),
                df.format(ScriptSettings.getSettingsData().steelBarRestockQuantity)
        ));
        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.CANNONBALL, OwnedItems.count(ItemID.CANNONBALL))
                .addRequiredItem(ItemID.COINS_995, requiredGP)
                .execute()
        ;
        return ReactionGenerator.getNormal();
    }
}
