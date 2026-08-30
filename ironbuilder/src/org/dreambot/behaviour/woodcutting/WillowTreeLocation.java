package org.dreambot.behaviour.woodcutting;

import org.dreambot.api.methods.map.Area;

public enum WillowTreeLocation {
    CRAFTING_GUIDE(new Area(2909, 3306, 2928, 3293)),
    PORT_SARIM(new Area(3055, 3256, 3064, 3250)),
    DRAYNOR(new Area(3082, 3238, 3091, 3225)),
    SOUTH_PORT_SARIM(new Area(2991, 3174, 3033, 3162)),
    RIMMINGTON_1(new Area(2985, 3191, 2992, 3181)),
    RIMMINGTON_2(new Area(2959, 3201, 2976, 3190)),
    LUM_LAKE(new Area(2959, 3201, 2976, 3190)),
    LUM_RIVER(new Area(3232, 3246, 3237, 3234)),
    LUM_RIVER_NORTH(new Area(3218, 3311, 3223, 3298)),
    ;

    final Area treeLocation;

    WillowTreeLocation(Area treeLocation) {
        this.treeLocation = treeLocation;
    }
}
