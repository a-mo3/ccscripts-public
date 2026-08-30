package org.dreambot.behaviour.training.agility;


import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CanifisFractal extends Fractal {
    public CanifisFractal(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        CanafisAgililtyWebnodes.create();
    }

    public CanifisFractal() {
        CanafisAgililtyWebnodes.create();
    }

    Area finalRoof = new Tile(3510, 3476, 2).getArea(5);

    @Override
    public int onLoop() {
        if (!finalRoof.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(finalRoof);
        } else {
            GameObject exit = GameObjects.closest("Gap");
            if (exit != null && exit.interact("Jump")) {
                Sleep.sleepUntil(() -> Players.getLocal().getZ() == 0, 5400);
            }
        }
        return ReactionGenerator.getNormal();
    }
}