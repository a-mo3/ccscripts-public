package org.dreambot.behaviour.training;


import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.training.method.CopperLongtails;
import org.dreambot.behaviour.training.method.Falconry;
import org.dreambot.behaviour.training.method.QuizBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

public class HunterBranch extends Fractal {
    public HunterBranch() {
        addChildren(
                new QuizBranch().setSimpleName("Quiz"),
                new CopperLongtails().setSimpleName("Copper Longtails"),
                new Falconry(() -> Skills.getRealLevel(Skill.HUNTER) < (ScriptSettings.trainTo73() ? 73 : 63)).setSimpleName("Falconry")
        );
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        Logger.info(String.format("Achieved a level of %d Target 73: %b, stopping script",
                Skills.getRealLevel(Skill.HUNTER), ScriptSettings.trainTo73()));
        Client.getInstance().getScriptManager().stop();
        return ReactionGenerator.getNormal();
    }
}
