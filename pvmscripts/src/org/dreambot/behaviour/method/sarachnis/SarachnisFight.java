package org.dreambot.behaviour.method.sarachnis;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class SarachnisFight extends TickFractal {
    public SarachnisFight(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Sarachnis");
        HashMap<ItemVariant, BooleanSupplier> potions = new HashMap<>();
        potions.put(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 2);
        potions.put(ItemVariants.BASTION_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3);
        potions.put(ItemVariants.RANGE_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3);
        potions.put(ItemVariants.ANTI_DOTE_PP, () -> Combat.isPoisoned() || Combat.isEnvenomed());

        addChildren(
                // we pray melee when next to sarachnis and range when far
                new SarachnisPrayerDecision(),

                new TickDrinkPotions(potions),


                // webbed eat
                new GenericTickEat().setAllowEat(() -> false),
                // higher eat threshold when DPS'ing
                new GenericTickEat().setMinMissingHP(40),

                new SarachnisFightDecision()

        );
    }
}
