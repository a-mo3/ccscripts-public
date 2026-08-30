package org.dreambot.behaviour.training.farming.tithe;

import org.dreambot.api.Client;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class TitheFarmBranch extends Fractal {

    public TitheFarmBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Tithe farm");

        addChildren(
                new ExitTitheFarm().setSimpleName("Exit tithe"),
                new EnterTitheFarm(() -> !Client.isDynamicRegion()),
                new FarmTheTithe(() -> true).setSimpleName("Farm")
        );
    }
}
