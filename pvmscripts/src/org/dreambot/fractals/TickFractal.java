package org.dreambot.fractals;

import org.dreambot.api.Client;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class TickFractal extends Fractal {
    List<TickDecision> decisions = new ArrayList<>();
    int lastRanTick = -1;

    public TickFractal(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    public TickFractal() {
    }

    @Override
    public Fractal addChildren(Fractal... childFractals) {
        log("Cannot add children to a tick fractal.");
        throw new RuntimeException("Not implemented");
    }

    public TickFractal addChildren(TickDecision... decisions) {
        this.decisions.addAll(Arrays.asList(decisions));
        return this;
    }

    @Override
    public int onLoop() {
        if (lastRanTick == Client.getGameTick()) return 25;

        for (TickDecision tickDecision : decisions) {
            if (tickDecision.evaluate()) break;
            else Sleep.sleep(25);
        }

        lastRanTick = Client.getGameTick();
        return 25;
    }
}
