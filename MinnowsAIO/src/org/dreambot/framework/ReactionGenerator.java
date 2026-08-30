package org.dreambot.framework;

import org.dreambot.api.methods.Calculations;

public class ReactionGenerator {
    public static int getNormal() {
        return 600 * Calculations.random(1, 4);
    }
}
