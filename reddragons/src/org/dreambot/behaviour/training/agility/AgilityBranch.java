package org.dreambot.behaviour.training.agility;



import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class AgilityBranch extends Fractal {
    public AgilityBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Agility");
        addChildren(
//                new GnomeLeaf(),
                new GnomeFractal().setSimpleName("Gnome"),
                new DraynorFractal().setSimpleName("Draynor"),
                new CanifisFractal().setSimpleName("Canifis")
//                new DraynorLeaf(),
//                new VarrockLeaf(),
//                new FaladorLeaf()

        );
    }

}
