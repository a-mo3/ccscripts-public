package org.dreambot.behaviour.method.revs.data;

import lombok.Getter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;

@Getter
public enum RevenantExit {
    LVL_40_EXIT("Stairs", "Climb-up", new Tile(3246, 10215)),
    BANDIT_CAMP_EXIT("Stairs", "Climb-up", new Tile(3217, 10058)),
    ;

    final String objectName;
    final String objectAction;
    final Tile location;

    RevenantExit(String objectName, String objectAction, Tile location) {
        this.objectName = objectName;
        this.objectAction = objectAction;
        this.location = location;
    }

    public GameObject getObject() {
        return GameObjects.closest(x -> x.hasAction(objectAction) && x.getName().equals(objectName));
    }
}
