package org.dreambot.behaviour.antelopes.sunfire;

import org.dreambot.behaviour.antelopes.EatFood;
import org.dreambot.behaviour.antelopes.FletchBolts;
import org.dreambot.behaviour.antelopes.LootAntelopes;
import org.dreambot.behaviour.antelopes.ScavangeAntelopes;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class SunlightAntelopes extends Fractal {
    public SunlightAntelopes(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        addChildren(
                new EatFood().setSimpleName("Eat"),
                new FletchBolts().setSimpleName("Fletch"),
                // todo handle banking, or just only bank when you mule off / run out of sharks
                new GotoSunlightAntelopes().setSimpleName("Go to antelopes"),
                new ScavangeAntelopes().setSimpleName("Scavenge"),
                new LootAntelopes().setSimpleName("Loot"),
                new BaitSunlight().setSimpleName("Bait"),
                new SetTrapsSunlight().setSimpleName("Set traps")
        );
    }
}
