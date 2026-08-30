package org.dreambot.behaviour.training.cooking;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;

import java.util.function.Supplier;

public class CookingBranch extends Fractal {
    public CookingBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        setSimpleName("Cooking");

        addChildren(
                new RoguesDenCook(() -> Skills.getRealLevel(Skill.COOKING) < 25, ItemID.RAW_SARDINE, 350, ItemID.SARDINE)
                        .setSimpleName("Sardines to 25"),
                new RoguesDenCook(() -> Skills.getRealLevel(Skill.COOKING) < 40, ItemID.RAW_SALMON, 3000, ItemID.SALMON)
                        .setSimpleName("Salmon to 62"),
                new RoguesDenCook(() -> Skills.getRealLevel(Skill.COOKING) < 68, ItemID.RAW_LOBSTER, 3000, ItemID.LOBSTER)
                        .setSimpleName("Lobster to 68"),
                new RoguesDenCook(() -> true, ItemID.RAW_MONKFISH, 3000, ItemID.MONKFISH)
                        .setSimpleName("Monkfish to 99")

        );
    }
}
