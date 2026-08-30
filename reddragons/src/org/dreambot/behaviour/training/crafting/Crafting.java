package org.dreambot.behaviour.training.crafting;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;

import java.util.function.Supplier;

public class Crafting extends Fractal {
    public Crafting(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        addChildren(
                new SpinSomething(() -> Skills.getRealLevel(Skill.CRAFTING) < 10,
                        ItemID.WOOL,
                        500,
                        ItemID.BALL_OF_WOOL).setSimpleName("Spin wool"),

                new SpinSomething(() -> true,
                        ItemID.FLAX,
                        5000,
                        ItemID.BOW_STRING).setSimpleName("Spin bow strings")
        );
    }
}
