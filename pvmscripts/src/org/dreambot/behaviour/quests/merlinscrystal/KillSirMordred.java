package org.dreambot.behaviour.quests.merlinscrystal;

import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class KillSirMordred extends Fractal {
    public KillSirMordred(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Kill Mordred");
    }
}
