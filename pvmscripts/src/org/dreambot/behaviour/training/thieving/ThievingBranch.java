package org.dreambot.behaviour.training.thieving;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.quests.TheFeud;
import org.dreambot.behaviour.quests.XMarksTheSpot;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class ThievingBranch extends Fractal {

    public ThievingBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Thieving");
        Area MEN_AREA = new Area(3217, 3233, 3226, 3205);

        addChildren(
                // pickpocket men
                new GenericPickpocket(() -> Skills.getRealLevel(Skill.THIEVING) < 5,
                        () -> NPCs.closest(x -> x.getName().toLowerCase().contains("man") && x.hasAction("Pickpocket")),
                        MEN_AREA)
                        .setSimpleName("Pickpocket Men"),
                // steal tea
                new StealFromTeaStall(() -> Skills.getRealLevel(Skill.THIEVING) < 25)
                        .setSimpleName("Tea Stall"),

                new XMarksTheSpot().setSimpleName("X marks the spot"),
//                new ClientOfKourend().setSimpleName("Kourend"),
                new StealFromFruitStall(() -> Skills.getRealLevel(Skill.THIEVING) < 45)
                        .setSimpleName("Fruit stalls"),
                // start feud then blackjack
                new TheFeud().setSimpleName("The Feud (Black jacking)"),
                new BlackJackingBranch(() -> true).setSimpleName("BJ'ing ;)")
        );
    }
}
