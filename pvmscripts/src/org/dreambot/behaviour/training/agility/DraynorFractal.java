package org.dreambot.behaviour.training.agility;


import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class DraynorFractal extends Fractal {
    public DraynorFractal(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        DraynorAgililtyWebnodes.create();
    }

    public DraynorFractal() {
        super(() -> Skills.getRealLevel(Skill.AGILITY) < 40);
        DraynorAgililtyWebnodes.create();
    }

    Area finalRoof = new Area(3096, 3261, 3101, 3256, 3);

    @Override
    public int onLoop() {
        if (!finalRoof.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(finalRoof);
        } else {
            GameObject exit = GameObjects.closest("Crate");
            if (exit != null && exit.interact("Climb-down")) {
                Sleep.sleepUntil(() -> Players.getLocal().getZ() == 0, 5400);
            }
        }
        return ReactionGenerator.getNormal();
    }
}
