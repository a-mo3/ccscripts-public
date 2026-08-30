package org.dreambot.behaviour.method.huey;

import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.TickDecision;

public class HueyToggleRun extends TickDecision {

    @Override
    public boolean evaluate() {
        if (Walking.getRunEnergy() > 10 && !Walking.isRunEnabled()){
            log("Toggle run");
            Walking.toggleRun();
        }
        return false;
    }
}
