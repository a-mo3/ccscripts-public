package com.ccscripts.reproducer;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.impl.hard.EntityInteraction;
import com.ccscripts.actions.impl.hard.WalkAction;
import org.dreambot.api.methods.walking.impl.Walking;

public class WalkReproducer extends AbstractActionReproducer {
    // walks have a menu action done before them that needs to be added to tell op code 23(tile on screen) or 1006(minimap)
    final WalkAction walkAction;
    final EntityInteraction associatedMenuAction;

    public WalkReproducer(WalkAction walkAction, EntityInteraction associatedMenuAction) {
        super("Walk To " + walkAction.getDestination() + " From " + walkAction.getCurrent());
        this.walkAction = walkAction;
        this.associatedMenuAction = associatedMenuAction;
    }

    @Override
    public AbstractAction getAction() {
        return walkAction;
    }

    @Override
    public void execute() {
        if (walkAction == null || associatedMenuAction == null) {
            log("Terrible failure null walk action or associated menu action");
            return;
        }

        int op = associatedMenuAction.getRow().getOpcode();
        if (op == 23) {
            log("On screen walk");
            Walking.walkOnScreen(walkAction.destToTile());
            return;
        }

        log("minimap walk");
        Walking.clickTileOnMinimap(walkAction.destToTile());

    }
}
