package org.dreambot.behaviour.fishing;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public enum SmallNetLocation {
    AL_KHARID_SHRIMP("Small Net",
            new Area(new Tile(3265, 3150, 0),
                    new Tile(3271, 3150, 0),
                    new Tile(3279, 3141, 0),
                    new Tile(3277, 3136, 0),
                    new Tile(3264, 3147, 0)), false, null),
    LUMBRIDGE("Net", new Area(3237, 3157, 3247, 3145), false, null),
    DRAYNOR("Small net", new Area(3083, 3232, 3088, 3224), false, null),
//  no musa point bcuz it takes gp  MUSA_POINT
    ;

    final String action;
    final Area location;
    final boolean isMembers;
    // nearby trees for when you want to cook the fish
    final Area rangeArea;

    SmallNetLocation(String action, Area location, boolean isMembers, Area nearbyTrees) {
        this.action = action;
        this.location = location;
        this.isMembers = isMembers;
        this.rangeArea = nearbyTrees;
    }
}
