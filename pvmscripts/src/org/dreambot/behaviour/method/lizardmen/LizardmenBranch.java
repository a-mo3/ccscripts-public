package org.dreambot.behaviour.method.lizardmen;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.behaviour.misc.tickcombat.decisions.*;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.scriptdata.LizardmenSettings;

import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class LizardmenBranch extends TickFractal {
    public LizardmenBranch(Supplier<Boolean> acceptCondition, LizardmenSettings settings) {
        super(acceptCondition);

        HashMap<ItemVariant, BooleanSupplier> potions = new HashMap<>();
        potions.put(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 2);
        potions.put(ItemVariants.BASTION_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3);
        potions.put(ItemVariants.RANGE_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3);
        potions.put(ItemVariants.ANTI_DOTE_PP, () -> Combat.isPoisoned() || Combat.isEnvenomed());

        addChildren(
                new TickFlickPray(settings.shouldFlick),

                new TickSetCombatStyle(CombatStyle.RANGED_RAPID),
                new TickConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MISSILES, PVMUtil.getBestRangePray()}),

                new TickDrinkPotions(potions),

                new GenericTickEat(),

                new KillLizardmen(settings.room)
        );
    }
}
