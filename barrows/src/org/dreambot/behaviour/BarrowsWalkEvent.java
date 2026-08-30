package org.dreambot.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.muling.Log;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarrowsWalkEvent extends AbstractResponseEvent<BarrowsWalkEvent.Response> {
    static int failCount = 0;

    class BarrowsObstacle {
        final GameObject door;
        final int index;

        BarrowsObstacle(GameObject door, int index) {
            this.door = door;
            this.index = index;
        }
    }

    final Timer timeout = new Timer(8 * 1000);
    final List<Tile> path;
    List<BarrowsObstacle> doors = new ArrayList<>();

    public BarrowsWalkEvent(List<Tile> path) {
        this.path = path;
    }

    enum Response {
        TIMEOUT,
        FINISHED_PATH,
        NO_PATH
    }

    @Override
    public int onLoop() {
        if (ScriptSettings.getSettingsData().prayInTunnel && !Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
            Item pot = ItemVariants.PRAYER_POTION.getItem();
            if (Skills.getBoostedLevel(Skill.PRAYER) < 3 && pot != null) {
                Inventory.interact(pot, "Drink");
                Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) > 3, 1000);
            }

            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        }

        if (path.isEmpty()) {
            setResponse(Response.NO_PATH);
            Walking.walkOnScreen(Players.getLocal().getSurroundingArea(4).getRandomTile());
            failCount++;

            if (failCount > 8) {
                Client.setIdleTime(30_000);
                failCount = 0;
            }
            return ReactionGenerator.getQuick();
        }

        WidgetChild solution = getSolution();
        if (solution != null) {
            Logger.info("Found solve");
            solution.interact();
            return ReactionGenerator.getQuick();
        }

        if (timeout.finished()) {
            setResponse(Response.TIMEOUT);
            return ReactionGenerator.getQuick();
        }

        Item stamina = ItemVariants.STAMINA_POTION.getItem();
        if (!Walking.isStaminaActive() && Walking.getRunEnergy() < 20 && stamina != null) {
            Inventory.interact(stamina);
            return ReactionGenerator.getQuick();
        }

        if (Walking.getRunEnergy() > 20 && !Walking.isRunEnabled()) {
            Walking.toggleRun();
            return ReactionGenerator.getQuick();
        }

        if (!Walking.shouldWalk()) {
            return ReactionGenerator.getQuick();
        }

        doors.clear();
        Tile positionInPath = null;
        int indexInPath = 0;
        for (int i = 0; i < path.size(); i++) {
            Tile t = path.get(i);
            GameObject[] arr = GameObjects.getObjectsOnTile(t);
            if (arr == null) {
                setResponse(Response.NO_PATH);
                Walking.walkOnScreen(Players.getLocal().getSurroundingArea(4).getRandomTile());
                failCount++;

                if (failCount > 8) {
                    Client.setIdleTime(30_000);
                    failCount = 0;
                }

                return ReactionGenerator.getQuick();
            }
            GameObject door = Arrays.stream(arr)
                    .filter(x -> x.hasAction("Open"))
                    .findFirst()
                    .orElse(null);
            if (door != null) {
                doors.add(new BarrowsObstacle(door, i));
            }

            if (positionInPath == null) {
                positionInPath = t;
                indexInPath = i;
                continue;
            }

            if (t.distance() < positionInPath.distance()) {
                positionInPath = t;
                indexInPath = i;
            }
        }

        final int finalIndexInPath = indexInPath;
        BarrowsObstacle nextDoor = doors.stream().filter(x -> x.index > finalIndexInPath).findFirst().orElse(null);
        if (nextDoor != null) {
            Logger.info("Interacting with door barrows walker");
//            nextDoor.door.interact("Open");
            GameObject door = GameObjects.closest(x -> x.getTile().equals(nextDoor.door.getTile()));
            if (door != null && door.distance() < 8) {
                Logger.info(doors.size() + " " + Arrays.toString(door.getActions()));
                // nextDoor door cant be interacted with
                door.interact("Open");
                return ReactionGenerator.getNormal();
            }
        }

        Tile end = path.get(path.size() - 1);
        if (end.distance() < 3) {
            setResponse(Response.FINISHED_PATH);
            return ReactionGenerator.getNormal();
        }

        int trgtIndex = indexInPath + 6;
        Log.info(trgtIndex + "trgt index");
        if (trgtIndex >= path.size()) {
            Logger.info("Walking to end " + path.size());
            Walking.walkOnScreen(path.get(path.size() - 1));
        } else {
            Logger.info(String.format("Walking to index %d tile %s", trgtIndex, path.get(trgtIndex)));
            Walking.walkOnScreen(path.get(trgtIndex));
        }

        return ReactionGenerator.getQuick();
    }


    private WidgetChild getSolution() {
        WidgetChild first = Widgets.get(25, 3);
        if (first == null) {
            return null;
        }

        int target = first.getDisabledMediaType() - 3;
        return Widgets.get(x -> x.getDisabledMediaType() == target);
    }
}
