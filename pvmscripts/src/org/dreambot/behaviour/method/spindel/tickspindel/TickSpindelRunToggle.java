package org.dreambot.behaviour.method.spindel.tickspindel;

import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.TickDecision;

public class TickSpindelRunToggle extends TickDecision {
    public TickSpindelRunToggle() {
        setSimpleName("Tick spindel run toggle");
    }

    @Override
    public boolean evaluate() {
        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 5) {
            log("Toggle run");
            Walking.toggleRun();
        }
        return false;
    }
}
