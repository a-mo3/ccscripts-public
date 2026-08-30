package org.dreambot;

import org.dreambot.api.input.mouse.algorithm.MouseProfile;
import org.dreambot.api.methods.Calculations;


/**
 * fuck with mouse profile after playing the minigame
 */
public class CamelMouse {
    public static void modify() {
        MouseProfile.setAccelerationDeviation(withIn(MouseProfile.getAccelerationDeviation()));
        MouseProfile.setAccelerationRate(withIn(MouseProfile.getAccelerationRate()));
        MouseProfile.setAngleDeviationRate(withIn(MouseProfile.getAngleDeviationRate()));
        MouseProfile.setDecayDistanceExponent(withIn(MouseProfile.getDecayDistanceExponent()));
        MouseProfile.setDecelDecayRate(withIn(MouseProfile.getDecelDecayRate()));
    }

    private static double withIn(double a) {
        return Calculations.random((a * 0.9), (a * 1.1));
    }
}
