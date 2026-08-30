package org.dreambot.behaviour.training.agility.wild;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.behaviour.training.agility.AreaUtils;
import org.dreambot.comms.impl.agility.BoxingClient;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.settings.timing.ReactionGenerator;

public class TickWildyGoToCourse extends TickDecision {
    public static final Area COURSE = new Area(2987, 3966, 3008, 3931);
    public static final Area SPIKE_AREA = new Area(2987, 10368, 3010, 10337);
    final WildernessAgilityMode mode;
    final int forceWorld;

    final Area PIPE_AREA = new Area(
            new Tile(2992, 3935, 0),
            new Tile(2992, 3930, 0),
            new Tile(3004, 3931, 0),
            new Tile(3007, 3934, 0),
            new Tile(3007, 3940, 0),
            new Tile(3002, 3939, 0)
    );

    final Tile COURSE_HOP_TILE = new Tile(3006, 3938, 0);

    public TickWildyGoToCourse(WildernessAgilityMode mode, int forceWorld) {
        this.mode = mode;
        this.forceWorld = forceWorld;
    }

    int suicideWorld = -1;
    boolean wasInWild;

    @Override
    public boolean evaluate() {
        if (suicideWorld < 0) {
            suicideWorld = Worlds.getRandomWorld(x -> x.isNormal() && x.isMembers() && x.getMinimumLevel() < 50)
                    .getWorld();
        }
        if (wasInWild && !Combat.isInWild()) {
            wasInWild = false;
        }
        if (!wasInWild && Combat.isInWild()) {
            // change suicide world
            suicideWorld = Worlds.getRandomWorld(x -> x.isNormal() && x.isMembers() && x.getMinimumLevel() < 50)
                    .getWorld();
            wasInWild = true;
        }

        if (SPIKE_AREA.contains(Players.getLocal())) {
            log("Get out of spikes");
            ObjectUtil.interact("Ladder");
            return true;
        }

        if (Inventory.contains(ItemID.JUG)) Inventory.dropAll(ItemID.JUG);

        if (Inventory.contains(ItemID.LOOTING_BAG_CLOSED)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            log("Open loot bag");
            Inventory.interact(ItemID.LOOTING_BAG_CLOSED);
            return true;
        }

        if (!AreaUtils.containsIgnorePlane(COURSE, Players.getLocal().getTile())) {
            if (mode == WildernessAgilityMode.BH_RAG_WORLD && Worlds.getCurrentWorld() == forceWorld) {
                // on rag world make sure we go to rag world on a random world so we dont get camped at lever
                log("Get off BH world");
                suicideWorld = Worlds.getRandomWorld(x -> x.isNormal() && x.isMembers() && x.getMinimumLevel() < 50)
                        .getWorld();
                WorldHopper.hopWorld(suicideWorld);
                Sleep.sleepUntil(() -> Worlds.getCurrentWorld() == suicideWorld && Client.isLoggedIn(), 12_000);
                return true;
            }

            if (!Combat.isInWild() && !Players.getLocal().isInCombat() && mode == WildernessAgilityMode.SUICIDE && Worlds.getCurrentWorld() != suicideWorld) {
                log("Hop to world " + suicideWorld);
                WorldHopper.hopWorld(suicideWorld);
                Sleep.sleepUntil(() -> Worlds.getCurrentWorld() == suicideWorld, 12_000);
                return true;
            }

            if (Combat.isAutoRetaliateOn()) {
                if (Bank.isOpen()) Bank.close();
                Logger.info("Turn off auto retaliate");
                Combat.toggleAutoRetaliate(false);
                return true;
            }

            int world = BoxingClient.getInstance().world;
            if (!Players.getLocal().isInCombat() && world > 0 && Worlds.getCurrentWorld() != world && mode == WildernessAgilityMode.BOXING) {
                log("Hop to team world");
                WorldHopper.hopWorld(world);
                return true;
            }
            if (Walking.shouldWalk(8)) Walking.walk(PIPE_AREA.getCenter());
            return true;
        }

        if (mode == WildernessAgilityMode.BH_RAG_WORLD && Worlds.getCurrentWorld() != forceWorld) {
            log("Hop back to BH world");
            if (!COURSE_HOP_TILE.equals(Players.getLocal().getTile())) {
                log("Get onto course hop tile");
                Walking.walkExact(COURSE_HOP_TILE);
                return true;
            }

            WorldHopper.hopWorld(forceWorld);
            return true;
        }
        return false;
    }
}
