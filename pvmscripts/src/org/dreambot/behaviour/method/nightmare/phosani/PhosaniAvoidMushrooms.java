package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class PhosaniAvoidMushrooms extends Fractal {
    private static final int NIGHTMARE_MUSHROOM = 37739;

    @Override
    public boolean isValid() {
        GameObject mushroom = GameObjects.closest(NIGHTMARE_MUSHROOM);
        return mushroom != null && mushroom.getSurroundingArea(1).contains(Players.getLocal());
    }

    @Override
    public int onLoop() {
        // walk away
        GameObject mushroom = GameObjects.closest(NIGHTMARE_MUSHROOM);
        // idk this was just on lost
        Walking.walkExact(Players.getLocal().getTile().translate(
                (mushroom.getX() - Players.getLocal().getX()) * -1,
                (mushroom.getY() - Players.getLocal().getY()) * -1
        ));
        return ReactionGenerator.getQuick();
    }
}
