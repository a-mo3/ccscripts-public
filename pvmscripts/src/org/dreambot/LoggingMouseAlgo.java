package org.dreambot;

import lombok.SneakyThrows;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.input.event.impl.mouse.MouseButton;
import org.dreambot.api.input.mouse.algorithm.MouseAlgorithm;
import org.dreambot.api.input.mouse.destination.AbstractMouseDestination;
import org.dreambot.api.utilities.Logger;

public class LoggingMouseAlgo implements MouseAlgorithm {
    MouseAlgorithm m = Mouse.getDefaultMouseAlgorithm();
    final PointBufferNio clickHistory;

    public LoggingMouseAlgo(PointBufferNio clickHistory) {
        this.clickHistory = clickHistory;
        Mouse.setMouseAlgorithm(this);
    }

    @Override
    public boolean handleMovement(AbstractMouseDestination abstractMouseDestination) {
        Logger.info("Penis - movement" );
        return m.handleMovement(abstractMouseDestination);
    }

    @SneakyThrows
    @Override
    public boolean handleClick(MouseButton mouseButton) {
        Logger.info("Penis - click");
        boolean r = m.handleClick(mouseButton);
        clickHistory.push(new MousePoint());
        return r;
    }
}
