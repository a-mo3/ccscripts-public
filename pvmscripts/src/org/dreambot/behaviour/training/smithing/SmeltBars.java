package org.dreambot.behaviour.training.smithing;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class SmeltBars extends Fractal {
    final Area EDGEVILLE_FURNACE = new Area(3089, 3504, 3111, 3494);
    final int barId;

    public SmeltBars(Supplier<Boolean> acceptCondition, int barId) {
        super(acceptCondition);
        this.barId = barId;
    }

    @Override
    public int onLoop() {
        if (ItemProcessing.isOpen()) {
            ItemProcessing.makeAll(barId);
            Sleep.sleepUntil(Inventory::isEmpty, () -> Players.getLocal().isAnimating(), 1400, 100);
            return ReactionGenerator.getNormal();
        }

        if (!EDGEVILLE_FURNACE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(EDGEVILLE_FURNACE);
            return ReactionGenerator.getNormal();
        }

        GameObject furance = GameObjects.closest("Furnace");
        if (furance != null && furance.interact()) {
            Sleep.sleepUntil(ItemProcessing::isOpen, 4400);
        }

        return ReactionGenerator.getNormal();
    }
}
