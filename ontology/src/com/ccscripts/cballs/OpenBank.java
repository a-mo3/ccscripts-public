package com.ccscripts.cballs;

import com.ccscripts.cballs.framework.ScriptNode;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.wrappers.interactive.Entity;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class OpenBank extends ScriptNode {
    public OpenBank() {
    }

    public boolean isValid() {
        return !Bank.isOpen();
    }

    @Override
    public int fallBack() {
        return 0;
    }

    @Override
    public String getIdentifier() {
        return "OpenBank";
    }

    @Override
    public String getExpectedNextState() {
        return "Withdrawing";
    }

    @Override
    public List<Rectangle> trainingHighlights() {
        List<Rectangle> r = GameObjects.all(x -> x.distance() < 20 && "Bank booth".equals(x.getName()))
                .stream()
                .map(Entity::getBoundingBox)
                .collect(Collectors.toList());
        r.addAll(NPCs.all(x -> x.distance() < 20 && "Banker".equals(x.getName()))
                .stream()
                .map(Entity::getBoundingBox)
                .collect(Collectors.toList()));
        return r;
    }

    @Override
    protected boolean shouldConfigureReproducers() {
        return false;
    }
}
