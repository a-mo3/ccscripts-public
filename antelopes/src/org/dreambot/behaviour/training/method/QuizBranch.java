package org.dreambot.behaviour.training.method;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.training.method.quizbranch.RewardLeaf;
import org.dreambot.behaviour.training.method.quizbranch.SolveLeaf;
import org.dreambot.behaviour.training.method.quizbranch.StartLeaf;
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
        return Skills.getRealLevel(Skill.HUNTER) < 9;
    }
}
