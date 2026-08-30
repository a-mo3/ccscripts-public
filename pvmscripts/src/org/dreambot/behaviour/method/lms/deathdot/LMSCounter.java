package org.dreambot.behaviour.method.lms.deathdot;

import org.dreambot.fractals.TickDecision;

public class LMSCounter extends TickDecision {
    // inc this when we do delaying actions like eating or attacking, dec once per tick
    public static int actionCounter = 0;
    // same thing but for the enemy
    public static int enemyActionCounter = 0;

    @Override
    public boolean evaluate() {
        if (actionCounter > 0) actionCounter--;
        if (enemyActionCounter > 0) enemyActionCounter--;
        return false;
    }
}
