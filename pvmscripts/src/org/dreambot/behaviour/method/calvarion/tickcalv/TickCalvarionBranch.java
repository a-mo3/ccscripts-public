package org.dreambot.behaviour.method.calvarion.tickcalv;

import org.dreambot.fractals.TickFractal;
import org.dreambot.scriptdata.CalvarionSettings;

import java.util.function.Supplier;

/**
 * Calvarion fight but using tick decisions for more optimal gameplay.
 */
public class TickCalvarionBranch extends TickFractal {
    public TickCalvarionBranch(Supplier<Boolean> acceptCondition, CalvarionSettings settings) {
        super(acceptCondition);

        addChildren(
                new CalvarionTickPrayer(settings.flickPrayers),
                new CalvarionTickEat(),
                new CalvarionTickPotion(),
                new CalvarionTickLightning(),
                new CalvarionTickAttack(),
                new CalvarionTickLoot(settings).setSimpleName("Loot")
        );
    }
}
