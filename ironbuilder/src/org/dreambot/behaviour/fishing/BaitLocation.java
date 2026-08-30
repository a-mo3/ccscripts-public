package org.dreambot.behaviour.fishing;

import org.dreambot.api.methods.map.Area;

public enum BaitLocation {
    LUM(new Area(3237, 3257, 3244, 3238), "Bait"),
    BARB_VILLAGE(new Area(3100, 3439, 3111, 3422), "Bait")
    ;

    final Area location;
    final String action;

    BaitLocation(Area location, String action) {
        this.location = location;
        this.action = action;
    }
}
