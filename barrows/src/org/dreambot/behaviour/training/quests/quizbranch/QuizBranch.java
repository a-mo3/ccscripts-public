package org.dreambot.behaviour.training.quests.quizbranch;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.Fractal;

public class QuizBranch extends Fractal {
    public QuizBranch() {
        addChildren(
                new RewardLeaf().setSimpleName("Reward"),
                new SolveLeaf().setSimpleName("Solve"),
                new StartLeaf().setSimpleName("Start")
        );
    }

    @Override
    public boolean isValid() {
        return Skills.getRealLevel(Skill.SLAYER) < 9;
    }
}
