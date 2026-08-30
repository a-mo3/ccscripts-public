package org.dreambot.behaviour.training.fletching;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;

import java.util.function.Supplier;

public class FletchBranch extends Fractal {
    public FletchBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        setSimpleName("Fletching");
        addChildren(
                new HeadlessArrows(() -> fletchLvl() < 20).setSimpleName("Headless arrows"),
                new FletchSomething(() -> fletchLvl() < 25, ItemID.OAK_LOGS, ItemID.OAK_SHORTBOW_U, 600)
                        .setSimpleName("Oak short"),
                new FletchSomething(() -> fletchLvl() < 40, ItemID.OAK_LOGS, ItemID.OAK_LONGBOW_U, 400)
                        .setSimpleName("Oak long"),
                new FletchSomething(() -> fletchLvl() < 55, ItemID.WILLOW_LOGS, ItemID.WILLOW_LONGBOW_U, 400)
                        .setSimpleName("Willow long"),
                new FletchSomething(() -> true, ItemID.MAPLE_LOGS, ItemID.MAPLE_LONGBOW_U, 2000)
                        .setSimpleName("Maple long")
        );
    }

    private int fletchLvl() {
        return Skills.getRealLevel(Skill.FLETCHING);
    }
}
