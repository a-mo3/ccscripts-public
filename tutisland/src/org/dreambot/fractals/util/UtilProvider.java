package org.dreambot.fractals.util;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;

public class UtilProvider {
    public static void stdWalk(Area area) {
        if (Walking.shouldWalk() && (Walking.getDestination() == null || !area.contains(Walking.getDestination()))) {
          if (Walking.shouldWalk(6)) Walking.walk(area.getCenter());
        }
    }

    public static void stdWalk(Tile tile) {
        if (Walking.shouldWalk() && (Walking.getDestination() == null || !tile.equals(Walking.getDestination()))) {
          if (Walking.shouldWalk(6)) Walking.walk(tile);
        }
    }
}
