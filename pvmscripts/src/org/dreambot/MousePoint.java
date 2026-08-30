package org.dreambot;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.interactive.Players;

import java.awt.*;

public class MousePoint {
    Point mousePoint = Mouse.getPosition();
    int regionId = Players.getLocal().getRegionId();

    @Override
    public String toString() {
        return mousePoint.x + "," + mousePoint.y + "," + regionId;
    }
}
