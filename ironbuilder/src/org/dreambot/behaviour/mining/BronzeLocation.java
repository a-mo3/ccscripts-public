package org.dreambot.behaviour.mining;

import org.dreambot.api.methods.map.Area;

/**
 * a bronze location is a mine that has copper and tin in it.
 */
public enum BronzeLocation implements MiningLocation {
    DWARVEN(new Area(3028, 9829, 3034, 9821), false, false),
    LUM_SWAMP(new Area(3221, 3150, 3231, 3143), true, false),
    EDGEVILLE_DUNGEON(new Area(3134, 9880, 3144, 9867), false, false),
    ;
    final Area location;
    final boolean isHcimSafe;
    final boolean isMembers;

    BronzeLocation(Area location, boolean isHcimSafe, boolean isMembers) {
        this.location = location;
        this.isHcimSafe = isHcimSafe;
        this.isMembers = isMembers;
    }

    @Override
    public Area getLocation() {
        return location;
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
