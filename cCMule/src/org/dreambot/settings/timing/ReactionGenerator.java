package org.dreambot.settings.timing;

import org.dreambot.api.methods.Calculations;

public class ReactionGenerator {

    public static int getQuick() {
        return Calculations.random(100, 250);
    }

    public static int getNormal() {
        return Calculations.random(400, 1200);
    }


    private static double clamp(double val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
}

