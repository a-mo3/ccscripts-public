package org.dreambot.behaviour.training.woodcutting;

import org.dreambot.api.methods.map.Area;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;

import java.util.function.Supplier;

public class AllOfTheLogs extends Fractal {
    Area[] trees = {
            new Area(3192, 3249, 3206, 3236),
            new Area(3187, 3228, 3196, 3213),
            new Area(3165, 3227, 3175, 3213),
            new Area(3173, 3261, 3189, 3251),
            new Area(3162, 3286, 3182, 3278),
            new Area(3235, 3277, 3250, 3266),
            new Area(3257, 3254, 3264, 3244),
            new Area(3272, 3355, 3284, 3343),
            new Area(3289, 3345, 3299, 3334),
            new Area(3261, 3372, 3276, 3361),
            new Area(3198, 3374, 3213, 3360),
            new Area(3158, 3396, 3170, 3375),
            new Area(3137, 3405, 3148, 3394),
            new Area(3158, 3418, 3168, 3404),
            new Area(3135, 3431, 3147, 3420),
            new Area(3125, 3443, 3144, 3433),
            new Area(3150, 3460, 3165, 3450),
            new Area(3042, 3435, 3053, 3420),
            new Area(3039, 3465, 3060, 3456),
            new Area(3042, 3451, 3051, 3438),
            new Area(2988, 3423, 3012, 3414),
            new Area(2962, 3457, 2977, 3444),
            new Area(2975, 3500, 2984, 3484),
            new Area(2947, 3418, 2959, 3396),
            new Area(2998, 3318, 3006, 3300),
            new Area(3014, 3323, 3029, 3314),
            new Area(3043, 3327, 3062, 3316),
            new Area(3034, 3275, 3053, 3261),
            new Area(2996, 3260, 3006, 3244),
            new Area(2970, 3300, 2984, 3285),
            new Area(2951, 3282, 2970, 3263),
            new Area(2986, 3222, 3007, 3208),
            new Area(3013, 3177, 3029, 3167),
            new Area(2946, 3241, 2955, 3228),
            new Area(3271, 3458, 3281, 3444),
            new Area(3265, 3483, 3282, 3471),
            new Area(3264, 3512, 3273, 3494),
            new Area(3200, 3519, 3222, 3510),
            new Area(3289, 3494, 3298, 3479),
            new Area(3123, 3220, 3139, 3200),
            new Area(3097, 3310, 3109, 3295),
            new Area(3093, 3413, 3104, 3400),
            new Area(3139, 3255, 3153, 3237),
            new Area(3032, 3340, 3058, 3331),
            new Area(2996, 3363, 3004, 3355),
            new Area(2911, 3314, 2935, 3301),
            new Area(2942, 3324, 2956, 3313),
            new Area(3014, 3410, 3029, 3395),
            new Area(3080, 3454, 3098, 3462),
            new Area(3112, 3513, 3122, 3499),
            new Area(3157, 3515, 3140, 3498),
            new Area(3242, 3372, 3257, 3361),
            new Area(2995, 3171, 3009, 3161),
            new Area(2957, 3238, 2963, 3226),
            new Area(2920, 3236, 2938, 3226),
            new Area(2986, 3301, 2996, 3286),
            new Area(3279, 3520, 3292, 3512)
    };

    public AllOfTheLogs(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        addChildren(
                new GenericChopLeaf(() -> true, trees[ShuffleFractal.getLoginValue() % trees.length],
                        x -> x.getName().equals("Tree") || x.getName().equals("Evergreen tree") || x.getName().equals("Dead tree"))
                        .setBankLogs(true)
                        .setInventoryLoadout(MixedChopping.AXE_LOADOUT)
                        .setSimpleName("Trees Area: " + ShuffleFractal.getLoginValue() % trees.length)
        );
    }


}
