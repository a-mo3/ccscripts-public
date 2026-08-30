package org.dreambot.behaviour.training;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public class QuestAreas {
    public static final Area THURGO_HOUSE = new Area(
            new Tile(2995, 3148, 0),
            new Tile(3002, 3148, 0),
            new Tile(3002, 3142, 0),
            new Tile(3005, 3142, 0),
            new Tile(3003, 3138, 0),
            new Tile(2996, 3138, 0));

    // knights sword
    public static final Area SQUIRE_AREA = new Area(
            new Tile(2980, 3337, 0),
            new Tile(2980, 3346, 0),
            new Tile(2976, 3346, 0),
            new Tile(2974, 3348, 0),
            new Tile(2971, 3347, 0),
            new Tile(2971, 3337, 0));
}
