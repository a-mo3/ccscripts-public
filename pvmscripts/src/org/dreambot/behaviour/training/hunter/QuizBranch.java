package org.dreambot.behaviour.training.hunter;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.quests.quizbranch.RewardLeaf;
import org.dreambot.behaviour.quests.quizbranch.SolveLeaf;
import org.dreambot.behaviour.quests.quizbranch.StartLeaf;
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
