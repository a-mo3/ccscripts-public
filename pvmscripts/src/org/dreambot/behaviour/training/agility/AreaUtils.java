package org.dreambot.behaviour.training.agility;


import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.Locatable;

public class AreaUtils {
    public static boolean containsIgnorePlane(Area area, Tile tile) {
        Tile newTile = new Tile(tile.getX(), tile.getY(), area.getZ());
        return area.contains(newTile);
    }

    public static boolean containsIgnorePlane(Area area, Locatable tile) {
        Tile newTile = new Tile(tile.getX(), tile.getY(), area.getZ());
        return area.contains(newTile);
    }
}
