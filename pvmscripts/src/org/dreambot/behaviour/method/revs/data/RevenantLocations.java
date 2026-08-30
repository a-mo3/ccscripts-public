package org.dreambot.behaviour.method.revs.data;

import lombok.Getter;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

@Getter
public enum RevenantLocations {
    SOUTH_DEMON(new Area(3143, 10127, 3178, 10105), "demon"),
    IMPS(new Area(3188, 10079, 3207, 10065), "imp"),
    GOBLINS(new Area(3212, 10081, 3235, 10059), "goblin"),
    HOBGOBLIN(new Area(3235, 10111, 3250, 10087), "hobgoblin"),
    SOUTH_ORKS(new Area(3200, 10106, 3232, 10088), "ork"),
    NORTH_ORKS(new Area(3205, 10149, 3236, 10115), "ork"),
    PYREFIENDS(new Area(3159, 10170, 3196, 10141), "pyrefiend"),
    NORTH_DEMON_CYCLOPS(new Area(3155, 10208, 3196, 10179), "cyclop"),
    DRAGONS(new Area(3224, 10213, 3254, 10192), "dragon"),
    HELLHOUNDS(new Area(3229, 10185, 3261, 10148), "hellhound"),
    DARK_BEASTS(new Area(3191, 10177, 3225, 10152), "beast"),
    KNIGHTS(new Area(
            new Tile(3204, 10231, 0),
            new Tile(3197, 10225, 0),
            new Tile(3203, 10211, 0),
            new Tile(3219, 10208, 0),
            new Tile(3231, 10214, 0),
            new Tile(3242, 10221, 0),
            new Tile(3255, 10230, 0),
            new Tile(3240, 10238, 0)), "knight"),
    ;

    final Area area;
    final String mobName;

    RevenantLocations(Area area, String name) {
        this.area = area;
        this.mobName = name;
    }

    public static final Area WHOLE_REV_CAVES = new Area(3144, 10247, 3266, 10051);
}
