package org.dreambot.behaviour.method.gwd.nex.frozendoor;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * key pieces are always lost on death so we use to to be sure we always tp out after a boss fight
 */
public class DepositKeyPiece extends Fractal {
    public DepositKeyPiece() {
        super(() -> Inventory.contains(ItemID.FROZEN_KEY_PIECE_ARMADYL, ItemID.FROZEN_KEY_PIECE_ZAMORAK, ItemID.FROZEN_KEY_PIECE_BANDOS, ItemID.FROZEN_KEY_PIECE_SARADOMIN));
    }

    @Override
    public int onLoop() {
        if (Players.getLocal().getY() > 4000) {
            Item glory = Equipment.get(x -> ItemVariants.AMULET_OF_GLORY.contains(x.getId()));
            if (glory != null) {
                glory.interact("Edgeville");
            }

            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getNormal();
        }

        new BankAllInventoryEvent().execute();

        return ReactionGenerator.getNormal();
    }
}
