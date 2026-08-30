package org.dreambot.behaviour.training.agility;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class GetGraceful extends Fractal {
    enum Graceful {
        HAT(35, ItemID.GRACEFUL_HOOD),
        CHEST(55, ItemID.GRACEFUL_TOP),
        LEGS(60, ItemID.GRACEFUL_LEGS),
        BOOTS(40, ItemID.GRACEFUL_BOOTS),
        GLOVES(30, ItemID.GRACEFUL_GLOVES),
        CAPE(40, ItemID.GRACEFUL_CAPE),
        ;

        final int cost;
        final int id;

        Graceful(int cost, int id) {
            this.cost = cost;
            this.id = id;
        }
    }

    @Override
    public boolean isValid() {
        int ownedNugs = OwnedItems.count(ItemID.MARK_OF_GRACE);
        return Arrays.stream(Graceful.values())
                .anyMatch(x -> (ownedNugs >= x.cost || OwnedItems.contains(x.id)) && !Equipment.contains(x.id));
    }

    @Override
    public int onLoop() {
        int ownedNugs = OwnedItems.count(ItemID.MARK_OF_GRACE);
        Graceful missingPiece = Arrays.stream(Graceful.values())
                .filter(x -> (ownedNugs >= x.cost || OwnedItems.contains(x.id)) && !Equipment.contains(x.id))
                .findFirst().orElse(null);

        if (missingPiece == null) {
            Logger.info("missing piece null");
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(missingPiece.id)) {
            Logger.info("Equipping piece " + missingPiece);
            if (Widgets.isOpen()) {
                Widgets.closeAll();
            }

            Inventory.interact(missingPiece.id);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.emptySlotCount() < 1) {
            Logger.info("Bank all");
            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.count(ItemID.MARK_OF_GRACE) < missingPiece.cost) {
            Logger.info("Withdrawing mogs");
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Logger.info("Withdraw");
            Bank.withdrawAll(ItemID.MARK_OF_GRACE);
            return ReactionGenerator.getNormal();
        }

        if (!Shop.isOpen()) {
            Logger.info("Opening shop");
            NPC percy = NPCs.closest("Grace");
            if (percy != null && percy.interact("Trade")) {
                Sleep.sleepUntil(Shop::isOpen, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        Logger.info("buying piece " + missingPiece);
        Shop.purchase(missingPiece.id, 1);
        return ReactionGenerator.getNormal();
    }
}
