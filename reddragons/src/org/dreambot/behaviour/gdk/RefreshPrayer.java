package org.dreambot.behaviour.gdk;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class RefreshPrayer extends Fractal {
    Area HOS_ALTAR = new Area(1731, 3580, 1736, 3572);
    Area ALTAR_REGION = new Area(1765, 3607, 1694, 3555);

    @Override
    public boolean isValid() {
        return ALTAR_REGION.contains(Players.getLocal()) && Skills.getBoostedLevel(Skill.PRAYER) < Skills.getRealLevel(Skill.PRAYER) / 2;
    }

    @Override
    public int onLoop() {
        if (!HOS_ALTAR.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(HOS_ALTAR);
            return ReactionGenerator.getNormal();
        }

        GameObject altar = GameObjects.closest("Altar");
        if (altar != null && altar.interact("Pray")) {
            Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) == Skills.getRealLevel(Skill.PRAYER), 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
