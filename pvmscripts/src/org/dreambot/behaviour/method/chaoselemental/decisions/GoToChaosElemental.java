package org.dreambot.behaviour.method.chaoselemental.decisions;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.TickDecision;

public class GoToChaosElemental extends TickDecision {
    Area ELEMENTAL_AREA =  new Area(
            new Tile(3260, 3948, 0),
            new Tile(3282, 3948, 0),
            new Tile(3274, 3939, 0),
            new Tile(3275, 3922, 0),
            new Tile(3275, 3904, 0),
            new Tile(3222, 3905, 0),
            new Tile(3217, 3911, 0),
            new Tile(3213, 3938, 0),
            new Tile(3224, 3934, 0),
            new Tile(3240, 3933, 0));

    @Override
    public boolean evaluate() {
        if (ELEMENTAL_AREA.contains(Players.getLocal())) return false;
        // todo maybe eat
        if (Walking.shouldWalk()) Walking.walk(ELEMENTAL_AREA);
        return true;
    }
}
