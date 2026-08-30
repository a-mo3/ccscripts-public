package org.dreambot.analytics.impl;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.input.event.impl.mouse.MouseButton;
import org.dreambot.api.input.mouse.algorithm.MouseAlgorithm;
import org.dreambot.api.input.mouse.algorithm.StandardMouseAlgorithm;
import org.dreambot.api.input.mouse.destination.AbstractMouseDestination;

import java.awt.*;

public class AnalyticMouseAlgo implements MouseAlgorithm {
    StandardMouseAlgorithm std = new StandardMouseAlgorithm();

    @Override
    public boolean handleMovement(AbstractMouseDestination abstractMouseDestination) {
        return std.handleMovement(abstractMouseDestination);
    }

    @Override
    public boolean handleClick(MouseButton mouseButton) {
        Point p = Mouse.getPosition();
        AnalyticsReporter.reportClick(p);
        return std.handleClick(mouseButton);
    }

    @Override
    public void clear() {
        MouseAlgorithm.super.clear();
    }
}
