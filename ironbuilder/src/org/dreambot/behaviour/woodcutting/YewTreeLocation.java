package org.dreambot.behaviour.woodcutting;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public enum YewTreeLocation {
    RIMMINGTON(new Area(2929, 3236, 2943, 3222)),
    PALACE(new Area(3202, 3506, 3224, 3498)),
    LUMBER_YARD(new Area(
            new Tile(3263, 3496, 0),
            new Tile(3268, 3501, 0),
            new Tile(3273, 3493, 0),
            new Tile(3272, 3479, 0),
            new Tile(3310, 3475, 0),
            new Tile(3309, 3461, 0),
            new Tile(3266, 3468, 0)
    )),
    LUMMY(new Area(
            new Tile(3140, 3259, 0),
            new Tile(3155, 3260, 0),
            new Tile(3161, 3230, 0),
            new Tile(3180, 3229, 0),
            new Tile(3193, 3229, 0),
            new Tile(3187, 3215, 0),
            new Tile(3163, 3212, 0),
            new Tile(3144, 3227, 0)
    )),
    EDGE(new Area(3085, 3482, 3088, 3468)),
    ;

    final Area treeLocation;

    YewTreeLocation(Area treeLocation) {
        this.treeLocation = treeLocation;
    }
}
