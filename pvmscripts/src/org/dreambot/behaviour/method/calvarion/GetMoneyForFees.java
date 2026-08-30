package org.dreambot.behaviour.method.calvarion;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.method.antipk.AntiPkLeaveBosses;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import static org.dreambot.behaviour.misc.MuleOff.LOOT;

/**
 * exits wild, gets gp to pay the entrance fee
 */
public class GetMoneyForFees extends Fractal {
    public GetMoneyForFees(int coinReq) {
        super(() -> Bank.isCached() && OwnedItems.count(ItemID.COINS_995) < coinReq);
    }

    public GetMoneyForFees() {
        super(() -> Bank.isCached() && OwnedItems.count(ItemID.COINS_995) < 50_000);
    }


    @Override
    public int onLoop() {
        if (Combat.isInWild()) {
            Logger.info("Getting out of wild");
            return AntiPkLeaveBosses.leaveBosses();
        }

        if (OwnedItems.containsAnyUnworn(LOOT)) {
            Logger.info("Selling all items");
            new SellAllEvent(LOOT)
                    .setInterruptCondition(Combat::isInWild)
                    .execute();
            return ReactionGenerator.getNormal();
        }

        Logger.info("Making mule request");
        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addRequiredItem(ItemID.COINS_995, 350_000)
                .execute();
        return ReactionGenerator.getNormal();
    }
}
