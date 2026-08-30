package org.dreambot.behaviour.method.lms.deathdot;

import org.dreambot.fractals.TickDecision;

public class MoveDecision extends TickDecision {
    @Override
    public boolean evaluate() {
        // if its time to attack we shouldnt be moving until we have attacked
        if (LMSCounter.actionCounter < 1) return false;

        return false;
    }
}
