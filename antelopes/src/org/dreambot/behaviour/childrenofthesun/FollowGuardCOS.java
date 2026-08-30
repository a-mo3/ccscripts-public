package org.dreambot.behaviour.childrenofthesun;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FollowGuardCOS extends Fractal {

    Map<Area, Tile> followGuardMap = new HashMap<>();

    public FollowGuardCOS(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    Tile currentSafespot = null;
    Tile instanceStartPos = new Tile(1, 1);

    Timer spotOneTimer = new Timer(5000);
    Timer spotTwoTimer = new Timer(20_000);
    Timer spotThreeTimer = new Timer(35_000);
    Timer spotFourTimer = new Timer(57_000);
    Timer spotFiveTimer = new Timer(77_000);

    // tiles here are actually offset values from the start pos
    Tile spotOne = new Tile(7, -1, 0);
    Tile spotTwo = new Tile(14, -11, 0);
    Tile spotThree = new Tile(15, -25, 0);
    Tile spotFour = new Tile(10, -36, 0);
    Tile spotFive = new Tile(21, -37, 0);

    @Override
    public int onLoop() {
        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 1) Walking.toggleRun();
        if (instanceStartPos.distance() > 1000) {
            Logger.info("Entered instance");
            instanceStartPos = Players.getLocal().getTile();
            spotOneTimer.reset();
            spotTwoTimer.reset();
            spotThreeTimer.reset();
            spotFourTimer.reset();
            spotFiveTimer.reset();
        }

        // have some timers decide which safespot offset
        if (spotOneTimer.finished()) currentSafespot = getOffset(spotOne);
        if (spotTwoTimer.finished()) currentSafespot = getOffset(spotTwo);
        if (spotThreeTimer.finished()) currentSafespot = getOffset(spotThree);
        if (spotFourTimer.finished()) currentSafespot = getOffset(spotFour);
        if (spotFiveTimer.finished()) currentSafespot = getOffset(spotFive);

        if (currentSafespot != null && !currentSafespot.equals(Players.getLocal().getTile())) {
            if (Walking.shouldWalk()) Walking.walkExact(currentSafespot);
        }
        return ReactionGenerator.getNormal();
    }

    private Tile getOffset(Tile t ) {
        return new Tile(instanceStartPos.getX() + t.getX(), instanceStartPos.getY() + t.getY());
    }
}
