package com.ccscripts.cballs.untrained;

import com.ccscripts.PaintButton;
import com.ccscripts.cballs.framework.ScriptNode;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;

import java.awt.*;
import java.util.List;

/**
 * not trained on just makes sure you are in edge
 */
public class GetToEdgeville extends ScriptNode {
    Area EDGE = new Area(3085, 3504, 3113, 3484);

    @Override
    public boolean isValid() {
        return !EDGE.contains(Players.getLocal());
    }

    @Override
    public int fallBack() {
        if (Walking.shouldWalk()) Walking.walk(EDGE);
        return 600;
    }

    @Override
    public String getIdentifier() {
        return "WalkToEdge";
    }

    @Override
    public String getExpectedNextState() {
        // lol
        return "penisbutt";
    }

    @Override
    public List<Rectangle> trainingHighlights() {
        return List.of();
    }

    @Override
    protected boolean shouldConfigureReproducers() {
        return false;
    }
}
