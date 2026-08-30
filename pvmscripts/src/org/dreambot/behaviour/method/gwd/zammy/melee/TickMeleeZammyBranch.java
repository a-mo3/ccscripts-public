package org.dreambot.behaviour.method.gwd.zammy.melee;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.behaviour.method.gwd.zammy.TickZammyPrayer;
import org.dreambot.behaviour.method.gwd.zammy.ZammyCounters;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.ZammySettings;

import java.util.function.Supplier;

public class TickMeleeZammyBranch extends TickFractal {
    public TickMeleeZammyBranch(Supplier<Boolean> acceptCondition, ZammySettings settings) {
        super(acceptCondition);

        setSimpleName("Melee Zammy");

        paintArraySupplier = () -> new String[]{
                "Zammy counter: " + ZammyCounters.zamCounter,
                "Range counter: " + ZammyCounters.rangeCounter,
                "Melee counter: " + ZammyCounters.meleeCounter,
                "Magic counter: " + ZammyCounters.magicCounter,
                "Our counter: " + ZammyCounters.ourCounter,
        };

        addChildren(
                new ZammyCounters().setSimpleName("Counters"),
                new TickZammyPrayer().setSimpleName("Zam prayers"),

//                new TickDrinkPotions()
//                        .addPotion(ItemVariants.SUPER_RESTORE, () -> Skill.ATTACK.getLevel() - Skill.ATTACK.getBoostedLevel() > 10)
//                        .addPotion(ItemVariants.SARADOMIN_BREW, () -> Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel() > 20)
//                        .setSimpleName("Pots"),
                new TickDrinkPotions()
                        .addPotion(ItemVariants.SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 10),

                new TickZammyEat().setSimpleName("Eat"),
                new TickZammyAttack().setSimpleName("Atk")
        );
    }
}
