package org.dreambot.behaviour.method.gwd.kree.group;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.KreearraSettings;

import java.util.function.Supplier;

public class GroupKree extends TickFractal {
    public GroupKree(Supplier<Boolean> acceptCondition, KreearraSettings settings) {
        super(acceptCondition);

        setSimpleName("Group Kree");
        addChildren(
                new EnterGroupKree(settings),

              // no flicking because we are switching for the minions
//                new TickFlickPray(true),

                new GenericTickEat(),

                new TickDrinkPotions()
                        .addPotion(ItemVariants.SARADOMIN_BREW, () -> Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel() > 10)
                        .addPotion(ItemVariants.SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 10),
                new TickKree()
        );
    }
}
