package org.dreambot.behaviour.magearenaone;


import org.dreambot.api.methods.map.Area;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class
MageArenaOneBranch extends Fractal {
    public static final Area MAGE_ARENA_BANK = new Area(2528, 4724, 2547, 4710);
    public static final Area MAGE_BANK_SWITCH = new Area(3090, 3958, 3091, 3956);
    public static final Area EDGEVILLE_SWITCH = new Area(3090, 3476, 3093, 3474);
    public static final Area MAGE_ARENA = new Area(3093, 3946, 3117, 3921);
    public static final Area CAPE_ROOM = new Area(2495, 4732, 2522, 4683);

    public MageArenaOneBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("MA1");
        addChildren(
                new GetMageCape().setSimpleName("Mage cape"),
                new FightKolodion().setSimpleName("Kolodian"),
                new StartMageArena(() -> true).setSimpleName("Mage arena")
        );
    }
}
