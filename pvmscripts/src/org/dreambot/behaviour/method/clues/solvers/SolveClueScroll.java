package org.dreambot.behaviour.method.clues.solvers;

import org.dreambot.behaviour.method.clues.CheckClueScroll;
import org.dreambot.fractals.Fractal;

/**
 * gets the last set instance of whatever clue is appropriate and runs its solve method
 */
public class SolveClueScroll extends Fractal {
    @Override
    public int onLoop() {
        return CheckClueScroll.lastClue.solve();
    }
}
