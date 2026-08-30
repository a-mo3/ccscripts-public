package org.dreambot.behaviour.mining;

import org.dreambot.api.methods.map.Area;

public enum GoldLocation implements MiningLocation {
    RIMMINGTON(new Area(2973, 3235, 2979, 3230), true, false),
    DWARVEN_MINE(new Area(3048, 9762, 3052, 9759), false, false),
    ;

    final Area location;
    final boolean isHcimSafe;
    final boolean isMembers;

    GoldLocation(Area location, boolean isHcimSafe, boolean isMembers) {
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
