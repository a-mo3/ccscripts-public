package org.dreambot.behaviour.method.spindel.range;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.SpindelData;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class KillSpiderlings extends Fractal {
    public KillSpiderlings(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        if (!Prayers.isActive(Prayer.PROTECT_FROM_MISSILES)) Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
        List<NPC> spiderlings = NPCs.all(SpindelData.SPIDERLING_ID).stream()
                .sorted(Comparator.comparingDouble(Entity::distance))
                .collect(Collectors.toList());

        for (NPC spiderling : spiderlings) {
            if (spiderling != null) {
                spiderling.interact("Attack");
                // todo add a clause for a pker in this
                Sleep.sleepUntil(() -> spiderling.getHealthPercent() == 0, 1200);
            }
        }
        return ReactionGenerator.getQuick();
    }
}
