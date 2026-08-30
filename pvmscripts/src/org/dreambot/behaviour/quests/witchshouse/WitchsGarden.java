package org.dreambot.behaviour.quests.witchshouse;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * navigate the witchs garden while staying out of her sight
 */
public class WitchsGarden extends Fractal {
    Tile fountainStart = new Tile(2901, 3460);
    Map<Tile, Tile> tilesToFountain = new HashMap<>();
    Map<Tile, Tile> tilesToShed = new HashMap<>();

    Area INSIDE_SHED = new Area(2934, 3467, 2937, 3459);

    public WitchsGarden(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        tilesToFountain.put(fountainStart, new Tile(2908, 3460));
        tilesToFountain.put(new Tile(2908, 3460), new Tile(2916, 3460));
        tilesToFountain.put(new Tile(2916, 3460), new Tile(2924, 3460));
        tilesToFountain.put(new Tile(2924, 3460), new Tile(2932, 3460));
        tilesToFountain.put(new Tile(2932, 3460), new Tile(2933, 3466));
        tilesToFountain.put(new Tile(2933, 3466), new Tile(2927, 3466));
        tilesToFountain.put(new Tile(2927, 3466), new Tile(2920, 3466));
        tilesToFountain.put(new Tile(2920, 3466), new Tile(2912, 3466));
        tilesToFountain.put(new Tile(2912, 3466), new Tile(2912, 3470));

        tilesToShed.put(new Tile(2913, 3466), new Tile(2920, 3466));
        tilesToShed.put(new Tile(2920, 3466), new Tile(2927, 3466));
        tilesToShed.put(new Tile(2927, 3466), new Tile(2933, 3464));
        tilesToShed.put(new Tile(2933, 3464), null);

    }

    @Override
    public int onLoop() {
        NPC witch = NPCs.closest("Nora T. Hagg");
        boolean witchFacingEast = witch == null || witch.getOrientation() > 1000;

        if (!Inventory.contains(ItemID.KEY_2411)) {
            GameObject fountain = GameObjects.closest("Fountain");
            if (fountain != null && fountain.hasAction("Check") && fountain.distance() < 3) {
                if (Dialogues.inDialogue()) {
                    Dialog.solve("");
                }
                log("Check fountain");
                fountain.interact("Check");
                return ReactionGenerator.getNormal();
            }

            // walk to fountain
            Tile current = tilesToFountain.keySet()
                    .stream()
                    .min(Comparator.comparingInt(x -> LocalPathFinder.getLocalPathFinder().calculate(Players.getLocal().getTile(), x).size()))
                    .orElse(null);
            if (current == null) {
                log("Failed to find current step");
                return ReactionGenerator.getNormal();
            }

            Player lp = Players.getLocal();
            if (!lp.isMoving() && !current.equals(lp.getTile())) {
                log("Walking onto current " + current);
                Walking.walkExact(current);
                Sleep.sleepUntil(() -> current.equals(Players.getLocal().getTile()), 2400);
                return ReactionGenerator.getNormal();
            }

            Tile nextStep = tilesToFountain.get(current);
            if (nextStep == null) {
                log("Cant find next step for " + current);
                return ReactionGenerator.getNormal();
            }

            if (witch == null) {
                Walking.walk(nextStep);
                Sleep.sleepUntil(() -> nextStep.equals(Players.getLocal().getTile()), 4400);
                return ReactionGenerator.getNormal();
            }

            // check if witch is facing the other way
            boolean movingEast = current.getX() < nextStep.getX();
            if (movingEast) {
                if (witchFacingEast) {
                    log("Waiting");
                } else if (witch.getX() <= lp.getX()) {
                    log("Run while shes not looking");
                    Walking.walk(nextStep);
                    Sleep.sleepUntil(() -> nextStep.equals(Players.getLocal().getTile()), 4400);
                    return ReactionGenerator.getNormal();
                }
            } else {
                if (!witchFacingEast) {
                    log("Waiting");
                } else if (witch.getX() >= lp.getX()) {
                    log("Run while shes not looking");
                    Walking.walk(nextStep);
                    Sleep.sleepUntil(() -> nextStep.equals(Players.getLocal().getTile()), 4400);
                    return ReactionGenerator.getNormal();
                }
            }
            return ReactionGenerator.getNormal();
        }


        // walk toshed
        Tile current = tilesToShed.keySet()
                .stream()
                .min(Comparator.comparingInt(x -> LocalPathFinder.getLocalPathFinder().calculate(Players.getLocal().getTile(), x).size()))
                .orElse(null);
        if (current == null) {
            log("Failed to find current step");
            return ReactionGenerator.getNormal();
        }

        Player lp = Players.getLocal();
        if (!lp.isMoving() && !current.equals(lp.getTile())) {
            log("Walking onto current " + current);
            Walking.walkExact(current);
            Sleep.sleepUntil(() -> current.equals(Players.getLocal().getTile()), 2400);
            return ReactionGenerator.getNormal();
        }

        Tile nextStep = tilesToShed.get(current);
        if (nextStep == null) {
            // end of path, enter shed
            Item key = Inventory.get(ItemID.KEY_2411);
            GameObject door = GameObjects.closest("Door");
            if (key == null || door == null) {
                log("Cant find key or door");
                return ReactionGenerator.getNormal();
            }
            key.useOn(door);
            Sleep.sleepUntil(() -> INSIDE_SHED.contains(Players.getLocal()), 4000);
            return ReactionGenerator.getNormal();
        }

        if (witch == null) {
            Walking.walk(nextStep);
            Sleep.sleepUntil(() -> nextStep.equals(Players.getLocal().getTile()), 4400);
            return ReactionGenerator.getNormal();
        }

        // check if witch is facing the other way
        boolean movingEast = current.getX() < nextStep.getX();
        if (movingEast) {
            if (witchFacingEast) {
                log("Waiting");
            } else if (witch.getX() <= lp.getX()) {
                log("Run while shes not looking");
                Walking.walk(nextStep);
                Sleep.sleepUntil(() -> nextStep.equals(Players.getLocal().getTile()), 4400);
                return ReactionGenerator.getNormal();
            }
        } else {
            if (!witchFacingEast) {
                log("Waiting");
            } else if (witch.getX() >= lp.getX()) {
                log("Run while shes not looking");
                Walking.walk(nextStep);
                Sleep.sleepUntil(() -> nextStep.equals(Players.getLocal().getTile()), 4400);
                return ReactionGenerator.getNormal();
            }
        }
        return ReactionGenerator.getNormal();
    }
}
