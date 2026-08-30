package org.dreambot.behaviour.training.farming;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.training.farming.tithe.TitheFarmBranch;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class FarmingBranch extends Fractal {
    public FarmingBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        setSimpleName("Farming");

        addChildren(
                new BaggedPlants(() -> Skills.getRealLevel(Skill.FARMING) < 34)
                        .setSimpleName("Bagged plants until 34"),
                new TitheFarmBranch(() -> true)
        );
    }
}
