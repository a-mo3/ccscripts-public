package org.dreambot.behaviour.training.construction;


import org.dreambot.behaviour.training.prayer.GetHouse;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class ConstructionBranch extends Fractal {
    public ConstructionBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        init();
    }

    private void init() {
        addChildren(
                new GetHouse().setSimpleName("Get house"),
                new CrudeWoodenChair(() -> true).setSimpleName("Wood chains")
        );
    }
}
