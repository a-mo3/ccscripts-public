package org.dreambot.behaviour.mining;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

/**
 * clay respawns so quickly these are just single rocks not the whole area
 * todo isHcimSafe should be replaced with a condition or level at which scorpions / mugger is not aggressive
 */
public enum ClayLocation implements MiningLocation {
    DWARVEN_MINE_1(new Tile(3053, 9818), false, false),
    DWARVEN_MINE_2(new Tile(3054, 9819), false, false),
    DWARVEN_MINE_3(new Tile(3030, 9810), false, false),
    DWARVEN_MINE_4(new Tile(3028, 9808), false, false),
    DWARVEN_MINE_5(new Tile(3030, 9809), false, false),

    // mugger at varrock makes this dangerous
    VARROCK_1(new Tile(3180, 3372), false, false),
    VARROCK_2(new Tile(3179, 3371), false, false),
    VARROCK_3(new Tile(3183, 3377), false, false),

    // idk about hcim safe you can get killed walking here
    RIMMINGTON_1(new Tile(2987, 3240), true, false),
    RIMMINGTON_2(new Tile(2986, 3239), true, false),
    ;

    final Tile location;
    final boolean isHcimSafe;
    final boolean isMembers;

    ClayLocation(Tile location, boolean isHcimSafe, boolean isMembers) {
        this.location = location;
        this.isHcimSafe = isHcimSafe;
        this.isMembers = isMembers;
    }

    @Override
    public Area getLocation() {
        return location.getArea(0);
    }

    @Override
    public boolean isHcimSafe() {
        return isHcimSafe;
    }

    @Override
    public boolean isMembers() {
        return isMembers;
    }
}
