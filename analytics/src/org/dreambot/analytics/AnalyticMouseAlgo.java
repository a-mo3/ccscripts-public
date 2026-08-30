package org.dreambot.analytics;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.input.event.impl.mouse.MouseButton;
import org.dreambot.api.input.mouse.algorithm.MouseAlgorithm;
import org.dreambot.api.input.mouse.destination.AbstractMouseDestination;

public class AnalyticMouseAlgo implements MouseAlgorithm {
    MouseAlgorithm prevMouseAlgo = Mouse.getMouseAlgorithm();

    @Override
    public boolean handleMovement(AbstractMouseDestination abstractMouseDestination) {
        return prevMouseAlgo.handleMovement(abstractMouseDestination);
    }

    @Override
    public boolean handleClick(MouseButton mouseButton) {
        return prevMouseAlgo.handleClick(mouseButton);
    }

    @Override
    public void clear() {
        prevMouseAlgo.clear();
    }
}
