package org.dreambot.behaviour.method.motherlode;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Locatable;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.settings.timing.ReactionGenerator;

public class EmptyBagEvent extends AbstractResponseEvent<EmptyBagEvent.Response> {
    enum Response {
        EMPTY,
        TIMEOUT
    }

    Timer timeout = new Timer(5 * 60 * 1000);
    // ore gems and nuggets, anything you are meant to bank
    public static final Filter<Item> ORE_FILTER = x -> x.getId() != ItemID.PAYDIRT && !x.getName().contains("pickaxe");

    @Override
    public int onLoop() {
        if (timeout.finished()) {
            setResponse(Response.TIMEOUT);
            return ReactionGenerator.getNormal();
        }

        GameObject topLadder = GameObjects.closest(MLMTopFloor.UPPER_LEVEL_LADDER);
        if (topLadder != null && topLadder.getSurrounding().stream().anyMatch(Locatable::canReach)) {
            topLadder.interact("Climb");
            Sleep.sleepUntil(() -> topLadder.getSurrounding().stream().noneMatch(Locatable::canReach), 2400);
            return ReactionGenerator.getNormal();
        }

        if (!MLMMining.MLM_INNER.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(MLMMining.MLM_INNER);
            return ReactionGenerator.getNormal();
        }

        int oreInSack = PlayerSettings.getBitValue(MLMMining.ORE_IN_SACK_VARBIT);
        if (oreInSack == 0 && !Inventory.contains(ORE_FILTER)) {
            // pick up dirt you previously dropped
            GroundItem droppedDIrt = GroundItems.closest(ItemID.PAYDIRT);
            if (droppedDIrt != null) {
                droppedDIrt.interact("Take");
                Sleep.sleepUntil(() -> !droppedDIrt.exists(), 1400);
                return ReactionGenerator.getNormal();
            }

            setResponse(Response.EMPTY);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.PAYDIRT)) {
            if (Players.getLocal().isStandingStill()) Inventory.dropAll(ItemID.PAYDIRT);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ORE_FILTER)) {
            if (!Bank.isOpen() && Walking.shouldWalk(6)) {
                Bank.open(BankLocation.MOTHERLODE_MINE);
                return ReactionGenerator.getNormal();
            }

            Bank.depositAll(ORE_FILTER);
            return ReactionGenerator.getNormal();
        }

        GameObject sack = GameObjects.closest("Sack");
        if (sack != null) {
            sack.interact("Search");
            Sleep.sleepUntil(() -> Inventory.contains(ORE_FILTER), 2400);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
