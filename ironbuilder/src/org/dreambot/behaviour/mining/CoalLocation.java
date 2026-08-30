package org.dreambot.behaviour.mining;

import org.dreambot.api.methods.map.Area;

public enum CoalLocation implements MiningLocation {
    AL_KHARID(new Area(3301, 3301, 3304, 3298), false, false),

    BANDIT_CAMP_WILDERNESS_0(new Area(3078, 3776, 3095, 3770), false, false),
    BANDIT_CAMP_WILDERNESS_1(new Area(3075, 3768, 3082, 3762), false, false),
    BANDIT_CAMP_WILDERNESS_2(new Area(3087, 3764, 3096, 3758), false, false),
    BANDIT_CAMP_WILDERNESS_3(new Area(3100, 3756, 3094, 3751), false, false),
    BANDIT_CAMP_WILDERNESS_5(new Area(3081, 3750, 3090, 3746), false, false),

    WILDERNESS_0(new Area(3019, 3591, 3024, 3584), false, false),
    WILDERNESS_1(new Area(3020, 3598, 3024, 3592), false, false),
    WILDERNESS_2(new Area(3012, 3599, 3019, 3593), false, false),
    WILDERNESS_3(new Area(3008, 3593, 3018, 3585), false, false),

    LUMBRIDGE(new Area(3143, 3154, 3147, 3147), true, false),

    BARB_VILLAGE(new Area(3078, 3423, 3085, 3418), true, false),

    CITHAREDE_ABBEY(new Area(3398, 3172, 3405, 3166), true, false),

    DWARVEN_MINE_0(new Area(3036, 9801, 3040, 9796), false, false),
    DWARVEN_MINE_1(new Area(3047, 9778, 3053, 9773), false, false),
    DWARVEN_MINE_2(new Area(3036, 9764, 3042, 9759), false, false),
    DWARVEN_MINE_3(new Area(3047, 9767, 3055, 9762), false, false),

    EDGEVILLE_DUNGEON(new Area(3134, 9880, 3144, 9867), false, false)

    // todo mining guild but only with reqs.

    ;

    final Area location;
    final boolean isHcimSafe;
    final boolean isMembers;

    CoalLocation(Area location, boolean isHcimSafe, boolean isMembers) {
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
