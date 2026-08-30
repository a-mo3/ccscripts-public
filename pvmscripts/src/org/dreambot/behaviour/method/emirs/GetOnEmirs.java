package org.dreambot.behaviour.method.emirs;

import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class GetOnEmirs extends Fractal {
    // just hop to a certain emirs world

    @Override
    public boolean isValid() {
        return Worlds.getCurrentWorld() != 578;
    }


    @Override
    public int onLoop() {
        WorldHopper.hopWorld(578);
        return ReactionGenerator.getNormal();
    }
}
