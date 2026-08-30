package org.dreambot.behaviour.method.clues;

import org.dreambot.behaviour.method.clues.solvers.SolveClueScroll;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.OwnedItems;

import java.util.function.Supplier;

public class ClueScrollBranch extends Fractal {

    // null when we havent checked
    ClueScrollType currentClue;

    public ClueScrollBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Clue scroll");

        // this shit is going to have spaghetti relations
        addChildren(
                // check clue scroll type
                new CheckClueScroll().setSimpleName("Check clue scroll"),

                // solve certain type of clue
//                new EmoteClueSolver(() -> CheckClueScroll.currentScrollType == ClueScrollType.EMOTE),
//                new CoordinateClueSolver(() -> CheckClueScroll.currentScrollType == ClueScrollType.COORDINATE),
//                new AnagramClueSolver(() -> CheckClueScroll.currentScrollType == ClueScrollType.ANAGRAM),
//                new CrypticClueSolver(() -> CheckClueScroll.currentScrollType == ClueScrollType.CRYPTIC)
                new SolveClueScroll().setSimpleName("Solve")
        );
    }

    // might need a tier of clue scroll for this later
    private boolean ownsClueScroll() {
        return OwnedItems.contains(x -> x.getName().contains("Medium clue scroll"));
    }
}
