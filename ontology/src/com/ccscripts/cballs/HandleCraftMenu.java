package com.ccscripts.cballs;

import com.ccscripts.actions.ActionType;
import com.ccscripts.cballs.framework.ScriptNode;
import com.piler.constraints.NodeConstraint;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.awt.*;
import java.util.List;

public class HandleCraftMenu extends ScriptNode {
    public HandleCraftMenu() {
        this.constraint = new NodeConstraint()
                .mustCompileWithAtLeastOneOfTypes(ActionType.ENTITY_INTERACTION, ActionType.KEY_PRESS)
        ;
    }

    @Override
    public boolean isValid() {
        return ItemProcessing.isOpen();
    }

    @Override
    public int fallBack() {
        return 0;
    }

    @Override
    public String getIdentifier() {
        return "HandleCraftMenu";
    }

    @Override
    public String getExpectedNextState() {
        return "WaitForCrafting";
    }

    @Override
    public List<Rectangle> trainingHighlights() {
        WidgetChild wc = Widgets.get(270, 15);
        return List.of(
                wc.getRectangle()
        );
    }

    @Override
    protected boolean shouldConfigureReproducers() {
        return false;
    }
}
