package org.dreambot.behaviour.training.prayer;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class PrayerBranch extends Fractal {
    public static List<Integer> BIPRODUCTS = Arrays.asList(
            ItemID.DRAGON_BONES,
            ItemID.TELEPORT_TO_HOUSE
    );

    public PrayerBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        init();
        this.paintArraySupplier = () -> new String[]{
                "Prayer level: " + Skills.getRealLevel(Skill.PRAYER)
        };
    }


    private void init() {
        addChildren(
                new GetHouse().setSimpleName("Getting house"),
                new PrayAtAltar().setSimpleName("Praying"),
                new GotoHouse().setSimpleName("Going to house")
        );
    }
}
