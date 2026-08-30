package org.dreambot.behaviour.training.hunter;

import lombok.Getter;
import org.dreambot.api.methods.map.Tile;

public enum GrayChinSpot {
    PISCATORIS(new Tile(2335, 3616)), // also new tile (2339, 3589), new (2362, 3565)
    KOUREND(new Tile(1484, 3506)),
    ISLE_OF_SOULS(new Tile(2129, 2950));
    @Getter
    private final Tile center;

    GrayChinSpot(Tile center) {
        this.center = center;
    }
}
