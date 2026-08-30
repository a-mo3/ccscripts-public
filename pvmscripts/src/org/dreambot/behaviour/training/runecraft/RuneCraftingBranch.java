package org.dreambot.behaviour.training.runecraft;

import org.dreambot.behaviour.quests.entertheabyss.EnterTheAbyss;
import org.dreambot.behaviour.quests.runemysteries.RuneMysteries;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class RuneCraftingBranch extends Fractal {
    public RuneCraftingBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        addChildren(
                new RuneMysteries().setSimpleName("Rune mysteries"),
                new EnterTheAbyss().setSimpleName("Enter the abyss"),
                new EarthRunes(() -> true).setSimpleName("Earth runes")
//                new TempleOfTheEye().setSimpleName("Temple of the eye")
        );
    }
}
