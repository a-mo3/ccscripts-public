package org.dreambot.behaviour.fishing;

import org.dreambot.api.methods.map.Area;

public enum FlyFishLocation {
    LUM(new Area(3237, 3257, 3244, 3238), "Lure"),
    BARB_VILLAGE(new Area(3100, 3439, 3111, 3422), "Lure"),
    ;

    final Area location;
    final String action;

    FlyFishLocation(Area location, String action) {
        this.location = location;
        this.action = action;
    }
}
