package org.dreambot.behaviour.method.vetion;

import java.util.Arrays;
import java.util.List;

public class VetionData {
    public static final int LESSER_VETION_ID = 6611;
    public static final int GREATER_VETION_ID = 6612;
//    public static final int TRANSFORM_CALVARION_ID = 11995; // when he transforms and you should sit under him

    public static final int LESSER_HELLHOUND_ID = 6613;
    public static final int GREATER_HELLHOUND_ID = 6614;

    public static final int LESSER_LIGHTENING_ID = 2346; // game graphic objects
    public static final int GREATER_LIGHTENING_ID = 2347;

    public static final int[] LESSER_AOE_ATTACK = new int[]{1446, 2184};
    public static final String VETION_NAME = "Vet'ion";

    static List<Integer> attacks = Arrays.asList(
            1446, 2184, LESSER_LIGHTENING_ID, GREATER_LIGHTENING_ID
    );

    public static boolean isVetionAttack(int id) {
        return attacks.contains(id);
    }

//    public static boolean isTransformVetion(int id) {
//        return id >= TRANSFORM_CALVARION_ID && id < 11997;
//    }
}
