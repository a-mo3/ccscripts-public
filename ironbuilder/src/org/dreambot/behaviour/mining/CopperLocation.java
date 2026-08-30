package org.dreambot.behaviour.mining;

import org.dreambot.api.methods.map.Area;

public enum CopperLocation implements MiningLocation {
    DWARVEN_MINE_1(new Area(3027, 9830, 3034, 9824), false, false),
    DWARVEN_MINE_2(new Area(3038, 9786, 3044, 9778), false, false),
    DWARVEN_MINE_3(new Area(3019, 9805, 3028, 9797), false, false),
    RIMMINGTON(new Area(2975, 3250, 2980, 3243), false, false),
    ;
    final Area location;
    final boolean isHcimSafe;
    final boolean isMembers;

    CopperLocation(Area location, boolean isHcimSafe, boolean isMembers) {
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
