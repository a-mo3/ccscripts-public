package org.dreambot.behaviour.method.calvarion;

import java.util.Arrays;
import java.util.List;

public class CalvarionData {
    public static final int LESSER_CALVARION_ID = 11993;
    public static final int GREATER_CALVARION_ID = 11994;
    public static final int TRANSFORM_CALVARION_ID = 11995; // when he transforms and you should sit under him

    public static final int LESSER_HELLHOUND_ID = 12107;
    public static final int GREATER_HELLHOUND_ID = 12108;

    public static final int LESSER_LIGHTENING_ID = 2346; // game graphic objects
    public static final int GREATER_LIGHTENING_ID = 2347;

    public static final int[] LESSER_AOE_ATTACK = new int[]{1446, 2184};

    static List<Integer> attacks = Arrays.asList(
            1446, 2184, LESSER_LIGHTENING_ID, GREATER_LIGHTENING_ID
    );

    public static boolean isCalvarionAttack(int id) {
        return attacks.contains(id);
    }

    public static boolean isTransformCalvarion(int id) {
        return id >= TRANSFORM_CALVARION_ID && id < 11997;
    }
}
