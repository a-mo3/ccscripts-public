package org.dreambot.behaviour.training.hunter.sallys;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class SalamanderBranch extends Fractal {

    Area redSal = new Area(
            new Tile(2449, 3228, 0),
            new Tile(2459, 3220, 0),
            new Tile(2453, 3211, 0),
            new Tile(2442, 3225, 0)
    );

    Area blackSal = new Area(
            new Tile(3291, 3678, 0),
            new Tile(3299, 3680, 0),
            new Tile(3302, 3672, 0),
            new Tile(3300, 3661, 0),
            new Tile(3291, 3668, 0));

    public SalamanderBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Salamander Hunter");
        addChildren(
//          new GenericSalamander(() -> Skill.HUNTER.getLevel() < 29) // todo swamp lizards require mortyania
                new GenericSalamander(() -> Skill.HUNTER.getLevel() < 67, redSal).setSimpleName("Reds"),
                new GenericSalamander(() -> Skill.HUNTER.getLevel() < 79, blackSal).setSimpleName("Blacks")

        );
    }
}
