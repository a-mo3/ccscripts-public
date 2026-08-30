package org.dreambot.behaviour.method.motherlode;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
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

public class GetProspector extends Fractal {
    final boolean buyProspector;

    public GetProspector(boolean buyProspector) {
        this.buyProspector = buyProspector;
    }

    enum Prospector {
        HAT(40, ItemID.PROSPECTOR_HELMET),
        CHEST(60, ItemID.PROSPECTOR_JACKET),
        LEGS(50, ItemID.PROSPECTOR_LEGS),
        BOOTS(30, ItemID.PROSPECTOR_BOOTS);

        final int cost;
        final int id;

        Prospector(int cost, int id) {
            this.cost = cost;
            this.id = id;
        }
    }

    @Override
    public boolean isValid() {
        if (!buyProspector) return false;
        int ownedNugs = OwnedItems.count(ItemID.GOLDEN_NUGGET);
        return MLMMining.MLM_INNER.contains(Players.getLocal())
                && Bank.isCached()
                && Players.getLocal().getZ() == 0
                && Arrays.stream(Prospector.values())
                .anyMatch(x -> (ownedNugs >= x.cost || OwnedItems.contains(x.id)) && !Equipment.contains(x.id));
    }

    @Override
    public int onLoop() {
        int ownedNugs = OwnedItems.count(ItemID.GOLDEN_NUGGET);
        Prospector missingPiece = Arrays.stream(Prospector.values())
                .filter(x -> (ownedNugs >= x.cost || OwnedItems.contains(x.id)) && !Equipment.contains(x.id))
                .findFirst().orElse(null);


        if (missingPiece == null) {
            Logger.info("missing piece null");
            return ReactionGenerator.getNormal();
        }

        Logger.info("Equipping piece " + missingPiece);
        if (Inventory.contains(missingPiece.id)) {
            if (Widgets.isOpen()) {
                Widgets.closeAll();
            }

            Inventory.interact(missingPiece.id);
            return ReactionGenerator.getNormal();
        }

        if (Bank.contains(missingPiece.id)) {
            log("Get piece out of bank");
            if (Bank.open()) {
                Bank.withdraw(missingPiece.id);
            }
            return ReactionGenerator.getNormal();
        }

        if (Inventory.emptySlotCount() < 1) {
            Logger.info("Bank all");
            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.count(ItemID.GOLDEN_NUGGET) < missingPiece.cost) {
            Logger.info("Withdrawing nugs");
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Logger.info("Withdraw");
            Bank.withdrawAll(ItemID.GOLDEN_NUGGET);
            return ReactionGenerator.getNormal();
        }

        if (!Shop.isOpen()) {
            Logger.info("Opening shop");
            NPC percy = NPCs.closest("Prospector percy");
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
