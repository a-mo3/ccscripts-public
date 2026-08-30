package org.dreambot.behaviour.method.revs.data;

import lombok.Getter;
import org.dreambot.api.methods.map.Tile;

@Getter
public enum RevenantEntrance {
    REVENANT_TELEPORT_ENTRANCE("Cavern", "Enter", new Tile(3128, 3833)),
    BANDIT_CAMP("Cavern", "Enter", new Tile(3075, 3653)),
    ;

    final String objectName;
    final String objectAction;
    final Tile location;

    RevenantEntrance(String objectName, String objectAction, Tile location) {
        this.objectName = objectName;
        this.objectAction = objectAction;
        this.location = location;
    }
}
