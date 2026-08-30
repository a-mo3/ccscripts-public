package org.dreambot.behaviour.training.sailing.ticksalvage;

import org.dreambot.fractals.TickFractal;

import java.util.function.Supplier;

/**
 * you can 2t salvage with a certain fletch,
 * lowest would be teak with 46 fletching
 */
public class TickSalvageBranch extends TickFractal {
    public TickSalvageBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("2Tick salvage");
    }
}
