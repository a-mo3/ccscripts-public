package org.dreambot.behaviour.smithing;

import lombok.Getter;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.GameObject;

@Getter
public enum FurnaceLocation {
    EDGE(new Area(3105, 3501, 3110, 3496), x -> "Furnace".equals(x.getName())),
    AL_KHARID(new Area(3272, 3188, 3279, 3184), x -> "Furnace".equals(x.getName())),
    LUMBRIDGE(new Area(3222, 3257, 3228, 3249), x -> "Furnace".equals(x.getName())),
    ;

    final Area area;
    final Filter<GameObject> furnaceFilter;

    FurnaceLocation(Area area, Filter<GameObject> furnaceFilter) {
        this.area = area;
        this.furnaceFilter = furnaceFilter;
    }
}
