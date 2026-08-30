package org.dreambot.behaviour.training.quests.animalmagnetism.util;


import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.util.Dialog;

public class SpecialWalker {
    public static final Area ECTOFUNTUS = new Area(3655, 3526, 3663, 3523, 1);
    public static final Area BOTTOM_FLOOR_ECTO = new Area(3655, 3526, 3663, 3523);
    public static final Area INSIDE_AVAS_ROOM = new Area(3091, 3363, 3096, 3354);
    public static final Area OUTSIDE_AVAS_ROOM = new Area(3097, 3363, 3104, 3354);
    public static final Area MORYTANIA_ENTRANCE = new Area(3442, 9886, 3437, 9900, 0);
    public static final Area SLIME_AREA = new Area(3663, 9906, 3694, 9872);
    public static final Area MORYTANIA = new Area(3420, 3595, 3775, 3171);
    // literally just exists to get in and out of avas room
    // it now also walks into morytania

    /**
     * @return true when in avas room
     */
    public static boolean enterAvasRoom() {
        if (INSIDE_AVAS_ROOM.contains(Players.getLocal())) return true;

        if (!OUTSIDE_AVAS_ROOM.contains(Players.getLocal())) {
         if (Walking.shouldWalk(8)) Walking.walk(OUTSIDE_AVAS_ROOM.getCenter());
            return false;
        }

        GameObject bookcase = GameObjects.closest("Bookcase");
        if (bookcase != null && bookcase.interact("Search")) {
            Sleep.sleepUntil(() -> INSIDE_AVAS_ROOM.contains(Players.getLocal()), 2400);
        }
        return false;
    }

    public static boolean leaveAvasRoom() {
        if (INSIDE_AVAS_ROOM.contains(Players.getLocal())) {
            GameObject lever = GameObjects.closest("Lever");
            if (lever != null && lever.interact("Pull")) {
                Sleep.sleepUntil(() -> !INSIDE_AVAS_ROOM.contains(Players.getLocal()), 2400);
            }
            return false;
        }
        return true;
    }

    public static boolean enterMorytania() {
        if (MORYTANIA.contains(Players.getLocal())) return true;
        if (Players.getLocal().getZ() != 0) return true;
        if (!MORYTANIA_ENTRANCE.contains(Players.getLocal())) {
         if (Walking.shouldWalk(8)) Walking.walk(MORYTANIA_ENTRANCE.getCenter());
            return false;
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("See you later");
            return false;
        }

        // varp for drezels warning about morytania
        if (PlayerSettings.getConfig(302) != 61) {
            NPC drezel = NPCs.closest("Drezel");
            if (drezel != null && drezel.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
            return false;
        }

        GameObject holyBarrier = GameObjects.closest("Holy barrier");
        if (holyBarrier != null && holyBarrier.interact("Pass-through")) {
            Sleep.sleepUntil(() -> MORYTANIA.contains(Players.getLocal()), 6000);
        }

        // todo interact with door
        return false;
    }

    public static boolean enterSlimeRoom() {
        if (!containsIgnorePlane(SLIME_AREA, Players.getLocal().getTile())) {
            if (!BOTTOM_FLOOR_ECTO.contains(Players.getLocal())) {
             if (Walking.shouldWalk(8)) Walking.walk(BOTTOM_FLOOR_ECTO.getCenter());
                return false;
            }
            GameObject trapdoor = GameObjects.closest("Trapdoor");
            if (trapdoor != null && trapdoor.interact()) {
                Sleep.sleepUntil(() -> containsIgnorePlane(SLIME_AREA, Players.getLocal().getTile()),
                        5000);
            }
            return false;
        }

        if (Players.getLocal().getZ() != 0) {
            GameObject stairs = GameObjects.closest(x -> x.getName().contains("Stairs")
                    && x.hasAction("Climb-down"));
            if (stairs != null && stairs.interact("Climb-down")) {
                int plane = Players.getLocal().getZ();
                Sleep.sleepUntil(() -> Players.getLocal().getZ() != plane, 7400);
            }
            return false;
        }
        return true;
    }

    public static boolean exitFuntus() {
        if (!containsIgnorePlane(SLIME_AREA, Players.getLocal().getTile())) return true;

        if (Players.getLocal().getZ() == 3) {
            GameObject ladder = GameObjects.closest("Ladder");
            if (ladder != null && ladder.distance() > 10) {
                if (Walking.shouldWalk()) Walking.walk(ladder.getTile());
                return false;
            }
            if (ladder != null && ladder.interact("Climb-up")) {
                Sleep.sleepUntil(() -> !containsIgnorePlane(SLIME_AREA, Players.getLocal().getTile()), 8000);
            }
            return false;
        }

        GameObject stairs = GameObjects.closest(x -> x.getName().equals("Stairs") && x.hasAction("Climb-up"));
        if (stairs != null && stairs.interact("Climb-up")) {
            int plane = Players.getLocal().getZ();
            Sleep.sleepUntil(() -> plane != Players.getLocal().getZ(), 8400);
        }
        return false;
    }

    public static boolean containsIgnorePlane(Area area, Tile tile) {
        area.setZ(tile.getZ());
        return area.contains(tile);
    }
}
