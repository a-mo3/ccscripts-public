package org.dreambot.behaviour.quests.observatory;

import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * prod and kill the sleeping guard
 */
public class ObservatoryPassGuard extends Fractal {
    @Override
    public int onLoop() {
        return ReactionGenerator.getNormal();
    }
}
