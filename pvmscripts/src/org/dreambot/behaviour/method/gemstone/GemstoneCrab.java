package org.dreambot.behaviour.method.gemstone;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.Map;
import java.util.function.Supplier;

public class GemstoneCrab extends TickFractal {
    private GemstoneCrab(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    public static GemstoneCrab getMelee(Map<Skill, Integer> targets) {
        GemstoneCrab branch = new GemstoneCrab(() -> targets.entrySet().stream().anyMatch(tgt -> {
            return tgt.getKey().getLevel() <= tgt.getValue();
        }));

        branch.setSimpleName("Gemstone crab melee");
        branch.addChildren(
                new FindGemStoneCrab().setSimpleName("Find gemstone"),
                new GemStoneMeleeStyle(targets).setSimpleName("Style"),
                new TickDrinkPotions()
                        .addPotion(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 2)
                        .addPotion(ItemVariants.DIVINE_SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3)
                        .addPotion(ItemVariants.SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3)
                        .addPotion(ItemVariants.COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 1)

                        .addPotion(ItemVariants.RANGING_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3)

                        .addPotion(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 2)
                        .addPotion(ItemVariants.STAMINA_POTION, () -> Walking.getRunEnergy() < 5)
                        .addPotion(ItemVariants.ANTI_DOTE_PP, () -> Combat.isPoisoned() || Combat.isEnvenomed()),
                new KillGemStoneCrab().setSimpleName("Kill gemstone")
        );

        return branch;
    }

    public static GemstoneCrab getRange(int range, int def) {
        GemstoneCrab branch = new GemstoneCrab(() -> Skill.RANGED.getLevel() < range || Skill.DEFENCE.getLevel() < def);

        branch.setSimpleName("Gemstone crab range");
        branch.addChildren(
                new FindGemStoneCrab().setSimpleName("Find gemstone"),
                new GemStoneRangeStyle(def).setSimpleName("Set style"),
                new KillGemStoneCrab().setSimpleName("Kill gemstone")
        );

        return branch;
    }


    public static GemstoneCrab getMagic(int magic, int def) {
        GemstoneCrab branch = new GemstoneCrab(() -> Skill.MAGIC.getLevel() < magic || Skill.DEFENCE.getLevel() < def);

        branch.setSimpleName("Gemstone crab magic");
        branch.addChildren(
                new FindGemStoneCrab().setSimpleName("Find gemstone"),
                new GemStoneMagicStyle(def).setSimpleName("Set autocast"),

                new KillGemStoneCrab().setSimpleName("Kill gemstone")
        );

        return branch;
    }
    // todo magic
}
