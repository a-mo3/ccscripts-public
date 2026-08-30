package org.dreambot.behaviour.method.callisto.tickcallisto;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.callisto.CallistoData;
import org.dreambot.behaviour.method.callisto.GoToCallisto;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * safely navigate through traps if you're too close to callisto
 * if we cant kind a safe route, just run somewherej
 */
public class CallistoGetGap extends TickDecision {

    public static final int BEAR_TRAP_ID = 47146;

    @Override
    public boolean evaluate() {
        NPC callisto = NPCs.closest(CallistoData.CALLISTO_NAME);
        if (callisto == null) {
            log("Failed to find callisto");
            return false;
        }

        log("Gap check dist " + calServerAdj(callisto).distance());
        if (calServerAdj(callisto).distance() > 8) {
            // to avoid convergence and edge cases we walk perpendicularly to a safter, unoccupied tile
//            if (Players.closest(x -> !x.equals(Players.getLocal()) && x.getTile().equals(Players.getLocal().getTile())) != null) {
//                log("We're ontop of some else");
//                List<Tile> validWalkTiles = Arrays.stream(Players.getLocal().getTile().getArea(8).getTiles())
//                        // far enough from callisto to safe
//                        .filter(x -> x.distance(calServerAdj(callisto)) > 8)
//                        // perpendicular
//                        .filter(x -> x.getX() == Players.getLocal().getX() || x.getY() == Players.getLocal().getY())
//                        .collect(Collectors.toList());
//                if (validWalkTiles.isEmpty())  return false;
//                if (Walking.shouldWalk()) Walking.walkExact(validWalkTiles.get(Calculations.random(validWalkTiles.size())));
//            }

            return false;
        }

        List<Tile> traps = GameObjects.all(BEAR_TRAP_ID)
                .stream()
                .map(GameObject::getTile)
                .collect(Collectors.toList());

        log("Get gap");
        Tile t = Arrays.stream(GoToCallisto.CALLISTO_ARENA.getTiles())
                .filter(x -> x.canReach() && x.distance(calServerAdj(callisto)) > 8 && x.distance(calServerAdj(callisto)) < 12)
                .filter(x -> !traps.contains(x))
                .min(Comparator.comparingDouble(Tile::distance)).orElse(null);
        if (t != null && Walking.getDestination() == null) {
            log("Walk to t");
            Walking.walkExact(t);
            Sleep.sleep(300);
            return false;
        }

        // trap dodge


        return false;
    }

    private Tile calServerAdj(NPC cal) {
        return cal.getServerTile().translate(2,2);
    }
}
