package org.dreambot.behaviour.mining;

import org.dreambot.api.methods.map.Area;

public enum IronLocation implements MiningLocation{
    CITHAREDE_ABBEY(new Area(3398, 3172, 3405, 3166), true, false),
    AL_KHARID_1(new Area(3293, 3312, 3303, 3307), false, false),
    AL_KHARID_2(new Area(3301, 3286, 3304, 3280), false, false),
    AL_KHARID_3(new Area(3301, 3303, 3306, 3300), false, false),

    DWARVEN_MINE_0(new Area(3301, 3286, 3304, 3280), false, false),
    DWARVEN_MINE_1(new Area(3301, 3303, 3306, 3300), false, false),
    DWARVEN_MINE_2(new Area(3029, 9829, 3034, 9823), false, false),
    DWARVEN_MINE_3(new Area(3052, 9828, 3055, 9820), false, false),
    DWARVEN_MINE_4(new Area(3035, 9778, 3041, 9773), false, false),
    DWARVEN_MINE_5(new Area(3043, 9772, 3047, 9766), false, false),

    EDGEVILLE_DUNGEON(new Area(3134, 9880, 3144, 9867), false, false),
    RIMMINGTON_1(new Area(2968, 3243, 2973, 3236), true, false),
    RIMMINGTON_2(new Area(2980, 3236, 2984, 3232), true, false),

    MINING_GUILD(new Area(3030, 9739, 3035, 9735), true, false),

    WILDERNESS_0(new Area(3074, 3771, 3077, 3768), false, false),
    WILDERNESS_1(new Area(3087, 3769, 3093, 3765), false, false),
    WILDERNESS_2(new Area(3098, 3770, 3103, 3763), false, false),

    VARROCK(new Area(3173, 3368, 3177, 3364), true, false),
    VARROCK_1(new Area(3180, 3375, 3182, 3370), true, false),
    VARROCK_2(new Area(3283, 3371, 3289, 3366), true, false),

    ;

    final Area location;
    final boolean isHcimSafe;
    final boolean isMembers;

    IronLocation(Area location, boolean isHcimSafe, boolean isMembers) {
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
