package org.dreambot.behaviour.mining;

import org.dreambot.api.methods.map.Area;

public enum SilverLocation implements MiningLocation{
    CHAMP_GUILD(new Area(3174, 3372, 3181, 3363), false, false),
    ;

    final Area location;
    final boolean isHcimSafe;
    final boolean isMembers;

    SilverLocation(Area location, boolean isHcimSafe, boolean isMembers) {
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
