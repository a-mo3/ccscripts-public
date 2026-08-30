package org.dreambot.behaviour.method.greendragon;

import org.dreambot.api.methods.map.Area;

public enum GDKLocation {
    UNDER_RUINS(new Area(3114, 3724, 3174, 3690)),
    EASTERN(new Area(3320, 3710, 3367, 3660)),
    WESTERN(new Area(2966, 3633, 2992, 3599));

    final Area area;

    GDKLocation(Area area) {
        this.area = area;
    }
}
