package org.dreambot.behaviour.method.pirates;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class RechargeAtFerox extends Fractal {
    public RechargeAtFerox() {
        super(() -> !Combat.isInWild() && !prayerFull() && GameObjects.closest("Pool of Refreshment") != null);
    }

    @Override
    public int onLoop() {
        GameObject pool = GameObjects.closest("Pool of Refreshment");
        if (pool == null) {
            log("Can't find pool");
            return ReactionGenerator.getNormal();
        }

        if (pool.distance() > 8) {
            log("Walk closer to pool");
            if (Walking.shouldWalk()) Walking.walk(pool);
            return ReactionGenerator.getNormal();
        }

        if (pool.interact("Drink")) {
            log("Recharging prayer");
            Sleep.sleepUntil(RechargeAtFerox::prayerFull, 4200 + ReactionGenerator.getNormal());
        }
        return ReactionGenerator.getNormal();
    }

    private static boolean prayerFull() {
        return Skills.getBoostedLevel(Skill.PRAYER) >= Skills.getRealLevel(Skill.PRAYER);
    }
}
