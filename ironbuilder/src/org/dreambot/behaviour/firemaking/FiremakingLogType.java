package org.dreambot.behaviour.firemaking;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.function.Supplier;

public enum FiremakingLogType {
    LOGS(0, 0, new Area[]{
            new Area(3150, 3463, 3171, 3449),
            new Area(3159, 3417, 3170, 3400),
            new Area(3160, 3397, 3171, 3375),
            new Area(3138, 3404, 3148, 3391),
            new Area(3135, 3428, 3148, 3419),
            new Area(3274, 3356, 3286, 3346)
    }, () -> GameObjects.closest("Tree")),
    OAK(15, 15, new Area[]{
            new Area(3198, 3229, 3179, 3208),
            new Area(3136, 3406, 3148, 3392),
            new Area(3053, 3452, 3066, 3424),
            new Area(3037, 3452, 3052, 3423),
            new Area(3037, 3468, 3061, 3454)
    }, () -> GameObjects.closest(x -> "Oak tree".equals(x.getName())))
    ;

    final int wcReq;
    final int fmReq;
    final Area[] areas;
    final Supplier<GameObject> treeSupplier;

    FiremakingLogType(int wcReq, int fmReq, Area[] areas, Supplier<GameObject> treeSupplier) {
        this.wcReq = wcReq;
        this.fmReq = fmReq;
        this.areas = areas;
        this.treeSupplier = treeSupplier;
    }

    Area getRandomArea() {
        return areas[Calculations.random(areas.length)];
    }
}
