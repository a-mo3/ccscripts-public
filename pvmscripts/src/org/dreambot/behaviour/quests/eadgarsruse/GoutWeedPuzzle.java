package org.dreambot.behaviour.quests.eadgarsruse;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class GoutWeedPuzzle extends Fractal {
    Tile mazeStart = new Tile(2862, 10092, 0);

    public GoutWeedPuzzle() {
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Storeroom door", "Open"));
    }

    // this is the guard we are waiting to get on goTile
    int guardID = 4150;

    Tile goTile = new Tile(2858, 10086, 0);

    @Override
    public int onLoop() {
        // todo hp consideration

        // todo maybe pray from missles

        if (!mazeStart.equals(Players.getLocal().getTile())) {
            if (Walking.shouldWalk()) Walking.walkExact(mazeStart);
            return ReactionGenerator.getNormal();
        }

        GameObject door = GameObjects.closest(x -> x.distance() < 5 && x.hasAction("Open"));
        if (door != null) {
            log("Opening door");
            door.interact("Open");
            return ReactionGenerator.getNormal();
        }

        if (Walking.getRunEnergy() < 15) {
            log("Waiting for run to regen.");
            return ReactionGenerator.getNormal();
        }

        if (!Walking.isRunEnabled()) {
            log("toggle run.");
            Walking.toggleRun();
            return ReactionGenerator.getNormal();
        }

        if (Prayers.isActive(Prayer.PROTECT_FROM_MISSILES))
            Prayers.toggle(false, Prayer.PROTECT_FROM_MISSILES);
        // once guard gets on that tile you can just interact with gout weed and theres a good enough chance you run through the whole maze
        NPC goGuard = NPCs.closest(guardID);
        if (goGuard != null && goTile.equals(goGuard.getTile())) {
            GameObject goutBox = GameObjects.closest("Goutweed Crate");
            log("Go!");
            // prot missles incase it doesn't work.
            if (Skills.getRealLevel(Skill.PRAYER) >= 40 && Skills.getBoostedLevel(Skill.PRAYER) > 1)
                Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
            if (goutBox != null) goutBox.interact("Search");
            Sleep.sleep(8_000);
        }

        return ReactionGenerator.getQuick();
    }
}
