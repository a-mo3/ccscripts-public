package org.dreambot.behaviour.training.agility;


import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GnomeFractal extends Fractal {
    final Area GNOME_AGIL_COURSE = new Area(2464, 3444, 2492, 3410);
    final Area LOG_SIDE = new Area(2470, 3437, 2478, 3422, 0);
    final Area LOG_SIDE_TREE = new Area(2472, 3421, 2477, 3418, 2);
    final Area UNDER_TREE = new Area(2489, 3418, 2481, 3426, 0);

    List<AgilityStage> stages = Arrays.asList(
            new AgilityStage(
                    () -> PlayerSettings.getBitValue(13140) == 0,
                    () -> !Players.getLocal().isMoving(),
                    () -> GameObjects.closest("Log balance"),
                    "Walk-across"
            ),
            new AgilityStage(
                    () -> !Players.getLocal().isMoving() && LOG_SIDE.contains(Players.getLocal()),
                    () -> Players.getLocal().getZ() != 0,
                    () -> GameObjects.closest("Obstacle net"),
                    "Climb-over"
            ),
            new AgilityStage(
                    () -> Players.getLocal().getZ() == 1,
                    () -> Players.getLocal().getZ() == 2,
                    () -> GameObjects.closest("Tree branch"),
                    "Climb"
            ),
            new AgilityStage(
                    () -> LOG_SIDE_TREE.contains(Players.getLocal()),
                    () -> !LOG_SIDE_TREE.contains(Players.getLocal()) && !Players.getLocal().isMoving(),
                    () -> GameObjects.closest("Balancing rope"),
                    "Walk-on"
            ),
            new AgilityStage(
                    () -> !LOG_SIDE_TREE.contains(Players.getLocal()) && Players.getLocal().getZ() == 2,
                    () -> Players.getLocal().getZ() != 2,
                    () -> GameObjects.closest("Tree branch"),
                    "Climb-down"
            ),
            new AgilityStage(
                    () -> UNDER_TREE.contains(Players.getLocal()),
                    () -> !UNDER_TREE.contains(Players.getLocal()),
                    () -> GameObjects.closest("Obstacle net"),
                    "Climb-over"
            ),
            new AgilityStage(
                    () -> Players.getLocal().getZ() == 0
                            && !LOG_SIDE.contains(Players.getLocal())
                            && !UNDER_TREE.contains(Players.getLocal()),
                    () -> PlayerSettings.getBitValue(13140) == 0,
                    () -> GameObjects.closest(23138), // thats the tunnel
                    "Squeeze-through"
            )
    );

    public GnomeFractal(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    public GnomeFractal() {
    }

    @Override
    public boolean isValid() {
        return Skills.getRealLevel(Skill.AGILITY) < 10;
    }

    @Override
    public int onLoop() {
        if (!AreaUtils.containsIgnorePlane(GNOME_AGIL_COURSE, Players.getLocal().getTile())) {
            if (Walking.shouldWalk(8)) Walking.walk(GNOME_AGIL_COURSE.getCenter());
            return ReactionGenerator.getNormal();
        }

        for (AgilityStage stage : stages) {
            if (stage.isValid()) return stage.onLoop();
        }
        return ReactionGenerator.getNormal();
    }
}
