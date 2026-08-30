package com.ccscripts.cballs;

import com.ccscripts.cballs.framework.ItemID;
import com.ccscripts.cballs.framework.ScriptNode;
import com.piler.constraints.NodeConstraint;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;

import java.awt.*;
import java.util.List;


public class FurnaceInteraction extends ScriptNode {
    public FurnaceInteraction() {
        this.constraint = new NodeConstraint()
                .mustCompileWithEntityInteraction(ei -> "Smelt".equals(ei.getRow().getAction()))
        ;
    }

    @Override
    public boolean isValid() {
        return Inventory.contains(ItemID.STEEL_BAR);
    }

    @Override
    public int fallBack() {
        return 0;
    }

    @Override
    public String getIdentifier() {
        return "FurnaceInteraction";
    }

    @Override
    public String getExpectedNextState() {
        return "HandleCraftMenu";
    }

    @Override
    public List<Rectangle> trainingHighlights() {
        return List.of(
                GameObjects.closest("Furnace").getBoundingBox()
        );
    }

    @Override
    protected boolean shouldConfigureReproducers() {
        return false;
    }
}
