package org.dreambot.behaviour.method.gorillas;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.function.Supplier;

public class DemonicGorillaBranch extends TickFractal {
    public DemonicGorillaBranch(Supplier<Boolean> acceptCondition, Area area, boolean flickPrayer) {
        super(acceptCondition);

        setSimpleName("Demonic Gorilla");
        paintArraySupplier = () -> {
            Character target = Players.getLocal().getInteractingCharacter();
            return new String[]{
                    "Ani " + (target == null ? "-" : target.getAnimation()),
                    "Neg " + FightDemonicGorilla.negativeStyle.toString(),
                    "Cnt " + FightDemonicGorilla.missCounter
            };
        };

        addChildren(
                new TickDrinkPotions()
                        .addPotion(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 2)
                        .addPotion(ItemVariants.SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 2)
                        .addPotion(ItemVariants.DIVINE_SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3)
                        .addPotion(ItemVariants.SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3)

                        .addPotion(ItemVariants.RANGE_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3)

                        .addPotion(ItemVariants.STAMINA_POTION, () -> Walking.getRunEnergy() < 5)
                        .addPotion(ItemVariants.ANTI_DOTE_PP, () -> Combat.isPoisoned() || Combat.isEnvenomed()),

                new GenericTickEat(),
                new DemonicGorillaLoot().setSimpleName("Loot"),
                new FightDemonicGorilla(area, flickPrayer).setSimpleName("Fight")
        );
    }
}
