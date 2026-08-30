package org.dreambot.behaviour.method.gwd.nex.kc;

import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.behaviour.misc.tickcombat.decisions.*;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;

import java.util.function.Supplier;

public class TickGetNexKC extends TickFractal {
    public TickGetNexKC(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Nex kc tick");

        addChildren(
                new TickFlickPray(true).setSimpleName("Flick"),

                new GenericTickEat().setSimpleName("Eat"),
                new TickDrinkPotions()
                        .addPotion(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 2)
                        .addPotion(ItemVariants.SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 2)
                        .addPotion(ItemVariants.RANGING_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3),
                new TickConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MAGIC, PVMUtil.getBestRangePray()}),

                new TickAttackMob(x -> x.getName().equals("Blood Reaver"), null)
        );
    }
}
