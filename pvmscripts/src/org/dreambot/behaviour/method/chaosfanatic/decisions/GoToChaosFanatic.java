package org.dreambot.behaviour.method.chaosfanatic.decisions;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.TickDecision;

public class GoToChaosFanatic extends TickDecision {
    Area FANATIC_AREA = new Area(2986, 3851, 2969, 3839);

    @Override
    public boolean evaluate() {
        if (FANATIC_AREA.contains(Players.getLocal())) return false;

        // todo maybe eat

        if (Walking.shouldWalk()) Walking.walk(FANATIC_AREA);
        return true;
    }
}
