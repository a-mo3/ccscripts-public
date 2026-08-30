package org.dreambot.behaviour.training.cooking;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class RoguesDenCook extends Fractal {
    final int rawID;
//    final int cookedID;

    public RoguesDenCook(Supplier<Boolean> acceptCondition, int rawID, int refill, int cookedID) {
        super(acceptCondition);
        this.rawID = rawID;
//        this.cookedID = cookedID;


        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(rawID, 1, 28)
                .setRefill(refill);
    }

    final Area ROGUES_FIRE = new Area(3044, 4976, 3040, 4968, 1);

    @Override
    public int onLoop() {
        if (!ROGUES_FIRE.contains(Players.getLocal())) {
            log("Walking to rogues fire");
            if (Walking.shouldWalk()) Walking.walk(ROGUES_FIRE);
            return ReactionGenerator.getNormal();
        }

        if (ItemProcessing.isOpen()) {
            log("Make all ");
            ItemProcessing.makeAll(rawID);
            Sleep.sleepUntil(() -> !Inventory.contains(rawID), () -> Players.getLocal().isAnimating(), 2000, 100);
            return ReactionGenerator.getNormal();
        }

        GameObject fire = GameObjects.closest("Fire");
        Item rawItem = Inventory.get(rawID);
        if (fire == null) {
            log("Cant find fire");
            return ReactionGenerator.getNormal();
        }

        if (rawItem == null) {
            log("Cant find raw fire");
            return ReactionGenerator.getNormal();
        }

        log("Cooking " + rawID);
        rawItem.useOn(fire);
        Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
        return ReactionGenerator.getNormal();
    }
}
