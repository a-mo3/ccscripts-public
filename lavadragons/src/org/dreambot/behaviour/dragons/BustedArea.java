package org.dreambot.behaviour.dragons;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * leave the area from ferox -> edgeville that dax tries to path through but lost doesnt handle
 */
public class BustedArea extends Fractal {
    final Area SOUL_WARS_PORTAL = new Area(2202, 2863, 2218, 2854);
    final Area UNDER_FEROX = new Area(3150, 10045, 3178, 10020);

    @Override
    public boolean isValid() {
        Player lp = Players.getLocal();
        return SOUL_WARS_PORTAL.contains(lp) || UNDER_FEROX.contains(lp);
    }

    @Override
    public int onLoop() {
        Magic.castSpell(Normal.HOME_TELEPORT);
        Player lp = Players.getLocal();
        Sleep.sleepUntil(() -> !SOUL_WARS_PORTAL.contains(lp) && !UNDER_FEROX.contains(lp), 60_000);
        return ReactionGenerator.getNormal();
    }
}
