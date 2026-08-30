package org.dreambot.behaviour.method.huey.mainfight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.util.Direction;
import org.dreambot.api.wrappers.map.Region;
import org.dreambot.behaviour.method.huey.HueyData;
import org.dreambot.behaviour.method.huey.HueyLoadout;
import org.dreambot.behaviour.quests.perilousmoon.InstanceWalking;
import org.dreambot.fractals.TickDecision;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;

public class HueyMainAttack extends TickDecision {
    HueyLoadout mode;

    public HueyMainAttack(HueyLoadout loadout) {
        mode = loadout;
    }

    // the side it was on not anything to do with wave
    public static Direction lastTailSide = Direction.WEST;

    @Override
    public boolean evaluate() {
        // or huey tail, if its active and after the dodge tick
        NPC tail = NPCs.closest("Hueycoatl tail");
        if (tail != null) {
            log("Tail attack portion");
            if (tail.getX() < Players.getLocal().getX()) {
                // if the npc is to the east, then the wave is east to west, and we call that the west wave, so the next one will be east
                if (lastTailSide != Direction.WEST) {
                    log("Tail switch side to west");
                    lastTailSide = Direction.WEST;
                    HueyWaveDodge.moveTick = Client.getGameTick() + 2;
                    Walking.walkExact(HueyData.getDiagTile());
                    return false;
                }
            } else {
                if (lastTailSide != Direction.EAST) {
                    log("Tail switch side to east");
                    lastTailSide = Direction.EAST;
                    HueyWaveDodge.moveTick = Client.getGameTick() + 2;
                    Walking.walkExact(HueyData.getDiagTile());
                    return false;
                }
            }
            // stay  above y  3284 or you'll be hit with wave
            if (Client.isDynamicRegion() ? Region.fromInstance(Players.getLocal().getServerTile()).getY() < 3285 : Players.getLocal().getY() < 3285) {
                log("We need to be higher for wave pos");
                Walking.walkExact(Players.getLocal().getTile().translate(0, 4));
                return false;
            }

            // wait until theres no graphic objects on the dodge tile before
            if (!tail.equals(Players.getLocal().getInteractingCharacter()) && HueyWaveDodge.moveTick <= Client.getGameTick()) {
                log("Tail not is target");
                if (lastTailSide == Direction.EAST) {
                    // east
                    GraphicsObject waveOnDodge = GraphicsObjects.closest(x -> x.getTile().equals(HueyData.getEasternWaveDodge()));
                    if (waveOnDodge == null) {
                        log("Eastern attack");
                        tail.interact("Attack");
                    }
                } else {
                    // west
                    GraphicsObject waveOnDodge = GraphicsObjects.closest(x -> x.getTile().equals(HueyData.getWesternWaveDodge()));
                    if (waveOnDodge == null) {
                        log("Western attack");
                        tail.interact("Attack");
                    }
                }
            }


//            if (Client.getGameTick() >= HueyWaveDodge.moveTick && !tail.equals(Players.getLocal().getInteractingCharacter())) {
//                log("Actually attack the tail " + (tail.getX() - Players.getLocal().getX()));
//                tail.interact("Attack");
//            }
            return false;
        }

        // make sure when we attack huey body we are on one of the tiles that makes it easy to dodge wave
        if (!Client.isDynamicRegion() && !HueyData.HUEY_MAIN_AREA.contains(Players.getLocal())) {
            log("Get into main fight");
            if (Walking.shouldWalk()) InstanceWalking.walkExact(HueyData.HUEY_MAIN_AREA.getCenter());
            return true;
        }

        // ensure we are on the correct east or west tile from huey that the wave doesnt reach
        // should be handled in huey wave dodge

        if (mode.getMode() != Skill.MAGIC && !HueyData.getHueyAttackTiles().contains(Players.getLocal().getServerTile())) {
            //  dont rly want to be on these in the 2nd half of the fight
            log("Get on correct huey attack tile");
            Tile best = HueyData.getHueyAttackTiles()
                    .stream()
                    .sorted(Comparator.comparingDouble(Tile::distance))
                    .findFirst()
                    .orElse(null);
            if (best == null) log("Null best");
            if (best != null) Walking.walkExact(best);
            return true;
        }

        // attack huey body
        NPC hueyBody = NPCs.closest("The Hueycoatl");
        if (hueyBody != null) {
            if (!hueyBody.equals(Players.getLocal().getInteractingCharacter())) hueyBody.interact("Attack");
        } else {
            log("Failed to find huey body");
        }

        return false;
    }
}
