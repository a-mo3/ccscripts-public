package org.dreambot.behaviour.method.rdk;

import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * kill reds without safespot
 */
public class KillReds extends Fractal {
    @Override
    public int onLoop() {


        return ReactionGenerator.getNormal();
    }
}
