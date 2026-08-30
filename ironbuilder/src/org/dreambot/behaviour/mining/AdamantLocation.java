package org.dreambot.behaviour.mining;

import org.dreambot.api.methods.map.Area;

public enum AdamantLocation implements MiningLocation {
    WILDERNESS_MINE(new Area(3072, 3762, 3085, 3752), false, false),
    ;

    final Area location;
    final boolean isHcimSafe;
    final boolean isMembers;

    AdamantLocation(Area location, boolean isHcimSafe, boolean isMembers) {
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
