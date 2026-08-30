package org.dreambot.behaviour.quests.pip;


import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public class PriestInPerilAreas {
    public static final Area ROALD_THRONE_ROOM = new Area(
            new Tile(3225, 3471, 0),
            new Tile(3219, 3471, 0),
            new Tile(3220, 3478, 0),
            new Tile(3223, 3478, 0));

    public static final Area OUTSIDE_TEMPLE = new Area(3408, 3490, 3404, 3487, 0);

    // in front of the trapdoor / ladder to go to the dog fight
    public static final Area IN_FRONT_OF_TRAPDOOR = new Area(3409, 3496, 3403, 3506, 0);

    public static final Area TEMPLE_FIRST_FLOOR = new Area(3409, 3484, 3418, 3493, 0);
    public static final Area TEMPLE_SECOND_FLOOR = new Area(3409, 3484, 3418, 3493, 1);
    public static final Area TEMPLE_THIRD_FLOOR = new Area(3409, 3484, 3418, 3493, 2);

    public static final Area MONUMENT_ROOM = new Area(3429, 9882, 3416, 9897, 0);
    // opposite side of in front of trapdoor, walker is bad :(
    public static final Area EXIT_MONUMENT = new Area(3407, 9903, 3403, 9907, 0);

    public static final Area FREED_DREZEL = new Area(3442, 9894, 3437, 9900, 0);
}
