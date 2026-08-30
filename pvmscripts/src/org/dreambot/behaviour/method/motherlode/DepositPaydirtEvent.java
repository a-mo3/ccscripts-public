package org.dreambot.behaviour.method.motherlode;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Locatable;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

public class DepositPaydirtEvent extends AbstractResponseEvent<DepositPaydirtEvent.Response> {
    Timer timeout = new Timer(35 * 1000);

    enum Response {
        DEPOSITED,
        TIMEOUT
    }


    Area HOPPER_AREA = new Area(3747, 5674, 3752, 5670);

    @Override
    public int onLoop() {
        if (timeout.finished()) {
            setResponse(Response.TIMEOUT);
            return ReactionGenerator.getNormal();
        }

        int oreInSack = PlayerSettings.getBitValue(MLMMining.ORE_IN_SACK_VARBIT);
        if (oreInSack > 54) {
            Log.info("Empty event: " + new EmptyBagEvent().executed());
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(EmptyBagEvent.ORE_FILTER)) {
            if (!Bank.isOpen() && Walking.shouldWalk(6)) {
                Bank.open(BankLocation.MOTHERLODE_MINE);
                return ReactionGenerator.getNormal();
            }

            Bank.depositAll(EmptyBagEvent.ORE_FILTER);
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.contains(ItemID.PAYDIRT)) {
            setResponse(Response.DEPOSITED);
            return ReactionGenerator.getNormal();
        }

        GameObject topLadder = GameObjects.closest(MLMTopFloor.UPPER_LEVEL_LADDER);
        if (topLadder != null && topLadder.getSurrounding().stream().anyMatch(Locatable::canReach)) {
//            GameObject topHopper = GameObjects.closest("Hopper");
//            if (topHopper != null) topHopper.interact("Deposit");
            topLadder.interact("Climb");
            Sleep.sleepUntil(() -> topLadder.getSurrounding().stream().noneMatch(Locatable::canReach), 2400);
            return ReactionGenerator.getNormal();
        }

        int brokenStruts = GameObjects.all("Broken strut").size();
        if (brokenStruts > 1 && MLMMining.MLM_INNER.contains(Players.getLocal())) {
            Logger.info("Fixing wheel");
            Logger.info("Wheel event " + new FixWheelEvent().executed());
            return ReactionGenerator.getNormal();
        }

        if (!HOPPER_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(HOPPER_AREA);
            return ReactionGenerator.getQuick();
        }

        GameObject hopper = GameObjects.closest("Hopper");
        if (hopper != null) {
            hopper.interact("Deposit");
        }

        return ReactionGenerator.getNormal();
    }

}
