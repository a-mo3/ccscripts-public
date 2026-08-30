package org.dreambot.behaviour.method.lavadragons;

import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * instead of running back to bank, it may be faster & safer to suicide -> loot death coffer
 */
public class LavaDragonSuicide extends Fractal {
    public LavaDragonSuicide(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        // if dragon is near go stand next to it

        return ReactionGenerator.getQuick();
    }
}
