package org.dreambot.behaviour.woodcutting;

import org.dreambot.api.methods.map.Area;

public enum NormalTreeLocation {
    TREE_LOC_1(new Area(3185, 3227, 3198, 3208)),
    TREE_LOC_2(new Area(3166, 3227, 3179, 3209)),
    TREE_LOC_3(new Area(3203, 3250, 3191, 3235)),
    TREE_LOC_4(new Area(3138, 3256, 3156, 3235)),
    TREE_LOC_5(new Area(2988, 3222, 3006, 3197)),
    TREE_LOC_6(new Area(2943, 3237, 2963, 3223)),
    TREE_LOC_7(new Area(2983, 3267, 3008, 3248)),
    TREE_LOC_8(new Area(3031, 3278, 3043, 3256)),
    TREE_LOC_9(new Area(3045, 3326, 3064, 3315)),
    TREE_LOC_10(new Area(3019, 3323, 3041, 3315)),
    TREE_LOC_11(new Area(2969, 3303, 2999, 3285)),
    TREE_LOC_12(new Area(2962, 3458, 2989, 3430)),
    TREE_LOC_13(new Area(3038, 3455, 3063, 3417)),
    TREE_LOC_14(new Area(3117, 3444, 3140, 3422)),
    TREE_LOC_15(new Area(3157, 3417, 3171, 3396)),
    TREE_LOC_16(new Area(3157, 3395, 3171, 3371)),
    TREE_LOC_17(new Area(3194, 3375, 3213, 3360)),
    TREE_LOC_18(new Area(3303, 3351, 3267, 3332)),
    TREE_LOC_19(new Area(3271, 3459, 3285, 3410)),
    TREE_LOC_20(new Area(3265, 3484, 3293, 3466)),
    TREE_LOC_21(new Area(3249, 3519, 3272, 3494)),
    TREE_LOC_22(new Area(3197, 3520, 3239, 3508)),
    TREE_LOC_23(new Area(3113, 3517, 3134, 3495)),
    TREE_LOC_24(new Area(2965, 3500, 2981, 3474)),
    TREE_LOC_25(new Area(3000, 3422, 3030, 3394)),
    ;

    public final Area treeLocation;

    NormalTreeLocation(Area treeLocation) {
        this.treeLocation = treeLocation;
    }
}
