package org.dreambot.behaviour.method.spindel.melee;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.SpindelAntiPk;
import org.dreambot.behaviour.method.spindel.SpindelData;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * for melee we need to switch to darts and a bit different prayers
 */
public class MeleeKillSpiderlings extends Fractal {
    public MeleeKillSpiderlings(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        MeleeSpindelBranch.prayCorrectly();

        if (!Equipment.contains(ItemID.ADAMANT_DART)) {
            if (!Inventory.contains(ItemID.ADAMANT_DART)) {
                Logger.info("No darts gotta bounce");
                return SpindelAntiPk.leaveSpindel();
            }

            Inventory.interact(ItemID.ADAMANT_DART);
            return ReactionGenerator.getQuick();
        }

        List<NPC> spiderlings = NPCs.all(SpindelData.SPIDERLING_ID).stream()
                .sorted(Comparator.comparingDouble(Entity::distance))
                .collect(Collectors.toList());

        for (NPC spiderling : spiderlings) {
            if (spiderling != null) {
                MeleeSpindelBranch.prayCorrectly();
                spiderling.interact("Attack");
                // todo add a clause for a pker in this
                Sleep.sleepUntil(() -> spiderling.getHealthPercent() == 0, 1200);
            }
        }

        return ReactionGenerator.getQuick();
    }


}
