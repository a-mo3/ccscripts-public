package org.dreambot.behaviour.method.mixology;

import org.dreambot.api.methods.settings.PlayerSettings;

public class MixologyData {
    public static final int VARP_LYE_RESIN = 11433;
    public static final int VARP_AGA_RESIN = 11432;
    public static final int VARP_MOX_RESIN = 11431;

    public static int lowestResinCount() {
        return Math.min(Math.min(
                        PlayerSettings.getBitValue(VARP_LYE_RESIN),
                        PlayerSettings.getBitValue(VARP_AGA_RESIN)
                ),
                PlayerSettings.getBitValue(VARP_MOX_RESIN)
        );
    }

    public static final int VARBIT_POTION_ORDER_1 = 11315;
    public static final int VARBIT_POTION_MODIFIER_1 = 11316;
    public static final int VARBIT_POTION_ORDER_2 = 11317;
    public static final int VARBIT_POTION_MODIFIER_2 = 11318;
    public static final int VARBIT_POTION_ORDER_3 = 11319;
    public static final int VARBIT_POTION_MODIFIER_3 = 11320;

    public static final int VARBIT_ALEMBIC_PROGRESS = 11328;
    public static final int VARBIT_AGITATOR_PROGRESS = 11329;

    public static final int VARBIT_AGITATOR_QUICKACTION = 11337;
    public static final int VARBIT_ALEMBIC_QUICKACTION = 11338;

    public static final int VARBIT_MIXING_VESSEL_POTION = 11339;
    public static final int VARBIT_AGITATOR_POTION = 11340;
    public static final int VARBIT_RETORT_POTION = 11341;
    public static final int VARBIT_ALEMBIC_POTION = 11342;

    public static final int VARBIT_DIGWEED_NORTH_EAST = 11330;
    public static final int VARBIT_DIGWEED_SOUTH_EAST = 11331;
    public static final int VARBIT_DIGWEED_SOUTH_WEST = 11332;
    public static final int VARBIT_DIGWEED_NORTH_WEST = 11333;
}
