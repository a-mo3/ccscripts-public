package org.dreambot.behaviour.training.runecraft;

import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class LavaRunes extends Fractal {
    public LavaRunes(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Craft Lava Runes");
    }

    @Override
    public int onLoop() {


        return ReactionGenerator.getNormal();
    }
}
