package org.dreambot.behaviour.tutorial;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class WalkAbout extends Fractal {
    final Timer t;

    // bunch of random locations in f2p
    Area[] areas = {
            new Area(3237, 3169, 3243, 3162),
            new Area(3215, 3253, 3223, 3243),
            new Area(3245, 3270, 3248, 3260),
            new Area(3258, 3289, 3262, 3282),
            new Area(3162, 3376, 3173, 3365),
            new Area(3117, 3407, 3131, 3399),
            new Area(3125, 3440, 3137, 3423),
            new Area(3042, 3448, 3054, 3438),
            new Area(3052, 3334, 3037, 3332),
            new Area(2961, 3381, 2966, 3374),
            new Area(2966, 3341, 2973, 3337),
            new Area(2973, 3300, 2984, 3290),
            new Area(2955, 3276, 2966, 3266),
            new Area(2994, 3256, 2998, 3249),
            new Area(2979, 3238, 2983, 3230),
            new Area(2951, 3222, 2962, 3215),
            new Area(3010, 3159, 3016, 3150),
            new Area(2969, 3449, 2977, 3442),
            new Area(3142, 3248, 3138, 3262),
            new Area(3092, 3301, 3104, 3293),
            new Area(3076, 3254, 3084, 3247),
            new Area(3043, 3248, 3045, 3246),
            new Area(2990, 3209, 2998, 3197),
            new Area(3066, 3357, 3071, 3344),
            new Area(3022, 3462, 3027, 3457),
            new Area(3050, 3495, 3053, 3485)
    };

    Area target;
    boolean firstLoop = true;

    public WalkAbout(int mins) {
        this.t = new Timer((long) mins * 60 * 1000);
    }

    @Override
    public int onLoop() {
        if (firstLoop) {
            t.reset();
            firstLoop = false;
        } else {
            if (t.finished()) {
                log("Script finished");
                return -1;
            }
        }

        if (target == null) target = areas[Calculations.random(0, areas.length - 1)];
        if (target.contains(Players.getLocal())) {
            log("Reached target");
            target = areas[Calculations.random(0, areas.length - 1)];
        } else {
            if (Walking.shouldWalk()) Walking.walk(target);
        }
        return ReactionGenerator.getNormal();
    }
}
