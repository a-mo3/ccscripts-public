package org.dreambot.behaviour.method.mta.telekinetic;

import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.GameStateListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.util.Direction;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.method.mta.MTANodes;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TelekineticMazeMTA extends Fractal implements GameStateListener, ChatListener {
    public TelekineticMazeMTA(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        MTANodes.init();
        setSimpleName("Telekinetic maze");
        NPCs.setIncludeNullNames(true);
        Client.getInstance().addEventListener(this);
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
        ;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.LAW_RUNE, 1, 1600);

        this.paintArraySupplier = () -> new String[]{
                "In maze " + currentMaze,
                "Steps remaining " + (currentSolve == null ? "-" : currentSolve.size()),
                "Direction " + currentDirection
        };
    }

    Area ENTER_AREA = new Area(3359, 3321, 3366, 3315);

    TelekenticSolutions currentMaze;
    Stack<Direction> currentSolve;
    Direction currentDirection;

    boolean spottedMovingMazeGuard = false;

    @Override
    public int onLoop() {
        if (!Client.isDynamicRegion()) {
            // todo enter a maze
            if (!ENTER_AREA.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(ENTER_AREA);
                return ReactionGenerator.getNormal();
            }

            log("Go into maze");
            GameObject enter = GameObjects.closest("Telekinetic Teleport");
            if (enter != null) {
                log("enter");
                enter.interact("Enter");
            } else {
                log("Failed to find exit teleport");
            }
            return ReactionGenerator.getNormal();
        }

        if (currentMaze == null) {
            log("Find current maze");
            currentMaze = TelekenticSolutions.findCurrentMaze();
            if (currentMaze == null) {
                Logger.warn("Failed to find the maze we're in");
            } else {
                currentSolve = (Stack<Direction>) currentMaze.solve.clone();
            }
            return ReactionGenerator.getNormal();
        }

        NPC nullG = NPCs.closest(MAZE_GUARDIAN_MOVING);
        if ((currentSolve == null || currentSolve.isEmpty()) && currentDirection == null) {
            log("Leave maze.");
            if (nullG != null) {
                log("Wait for mans to finish");
                return ReactionGenerator.getNormal();
            }

            GameObject exit = GameObjects.closest("Exit Teleport");
            if (exit != null) {
                log("Leave w/ teleport");
                if (!Menu.isMenuManipulationActive()) {
                    if (exit.distance() > 5) {
                        log("Walk closer no menu manip");
                        if (Walking.shouldWalk()) Walking.walk(exit);
                        return ReactionGenerator.getNormal();
                    }
                }

                exit.interact("Enter");
            } else {
                log("Failed to find exit teleport");
            }
            return ReactionGenerator.getNormal();
        }


        if (currentDirection == null) currentDirection = currentSolve.pop();

        if (nullG != null) {
            // when moving guard spotted and hasnt yet despawned set the dirrection to null to naturally cycle through
            if (!spottedMovingMazeGuard) {
                log("Null g out and about " + nullG.getAnimation());
                spottedMovingMazeGuard = true;
                currentDirection = null;
            }
        } else {
            spottedMovingMazeGuard = false;
        }

        NPC guardian = NPCs.closest(MAZE_GUARDIAN_MOVING - 1);

        log("Cast on side: " + currentDirection);
        if (currentDirection == null) return ReactionGenerator.getNormal();
        switch (currentDirection) {
            case NORTH:
                return doCast(guardian, getNorthSpots());
            case SOUTH:
                return doCast(guardian, getSouthSpots());
            case EAST:
                return doCast(guardian, getEastSpots());
            case WEST:
                return doCast(guardian, getWestSpots());
        }


        return ReactionGenerator.getNormal();
    }

    private int doCast(NPC guardian, Tile[] castSpots) {
        Tile closer = Arrays.stream(castSpots).min(Comparator.comparingDouble(Tile::distance)).orElse(null);
        if (closer == null) {
            log("Terrible closer null");
            return ReactionGenerator.getNormal();
        }
        if (!closer.equals(Players.getLocal().getTile())) {
            slowLog("Get onto cast spot");
            if (Walking.shouldWalk()) Walking.walkExact(closer);
            return ReactionGenerator.getNormal();
        }

        if (guardian != null) {
            log("Cast on jaunt");
            Magic.castSpellOn(Normal.TELEKINETIC_GRAB, guardian);
            return ReactionGenerator.getNormal() * 2;
        } else {
            log("Waiting for guardian to stop moving");
            return ReactionGenerator.getNormal();
        }
    }

    private static final int MAZE_GUARDIAN_MOVING = 6778;

    @Override
    public void onMessage(Message message) {
        if (!isValid() || message.getType() == MessageType.PLAYER) {
            return;
        }

        if (message.getMessage().contains("teleport out of the training arena!")) {
            log("Logout for instance teleport failsafe");
            Client.setIdleTime(30_000);
            return;
        }

        if (message.getMessage().contains("Congratulations! You have received")) {
            currentDirection = null;
            currentSolve = null;
        }
    }

    private void resetState() {
        currentMaze = null;
        currentSolve = null;
        spottedMovingMazeGuard = false;
        currentDirection = null;
    }

    @Override
    public void onGameStateChange(GameState gameState) {
        if (gameState == GameState.LOADING || gameState == GameState.GAME_LOADING) {
            resetState();
        }
    }

    public static Tile[] findCorners(List<GameObject> walls) {
        if (walls == null || walls.isEmpty()) {
            Logger.info("The set of tiles cannot be null or empty.");
            return null;
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        // Find the min and max x and y coordinates
        for (Tile t : walls.stream().map(GameObject::getTile).collect(Collectors.toList())) {
            if (t.getX() < minX) minX = t.getX();
            if (t.getY() < minY) minY = t.getY();
            if (t.getX() > maxX) maxX = t.getX();
            if (t.getY() > maxY) maxY = t.getY();
        }

        // Create the corner points
        Tile northEast = new Tile(maxX, maxY);
        Tile northWest = new Tile(minX, maxY);
        Tile bottomRight = new Tile(maxX, minY);
        Tile bottomLeft = new Tile(minX, minY);

        // Return the corners in the specified order
        return new Tile[]{
                northEast,
                northWest,
                bottomRight,
                bottomLeft};
    }

    // gets the two north corner spots one could be on
    // todo i should cache the corner results but i think if i do that ill calculate it on a half loaded maze and brick the script for 30 minutes
    public static Tile[] getNorthSpots() {
        Tile[] corners = findCorners(GameObjects.all(10755));
        return new Tile[]{corners[0].translate(-1, 0), corners[1].translate(1, 0)};
    }

    public static Tile[] getSouthSpots() {
        Tile[] corners = findCorners(GameObjects.all(10755));
        return new Tile[]{corners[2].translate(-1, 0), corners[3].translate(1, 0)};
    }

    public static Tile[] getEastSpots() {
        Tile[] corners = findCorners(GameObjects.all(10755));
        return new Tile[]{corners[0].translate(0, -1), corners[2].translate(0, 1)};
    }

    public static Tile[] getWestSpots() {
        Tile[] corners = findCorners(GameObjects.all(10755));
        return new Tile[]{corners[1].translate(0, -1), corners[3].translate(0, 1)};
    }
}

