package org.dreambot.behaviour.method.corp.behaviour;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickFlickPray;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.CorpSettings;

import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class FightCorpBranch extends TickFractal {
    public FightCorpBranch(Supplier<Boolean> acceptCondition, CorpSettings settings) {
        super(acceptCondition);
        setSimpleName("Fight corp");

        HashMap<ItemVariant, BooleanSupplier> potions = new HashMap<>();
        potions.put(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 1);
        potions.put(ItemVariants.DIVINE_SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3);
        potions.put(ItemVariants.BASTION_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3);
        potions.put(ItemVariants.RANGE_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3);

        addChildren(
                new TickFlickPray(settings.shouldFlick).setSimpleName("Flick prayers"),

                new TickDrinkPotions(potions),

                new GenericTickEat()
                        .setMinMissingHP(38)
                        .setSimpleName("Eat"),

                new CorpSpecDecision().setSimpleName("Spec"),

                new AttackCorp().setSimpleName("Attack corp"),

                new LootCorp().setSimpleName("Loot corp")
        );
    }
}
