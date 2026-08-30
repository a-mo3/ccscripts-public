package com.ccscripts.cballs;

import com.ccscripts.actions.ActionType;
import com.ccscripts.cballs.framework.ScriptNode;
import com.piler.constraints.NodeConstraint;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.widget.Widgets;

import java.awt.*;
import java.util.List;

public class CloseBank extends ScriptNode {
    public CloseBank() {
        this.constraint = new NodeConstraint()
                .mustCompileWithAtLeastOneOfTypes(ActionType.WALK, ActionType.ENTITY_INTERACTION, ActionType.KEY_PRESS)
        ;
    }

    public boolean isValid() {
        return Bank.isOpen();
    }

    @Override
    public int fallBack() {
        return 0;
    }

    @Override
    public String getIdentifier() {
        return "CloseBank";
    }

    @Override
    public String getExpectedNextState() {
        return "FurnaceInteraction";
    }

    @Override
    public List<Rectangle> trainingHighlights() {
        return List.of(
                Widgets.get(12, 2, 11).getRectangle()
        );
    }

    @Override
    protected boolean shouldConfigureReproducers() {
        return false;
    }
}
