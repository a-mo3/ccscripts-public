package org.dreambot.behaviour.quests.digsite;

import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.herblore.HerbloreBranch;
import org.dreambot.behaviour.training.thieving.ThievingBranch;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class TheDigSite extends Fractal {

    public TheDigSite() {
        super(() -> !PaidQuest.THE_DIG_SITE.isFinished());
        setSimpleName("Dig site");
        addChildren(
                new ThievingBranch(() -> Skills.getRealLevel(Skill.THIEVING) < 25).setSimpleName("25 thieve"),
                new HerbloreBranch(() -> Skills.getRealLevel(Skill.HERBLORE) < 10, false).setSimpleName("10 herblore"),
                new AgilityBranch(() -> Skills.getRealLevel(Skill.AGILITY) < 10).setSimpleName("10 agility")
                // todo quest
        );
    }
}
