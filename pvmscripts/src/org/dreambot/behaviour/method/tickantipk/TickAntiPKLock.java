package org.dreambot.behaviour.method.tickantipk;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.fractals.TickDecision;

public class TickAntiPKLock extends TickDecision {
    @Override
    public boolean evaluate() {
        TickAntiPKBranch.lock = Combat.isInWild();
        return false;
    }
}
