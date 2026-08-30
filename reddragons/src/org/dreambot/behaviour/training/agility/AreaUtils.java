package org.dreambot.behaviour.training.agility;


import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public class AreaUtils {
    public static boolean containsIgnorePlane(Area area, Tile tile) {
        Tile newTile = new Tile(tile.getX(), tile.getY(), area.getZ());
        return area.contains(newTile);
    }
}
