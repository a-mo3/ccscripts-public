package org.dreambot.behaviour.method.spindel.tickspindel;

import org.dreambot.fractals.TickFractal;

import java.util.function.Supplier;

public class TickSpindelBranch extends TickFractal {
    public TickSpindelBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Spindel range");

        addChildren(
                // todo anti crash decision - leave or what not
                new TickSpindelRunToggle().setSimpleName("Run toggle"),
                new TickSpindelPrayerDecision().setSimpleName("Spindel Prayer"),
                new TickSpindelPotionDecision().setSimpleName("Spindel potion"),
                new TickSpindelEatDecision().setSimpleName("Spindel eat"),
                new TickSpindelHandleMinions().setSimpleName("Spiderlings"),
                // place the web in the spot where a new person spawns.
                new TickSpindelWebDodge().setSimpleName("Web dodge"),
                new TickSpindelMeleeAttack().setSimpleName("Melee Atk"),
                new TickSpindelLoot().setSimpleName("Loot")
        );
    }
}
