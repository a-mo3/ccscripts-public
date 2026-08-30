package org.dreambot.behaviour.fuckingaround;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.IronFractal;

import java.util.function.BooleanSupplier;

/**
 * Walks around the map
 */
public class WalkAbout extends IronFractal {
    Area[] exploreTargets = {
            new Area(3051, 3294, 3058, 3286),
            new Area(3026, 3308, 3030, 3303),
            new Area(3203, 3157, 3217, 3145),
            new Area(3150, 3196, 3161, 3179),
            new Area(3171, 3230, 3184, 3213),
            new Area(3170, 3332, 3180, 3321),
            new Area(3077, 3308, 3086, 3298),
            new Area(3141, 3247, 3150, 3240),
            new Area(3100, 3220, 3111, 3213),
            new Area(3101, 3155, 3107, 3147),
            new Area(3300, 3142, 3304, 3137),
            new Area(3324, 3168, 3327, 3162),
            new Area(3307, 3236, 3309, 3232),
            new Area(3324, 3272, 3329, 3269),
            new Area(3327, 3316, 3330, 3308),
            new Area(3276, 3324, 3279, 3318),
            new Area(3282, 3348, 3286, 3340),
            new Area(3300, 3339, 3305, 3333),
            new Area(3294, 3380, 3298, 3375),
            new Area(3275, 3482, 3277, 3476),
            new Area(3309, 3489, 3300, 3482),
            new Area(3252, 3515, 3259, 3510),
            new Area(3205, 3515, 3210, 3510),
            new Area(3220, 3487, 3222, 3484),
            new Area(3177, 3492, 3181, 3485),
            new Area(3202, 3493, 3204, 3491, 1),
            new Area(3152, 3437, 3154, 3434, 1),
            new Area(3202, 3398, 3206, 3396, 1),
            new Area(3215, 3418, 3218, 3414, 1),
            new Area(3260, 3450, 3263, 3445, 1),
            new Area(3156, 3392, 3161, 3385),
            new Area(3124, 3401, 3131, 3396),
            new Area(3124, 3440, 3128, 3431),
            new Area(3045, 3439, 3052, 3429),
            new Area(3037, 3356, 3041, 3351),
            new Area(2980, 3334, 2967, 3347),
            new Area(2941, 3305, 2946, 3299),
            new Area(2913, 3311, 2918, 3306),
            new Area(2947, 3259, 2953, 3249),
            new Area(2925, 3265, 2939, 3261),
            new Area(2929, 3227, 2936, 3218),
            new Area(2954, 3199, 2972, 3194),
            new Area(2954, 3217, 2959, 3209),
            new Area(3023, 3208, 3027, 3203),
            new Area(3049, 3204, 3042, 3202),
            new Area(3040, 3237, 3051, 3234),
            new Area(3016, 3162, 3020, 3157),
            new Area(2994, 3148, 3001, 3143),
            new Area(2947, 3455, 2955, 3447),
            new Area(2963, 3473, 2974, 3453),
            new Area(2954, 3505, 2957, 3500),
            new Area(2939, 3514, 2941, 3511),
            new Area(2980, 3516, 2985, 3509),
            new Area(3051, 3506, 3053, 3501),
            new Area(3055, 3484, 3059, 3483),
            new Area(3092, 3481, 3095, 3476),
            new Area(3156, 3302, 3160, 3297),
            new Area(3143, 3286, 3149, 3276),
            new Area(3117, 3285, 3123, 3278),
            new Area(3138, 3305, 3145, 3300),
            new Area(3198, 3295, 3204, 3288),
            new Area(3198, 3269, 3206, 3261),
            new Area(3187, 3268, 3190, 3265),
            new Area(3207, 3224, 3218, 3230),
            new Area(3241, 3199, 3244, 3192),
            new Area(3248, 3194, 3250, 3190),
            new Area(3235, 3175, 3241, 3166),
            new Area(3144, 3154, 3166, 3149),
            new Area(3023, 3252, 3028, 3245),
            new Area(2994, 3180, 2998, 3175),
            new Area(2996, 3196, 2990, 3230),
            new Area(2987, 3266, 3000, 3257),
            new Area(3106, 3364, 3109, 3360),
            new Area(3100, 3373, 3115, 3370, 1),
            new Area(3040, 3374, 3050, 3372, 1),
            new Area(2973, 3370, 2982, 3368, 1),
            new Area(2970, 3383, 2977, 3381, 1),
            new Area(2953, 3373, 2957, 3366, 1),
            new Area(2943, 3371, 2945, 3368),
            new Area(2942, 3386, 2944, 3378),
            new Area(2940, 3339, 2946, 3330),
            new Area(2944, 3316, 2953, 3312)
    };

    public WalkAbout(BooleanSupplier acceptCondition) {
        super(acceptCondition);
        setSimpleName("'Gan Walk About");
    }

    Area selected = exploreTargets[Calculations.random(exploreTargets.length)];

    @Override
    protected int onLoop() {
        if (!Inventory.isEmpty()) {
            log("Safety deposit");
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return sleep();
            }
            Bank.depositAllItems();
            return sleep();
        }

        if (!selected.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(selected);
            return sleep();
        }

        selected = exploreTargets[Calculations.random(exploreTargets.length)];
        return sleep(Calculations.random(10_000, 115_000));
    }
}
