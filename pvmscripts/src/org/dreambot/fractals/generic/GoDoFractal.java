package org.dreambot.fractals.generic;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * go somewhere then do an action, making for digging in x marks the spot might expand later
 */
public class GoDoFractal extends Fractal {
    public GoDoFractal(Supplier<Boolean> acceptCondition, Tile tile, Supplier<Integer> onLoopSupplier) {
        super(acceptCondition);
        this.onLoopSupplier = onLoopSupplier;
        this.tile = tile;
    }

    final Supplier<Integer> onLoopSupplier;
    final Tile tile;

    @Override
    public int onLoop() {
        if (!Players.getLocal().getTile().equals(tile)) {
            log("Walking to location");
            if (Walking.shouldWalk(6)) Walking.walkExact(tile);
            return ReactionGenerator.getNormal();
        }

        return onLoopSupplier != null ? onLoopSupplier.get() : ReactionGenerator.getNormal();
    }
}
