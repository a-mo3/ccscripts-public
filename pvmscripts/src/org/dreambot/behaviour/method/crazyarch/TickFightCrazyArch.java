package org.dreambot.behaviour.method.crazyarch;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.CombatUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TickFightCrazyArch extends TickDecision {
    final Skill mode;

    public TickFightCrazyArch(Skill mode) {
        this.mode = mode;
    }

    public static final Area RUINS_AREA = new Area(2970, 3714, 2985, 3695);
    boolean hopWorld = false;

    @Override
    public boolean evaluate() {
        Player threat = CombatUtil.getThreat();
        if (threat != null) {
            log("Threat present " + threat);
            Walking.walk(BankLocation.GRAND_EXCHANGE);
            hopWorld = true;
            return true;
        }

        if (!RUINS_AREA.contains(Players.getLocal())) {
            if (hopWorld && !Combat.isInWild()) {
                log("Hop world");
                hopWorld = false;
                WorldHopper.hopWorld(Worlds.getRandomWorld(GetOff330.MEMBERS_WORLD_FILTER));
                Sleep.sleep(6_000);
                return true;
            }

            log("Go to ruins");
            if (Walking.shouldWalk()) Walking.walk(RUINS_AREA);
            return true;
        }
        // the 3x3 exploding books
        List<Projectile> books = Projectiles.all(1260);

        NPC crazy = NPCs.closest("Crazy archaeologist");
        if (crazy == null) {
            log("No crazy archaeologist");
            // take loot
            GroundItem loot = GroundItems.closest(x -> RUINS_AREA.contains(x.getTile()) && x.getItem().getLivePrice() > 1000);
            if (loot != null) {
                log("loot " + loot.getName());
                loot.interact();
            }
            return true;
        }

        if (!books.isEmpty()) {
            log("Books present");
            // areas where you will take damage
            List<Area> bookRadius = books.stream().map(x -> x.getTargetTile().getArea(1))
                    .collect(Collectors.toList());

            // if you are in one, get away from it
            if (bookRadius.stream().anyMatch(x -> x.contains(Players.getLocal()))) {
                log("In a dangerous spot, get out of it");
                Tile safe = null;
                if (mode == Skill.MAGIC) {
                    // magic mode safe tile, get distance
                    log("Magic dodge");

                    safe = Arrays.stream(Players.getLocal().getSurroundingArea(6)
                                    .getTiles())
                            .filter(x -> bookRadius.stream().noneMatch(i -> i.contains(x)))
                            .min(Comparator.comparingDouble(Tile::distance))
                            .orElse(null);
                    if (safe == null) {
                        log("safe was not found");
                        return false;
                    }

                    if (!Players.getLocal().getTile().equals(safe)) {
                        log("Get onto safe");
                        Walking.walkExact(safe);
                        return false;
                    } else {
                        if (!crazy.equals(Players.getLocal().getInteractingCharacter()) && crazy.distance() < 7) {
                            log("Attack crazy");
                            crazy.interact("Attack");
                        }
                    }
                } else {
                    // melee mode safe tile, want to be next to
                }

                return true;
            }
            // if you are not in one,
            return false;
        }

        // not in an area attack the arch
        if (!crazy.equals(Players.getLocal().getInteractingCharacter())) {
            log("Attack crazy");
            crazy.interact("Attack");
        }

        return false;
    }

}
