package org.dreambot.behaviour.training;


import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.quizbranch.QuizBranch;
import org.dreambot.fractals.Fractal;

public class HunterBranch extends Fractal {
    public HunterBranch() {
        addChildren(
                new QuizBranch().setSimpleName("Quiz"),
                new CopperLongtails().setSimpleName("Copper Longtails"),
                new Falconry(() -> Skills.getRealLevel(Skill.HUNTER) < 73).setSimpleName("Falconry")
        );
    }
}
