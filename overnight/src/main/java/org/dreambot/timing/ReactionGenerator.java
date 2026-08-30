package org.dreambot.timing;

import lombok.Setter;
import org.dreambot.api.methods.Calculations;

public class ReactionGenerator {
    @Setter
    private static ReactionSettings reactionSettings = new ReactionSettings();

    public static int getQuick() {
        return (int) clamp((-Math.log(Math.abs(Calculations.nextGaussianRandom(0.0, 1.0)))) * 1 + 3,
                reactionSettings.getQuickLow(), reactionSettings.getQuickHigh());
    }

    public static int getNormal() {
        return (int) clamp((-Math.log(Math.abs(Calculations.nextGaussianRandom(0.0, 1.0)))) * 1 + 3,
                reactionSettings.getNormalLow(), reactionSettings.getNormalHigh());
    }

    public static int getLong() {
        return (int) clamp((-Math.log(Math.abs(Calculations.nextGaussianRandom(0.0, 1.0)))) * 1 + 3,
                reactionSettings.getLongLow(), reactionSettings.getLongHigh());
    }

    private static double clamp(double val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
}
