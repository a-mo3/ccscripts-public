package org.dreambot.behaviour.method.calvarion.tickcalv;

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
import org.dreambot.behaviour.method.calvarion.CalvarionData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PVMUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CalvarionTickLightning extends TickDecision {
    public CalvarionTickLightning() {
        setSimpleName("Dodge lightning");
    }

    @Override
    public boolean evaluate() {
        List<GraphicsObject> lightnings = GraphicsObjects.all(x -> CalvarionData.isCalvarionAttack(x.getId()));
        if (lightnings.isEmpty()) {
            return false;
        }
        List<Area> lightningAreas = lightnings.stream().map(x -> x.getTile().getArea(1)).collect(Collectors.toList());
        Player local = Players.getLocal();
        if (lightningAreas.stream().noneMatch(x -> x.contains(local))) {
            return false;
        }

        List<NPC> hellhounds = NPCs.all(x -> x.getName().contains("hound"));

        // consider special attack and walk under
        NPC transform = NPCs.closest(x -> CalvarionData.isTransformCalvarion(x.getId()));
        if (transform != null) {
            log("Trasnform run under");
            Tile bestTile = Arrays.stream(Players.getLocal().getSurroundingArea(3).getTiles())
                    .filter(Locatable::canReach)
                    .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                    .min(Comparator.comparingDouble(Tile::distance))
                    .orElse(null);

            if (!Players.getLocal().getTile().equals(bestTile)) {
                Walking.walkExact(bestTile);
            }
            return true;
        }


        // if hellhounds are present, find a tile next to the hellhound, that is not on or 1 distance from a lightening
        if (!hellhounds.isEmpty()) {
            NPC hound = hellhounds.get(0);
            Tile[] attackHoundTiles = PVMUtil.attackableTiles(hound, 1);
            Tile bestTile = Arrays.stream(attackHoundTiles)
                    .filter(Locatable::canReach)
                    .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                    .sorted(Comparator.comparingDouble(Tile::distance))
                    .findFirst().orElse(null);
            if (bestTile == null) {
                log("Couldnt find best tile");
                bestTile = Arrays.stream(Players.getLocal().getSurroundingArea(3).getTiles())
                        .filter(Locatable::canReach)
                        .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                        .min(Comparator.comparingDouble(Tile::distance))
                        .orElse(null);
            }

            if (bestTile != null && !Players.getLocal().getTile().equals(bestTile)) {
                Walking.walkExact(bestTile);
            } else {
                hellhounds.get(0).interact("Attack");
            }
            return true;
        }

        // no hellhounds, find a tile next to calvarion that is not on or next to a lightening
        NPC calvarion = NPCs.closest("Calvar'ion");
        if (calvarion == null) {
            log("Calvarion is null");
            Tile bestTile = Arrays.stream(Players.getLocal().getSurroundingArea(3).getTiles())
                    .filter(Locatable::canReach)
                    .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                    .min(Comparator.comparingDouble(Tile::distance))
                    .orElse(null);

            if (bestTile != null && !Players.getLocal().getTile().equals(bestTile)) {
                Walking.walkExact(bestTile);
            }
            return true;
        }

        Tile[] attackCalvTiles = PVMUtil.attackableTiles(calvarion, 2);
        Tile bestTile = Arrays.stream(attackCalvTiles)
                .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                .filter(Locatable::canReach)
                .min(Comparator.comparingDouble(Tile::distance))
                .orElse(null);
        if (bestTile == null) {
            log("Couldnt find best tile");
            bestTile = Arrays.stream(Players.getLocal().getSurroundingArea(3).getTiles())
                    .filter(Locatable::canReach)
                    .filter(x -> lightningAreas.stream().noneMatch(l -> l.contains(x)))
                    .min(Comparator.comparingDouble(Tile::distance))
                    .orElse(null);
        }
        if (bestTile != null && !Players.getLocal().getTile().equals(bestTile)) {
            Walking.walkExact(bestTile);
        } else {
            calvarion.interact("Attack");
        }
        // consider a sleep until here
        return true;
    }
}
