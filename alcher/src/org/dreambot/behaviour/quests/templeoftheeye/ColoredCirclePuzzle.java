package org.dreambot.behaviour.quests.templeoftheeye;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public class ColoredCirclePuzzle extends Fractal {
    enum CircleColours {
        BLUE("Law"),
        BROWN("Earth"),
        GREY("Death"),
        YELLOW("Cosmic"),
        GREEN("Nature"),
        RED("Fire"),
        ;

        final String energy;

        CircleColours(String energy) {
            this.energy = energy;
        }
    }

    int step = -1;
    public ColoredCirclePuzzle(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.paintArraySupplier = () -> new String[]{
            "Step" + step
        };
    }

    // i array represent index j all the options you can choose, once the correct option is discovered set all others to null
    CircleColours[][] puzzleState = new CircleColours[][]{
            CircleColours.values(),
            CircleColours.values(),
            CircleColours.values(),
            CircleColours.values(),
            CircleColours.values(),
            CircleColours.values(),
    };

    @Override
    public int onLoop() {
        if (Walking.getRunEnergy() > 50 && !Walking.isRunEnabled()) {
            Walking.toggleRun();
        }
        Logger.info("--------------------");
        for (CircleColours[] index : puzzleState) {
            StringBuilder options = new StringBuilder();
            for (CircleColours option : index) {
                if (option == null) continue;
                options.append(option).append(" ");
            }
            Logger.info(options.toString());
        }

        // we will go in descending order
        step = GameObjects.all(x -> x.hasAction("Touch")).size() - 1;
        if (step > puzzleState.length - 1 || step < 0) {
            Logger.format("Fucked up da math step %d", step);
            return ReactionGenerator.getNormal();
        }

        if (step == 0) {
            GameObject circle = GameObjects.closest(x -> x.hasAction("Touch"));
            if (circle != null && circle.interact()) {
                Sleep.sleep(4000);
            }
            return ReactionGenerator.getLong();
        }

        CircleColours[] move = puzzleState[step];
        CircleColours jStep = Arrays.stream(move).filter(Objects::nonNull).findFirst().orElse(null);
        if (jStep == null) {
            Logger.warn("jStep null");
            return ReactionGenerator.getNormal();
        }

        GameObject circle = GameObjects.closest(x -> x.hasAction("Touch") && x.getName().contains(jStep.energy));
        if (circle == null) {
            // remove this option from possible options
            for (int i = 0; i <= step; i++) {
                if (jStep == puzzleState[step][i]) {
                    Logger.info("removing " + jStep);
                    puzzleState[step][i] = null;
                }
            }
            return ReactionGenerator.getLong();
        }

        if (circle.interact()) {
            if (Sleep.sleepUntil(() -> GameObjects.all(x -> x.hasAction("Touch")).size() - 1 < step,
                    () -> Players.getLocal().isMoving(),
                    4_000,
                    300)) {
                // didnt time out, probably that was the right choice
                // make everything other than the right one null.
                Logger.format("Found solution for Step: %d Solution: %s", step, jStep);
                puzzleState[step] = new CircleColours[]{jStep, null, null, null, null};
                return ReactionGenerator.getLong();
            }
            // remove this option from possible options
            for (int i = 0; i <= step; i++) {
                if (jStep == puzzleState[step][i]) {
                    Logger.info("removing " + jStep);
                    puzzleState[step][i] = null;
                }
            }
            return ReactionGenerator.getLong();
        }

        return ReactionGenerator.getNormal();
    }
}
