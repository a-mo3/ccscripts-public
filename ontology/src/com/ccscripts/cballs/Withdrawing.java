package com.ccscripts.cballs;

import com.ccscripts.actions.ActionType;
import com.ccscripts.cballs.framework.ScriptNode;
import com.piler.constraints.NodeConstraint;
import org.dreambot.api.methods.container.impl.bank.Bank;

import java.awt.*;
import java.util.List;

public class Withdrawing extends ScriptNode {
    public Withdrawing() {
        this.constraint = new NodeConstraint()
                .mustCompileWithAtLeastOneOfTypes(ActionType.ENTITY_INTERACTION)
        ;
    }

    @Override
    public boolean isValid() {
        return Bank.isOpen();
    }

    @Override
    public int fallBack() {
        return 0;
    }

    @Override
    public String getIdentifier() {
        return "Withdrawing";
    }

    @Override
    public String getExpectedNextState() {
        return "CloseBank";
    }

    @Override
    public List<Rectangle> trainingHighlights() {
        return List.of(
        );
    }

    @Override
    protected boolean shouldConfigureReproducers() {
        return false;
    }
}
