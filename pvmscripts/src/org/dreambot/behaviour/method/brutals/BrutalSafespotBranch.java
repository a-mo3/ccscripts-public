package org.dreambot.behaviour.method.brutals;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.util.PrayerUtils;

import java.util.function.Supplier;

public class BrutalSafespotBranch extends TickFractal {
    public BrutalSafespotBranch(Supplier<Boolean> acceptCondition, Supplier<NPC> npcSupplier, Tile safeSpot) {
        super(acceptCondition);
        this.setPrependLogic(() -> {
//            PrayerUtils.disableAll();
            return false;
        });

        addChildren(
                // prayer flick
                new BrutalPrayerFlick(false).setSimpleName("Flick"),
                // eat and drink (shouldn't rly ever need to)
                new BrutalPotionDecision().setSimpleName("Potion"),
                new BrutalEatDecision().setSimpleName("Eat"),
                // loot
                // attack & get into spot
                new BrutalAttackDecision(npcSupplier, safeSpot).setSimpleName("Attack")
        );
    }
}
