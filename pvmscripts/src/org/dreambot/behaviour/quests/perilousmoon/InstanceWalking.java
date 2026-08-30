package org.dreambot.behaviour.quests.perilousmoon;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.path.impl.GlobalPath;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.map.Region;

import java.util.List;

public class InstanceWalking {
    public static void walk(Tile tile) {
        if (!Client.isDynamicRegion()) {
            Logger.info("Instance walker revert to normal");
            if (Walking.shouldWalk()) Walking.walk(tile);
            return;
        }

        GlobalPath<AbstractWebNode> p = WebFinder.getWebFinder().calculate(
                Region.fromInstance(Players.getLocal().getTile()),
                tile
        );
        Logger.info(Region.fromInstance(Players.getLocal().getTile()));
        Logger.info(p);

        if (p == null || p.size() < 2) {
            Logger.info("instance walking p = " + p.size());
            return;
        }

        AbstractWebNode next = p.get(1); // todo size check

        if (next == null) {
            Logger.info("next node was null instance walker");
            return;
        }

        if (next instanceof EntranceWebNode) {
            Logger.info("Handle instance walking entrance " + next.getTile());
            GameObject entrance = GameObjects.closest(x -> ((EntranceWebNode) next).getEntityName().equals(x.getName())
                    && (!Client.isDynamicRegion() || Region.fromInstance(x.getTile()).distance(next.getTile()) < 10));
            if (entrance != null) {
                Logger.info("Entrance on " + entrance.getTile() + Region.fromInstance(entrance.getTile()));
                Logger.info(String.format("interacting with entrance %s %s", entrance.getName(), ((EntranceWebNode) next).getAction()));
                entrance.interact(((EntranceWebNode) next).getAction());
                Sleep.sleep(2400);
            }
            return;
        }

        Logger.info("Walking to next tile");
        List<Tile> translated = Region.toInstance(next.getTile());
        if (!translated.isEmpty()) Walking.walk(translated.get(0));
    }

    public static boolean walkExact(Tile t) {
        List<Tile> ts = Region.toInstance(t);
        if (ts.isEmpty()) Logger.info("Failed to convert tiles to instance");
        Tile instanced = ts.get(0);
        if (instanced == null) Logger.info("Failed to convert tiles to instance");
        return Walking.walkExact(instanced);
    }
}
