package org.dreambot.behaviour.method.vetion.tickvetion;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.Locatable;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.vetion.VetionData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PVMUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VetionTickLightning extends TickDecision {
    public VetionTickLightning() {
        setSimpleName("Dodge lightning");
    }

    @Override
    public boolean evaluate() {
        List<GraphicsObject> lightnings = GraphicsObjects.all(x -> VetionData.isVetionAttack(x.getId()));
        if (lightnings.isEmpty()) {
            return false;
        }
        List<Area> lightningAreas = lightnings.stream().map(x -> x.getTile().getArea(1)).collect(Collectors.toList());
        Player local = Players.getLocal();
        if (lightningAreas.stream().noneMatch(x -> x.contains(local))) {
            return false;
        }

        List<NPC> hellhounds = NPCs.all(x -> x.getName().contains("hound"));

        // if hellhounds are present, find a tile next to the hellhound, that is not on or 1 distance from a lightening
        if (!hellhounds.isEmpty()) {
            NPC hound = hellhounds.get(0);
            Tile[] attackHoundTiles = PVMUtil.attackableTiles(hound, 1);
            Tile bestTile = Arrays.stream(attackHoundTiles)
                    .filter(Locatable::canReach)
                    .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                    .sorted((i, j) -> Calculations.random(-1, 2)) // randomly select an acceptable tile so bots dont converge to 1 tile and get ice barraged to lum
                    .findAny()
                    .orElse(null);
            if (bestTile == null) {
                log("Couldnt find best tile");
                bestTile = Arrays.stream(Players.getLocal().getSurroundingArea(3).getTiles())
                        .filter(Locatable::canReach)
                        .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                        .sorted((i, j) -> Calculations.random(-1, 2)) // randomly select an acceptable tile so bots dont converge to 1 tile and get ice barraged to lum
                        .findAny()
                        .orElse(null);
            }

            if (bestTile != null && !isSafelyWalk(lightningAreas)) {
                Walking.walkExact(bestTile);
            } else {
//                hellhounds.get(0).interact("Attack");
            }
            return true;
        }

        // no hellhounds, find a tile next to calvarion that is not on or next to a lightening
        NPC vetion = NPCs.closest(VetionData.VETION_NAME);
        if (vetion == null) {
            log("vetion is null");
            Tile bestTile = Arrays.stream(Players.getLocal().getSurroundingArea(3).getTiles())
                    .filter(Locatable::canReach)
                    .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                    .sorted((i, j) -> Calculations.random(-1, 2)) // randomly select an acceptable tile so bots dont converge to 1 tile and get ice barraged to lum
                    .findAny()
                    .orElse(null);

            if (bestTile != null && !Players.getLocal().getTile().equals(bestTile)) {
                Walking.walkExact(bestTile);
            }
            return true;
        }

        Tile[] attackVetionTiles = PVMUtil.attackableTiles(vetion, 2);
        Tile bestTile = Arrays.stream(attackVetionTiles)
                .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                .filter(Locatable::canReach)
                .sorted((i, j) -> Calculations.random(-1, 2)) // randomly select an acceptable tile so bots dont converge to 1 tile and get ice barraged to lum
                .findAny()
                .orElse(null);
        if (bestTile == null) {
            log("Couldnt find best tile");
            bestTile = Arrays.stream(Players.getLocal().getSurroundingArea(3).getTiles())
                    .filter(Locatable::canReach)
                    .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                    .sorted((i, j) -> Calculations.random(-1, 2)) // randomly select an acceptable tile so bots dont converge to 1 tile and get ice barraged to lum
                    .findAny()
                    .orElse(null);
        }
        if (bestTile != null && !isSafelyWalk(lightningAreas)) {
            Walking.walkExact(bestTile);
        } else {
//            vetion.interact("Attack");
        }
        // consider a sleep until here
        return true;
    }

    /**
     * we cant just check equivalence because we walk randomly, we need to check safety
     * safety means our dest is non null, and is not in any of the lightning areas
     */
    private boolean isSafelyWalk(List<Area> lightning) {
        Tile dest = Walking.getDestination();
        if (dest == null) {
            return lightning.stream().noneMatch(x -> x.contains(Players.getLocal()));
        } else {
            return lightning.stream().noneMatch(x -> x.contains(Walking.getDestination()));
        }

    }
}
