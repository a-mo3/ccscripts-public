package org.dreambot.behaviour.training.hunter;


import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class HunterBranch extends Fractal {
    public HunterBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Hunter");
        addChildren(
                new QuizBranch().setSimpleName("Quiz"),
                new CopperLongtails().setSimpleName("Copper Longtails"),
                new Falconry(() -> true).setSimpleName("Falconry")
        );
    }

    public HunterBranch() {
        setSimpleName("Hunter");
        addChildren(
                new QuizBranch().setSimpleName("Quiz"),
                new CopperLongtails().setSimpleName("Copper Longtails"),
                new Falconry(() -> Skills.getRealLevel(Skill.HUNTER) < 73).setSimpleName("Falconry")
        );
    }
}
