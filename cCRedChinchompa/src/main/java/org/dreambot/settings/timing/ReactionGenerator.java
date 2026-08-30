package org.dreambot.settings.timing;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.methods.Calculations;

public class ReactionGenerator {
    @Setter
    @Getter
    private static ReactionSettings reactionSettings = new ReactionSettings();

    public static int getQuick() {
        return Calculations.random(reactionSettings.getQuickLow(), reactionSettings.getQuickHigh());
    }

    public static int getNormal() {
        return Calculations.random(
                reactionSettings.getNormalLow(), reactionSettings.getNormalHigh());
    }

    public static int getLong() {
        return Calculations.random(
                reactionSettings.getLongLow(), reactionSettings.getLongHigh());
    }

    private static double clamp(double val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
}

