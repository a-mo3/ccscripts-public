package org.dreambot.behaviour.method.crazyarch;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickConfigureQuickPrayers;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickFlickPray;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.scriptdata.CrazySettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class FightCrazyArch extends TickFractal {
    final CrazySettings settings;

    public FightCrazyArch(Supplier<Boolean> acceptCondition, CrazySettings settings) {
        super(acceptCondition);
        this.settings = settings;
        setSimpleName("Fight");

        this.loadoutCondition = () -> !Combat.isInWild();
        this.inventoryLoadout = settings.loadout.inventoryLoadout;
        this.equipmentLoadout = settings.loadout.equipmentLoadout;

        addChildren(
                new TickConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MISSILES,
                        settings.loadout.mode == Skill.MAGIC ? PVMUtil.getBestMagePray() : PVMUtil.getBestMeleePray()}),

                new TickFlickPray(settings.flick),

                new TickDrinkPotions()
                        .addPotion(ItemVariants.BLIGHTED_SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() - Skill.PRAYER.getLevel() < 3)
                        .addPotion(ItemVariants.SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3)
                        .addPotion(ItemVariants.COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 1)
                        .addPotion(ItemVariants.MAGIC_POTION, () -> Skill.MAGIC.getBoostedLevel() - Skill.RANGED.getLevel() < 3)
                        .addPotion(ItemVariants.STAMINA_POTION, () -> Walking.getRunEnergy() < 5),

                new GenericTickEat(),

                new TickFightCrazyArch(settings.loadout.mode)

        );
    }
}
