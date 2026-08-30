package org.dreambot.behaviour.training.smithing;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.Smithing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class AnvilSmithing extends Fractal {
    Area ANVIL = new Tile(3187, 3426).getArea(1);
    final int productId;
    // sleep until you have no more of this
    final int supply;

    public AnvilSmithing(Supplier<Boolean> acceptCondition, int productId, int supply) {
        super(acceptCondition);
        this.productId = productId;
        this.supply = supply;
    }

    @Override
    public int onLoop() {
        if (!ANVIL.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(ANVIL);
            return ReactionGenerator.getNormal();
        }

        if (!Smithing.isOpen()) {
            GameObject anvil = GameObjects.closest("Anvil");
            if (anvil == null) {
                Logger.info("Failed to find anvil");
                return ReactionGenerator.getNormal();
            }

            anvil.interact("Smith");
            Sleep.sleepUntil(Widgets::isOpen, 6400);
            return ReactionGenerator.getNormal();
        }

        // this will have no action
        Smithing.makeAll(productId);
        Sleep.sleepUntil(() -> !Inventory.contains(supply), () -> Players.getLocal().isAnimating(), 4400, 100);
        return ReactionGenerator.getNormal();
    }
}
