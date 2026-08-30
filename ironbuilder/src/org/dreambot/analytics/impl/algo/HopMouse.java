package org.dreambot.analytics.impl.algo;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.input.event.impl.mouse.MouseButton;
import org.dreambot.api.input.mouse.algorithm.MouseAlgorithm;
import org.dreambot.api.input.mouse.destination.AbstractMouseDestination;

/**
 * Hops to the center.
 */
public class HopMouse implements MouseAlgorithm {
    MouseAlgorithm def = Mouse.getDefaultMouseAlgorithm();

    @Override
    public boolean handleMovement(AbstractMouseDestination abstractMouseDestination) {
        return Mouse.hop(abstractMouseDestination.getCenterPoint());
    }

    @Override
    public boolean handleClick(MouseButton mouseButton) {
        return def.handleClick(mouseButton);
    }
}
