package org.dreambot.behaviour.mining;

import org.apache.commons.math3.analysis.function.Min;
import org.dreambot.api.methods.map.Area;

public enum TinLocation implements MiningLocation {
    RIMMINGTON(new Area(2983, 3237, 2987, 3233), true, false),
    SE_VARROCK_1(new Area(3280, 3365, 3284, 3361), true, false),
    SE_VARROCK_2(new Area(3286, 3368, 3290, 3365), true, false),
    SW_VARROCK_1(new Area(3179, 3378, 3185, 3373), false, false),
    SW_VARROCK_2(new Area(3170, 3370, 3180, 3363), false, false),

    DWARVEN_MINE_1(new Area(3051, 9784, 3059, 9776), false, false),
    DWARVEN_MINE_2(new Area(3049, 9814, 3054, 9810), false, false),
    DWARVEN_MINE_3(new Area(3029, 9824, 3034, 9821), false, false),

    ;
    final Area location;
    final boolean isHcimSafe;
    final boolean isMembers;

    TinLocation(Area location, boolean isHcimSafe, boolean isMembers) {
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
