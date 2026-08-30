package org.dreambot.behaviour.mining;

import org.dreambot.api.methods.map.Area;

public enum MithrilLocation implements MiningLocation {
    DWARVEN_MINE(new Area(3033, 9773, 3039, 9770), false, false),
    MINING_GUILD(new Area(3043, 9740, 3054, 9731), true, false),
    EDGEVILLE_DUNGEON(new Area(3134, 9880, 3144, 9867), false, false),
    WILDERNESS_MINE(new Area(3081, 3771, 3103, 3753), false, false),
    ;

    final Area location;
    final boolean isHcimSafe;
    final boolean isMembers;

    MithrilLocation(Area location, boolean isHcimSafe, boolean isMembers) {
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
