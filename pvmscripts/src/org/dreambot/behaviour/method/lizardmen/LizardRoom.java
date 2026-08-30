package org.dreambot.behaviour.method.lizardmen;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum LizardRoom {
    EAST(
            new Area(1325, 10101, 1334, 10092),
            new Tile(1328, 10100, 0),
            new Tile(1326, 10099, 0),
            new Tile(1331, 10100, 0),
            new Tile(1333, 10099, 0),
            new Tile(1326, 10095, 0),
            new Tile(1328, 10093, 0),
            new Tile(1333, 10095, 0),
            new Tile(1332, 10093, 0)
    ),
    MID(
            new Area(1308, 10101, 1317, 10092),
            new Tile(1310, 10100, 0),
            new Tile(1309, 10098, 0),
            new Tile(1315, 10100, 0),
            new Tile(1316, 10099, 0),
            new Tile(1309, 10094, 0),
            new Tile(1310, 10092, 0),
            new Tile(1309, 10092, 0),
            new Tile(1316, 10095, 0),
            new Tile(1315, 10092, 0)
    ),
    WEST(
            new Area(1289, 10100, 1296, 10093),
            new Tile(1291, 10100, 0),
            new Tile(1289, 10099, 0),
            new Tile(1295, 10100, 0),
            new Tile(1296, 10099, 0),
            new Tile(1289, 10094, 0),
            new Tile(1290, 10093, 0),
            new Tile(1296, 10095, 0),
            new Tile(1295, 10093, 0)
    ),
    RANDOM(
            new Area(1289, 10100, 1296, 10093),
            new Tile(1291, 10100, 0),
            new Tile(1289, 10099, 0),
            new Tile(1295, 10100, 0),
            new Tile(1296, 10099, 0),
            new Tile(1289, 10094, 0),
            new Tile(1290, 10093, 0),
            new Tile(1296, 10095, 0),
            new Tile(1295, 10093, 0)
    ),
    ;

    public final Area area;
    public final List<Tile> tiles;

    LizardRoom(Area area, Tile... tiles) {
        this.area = area;
        this.tiles = new ArrayList<>(Arrays.asList(tiles));
    }
}
