package org.dreambot.behaviour.method.moonsofperil.bloodmoon;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * this is the decision responsible for avoid rain in the phase where it wildy rains down
 * and there is no attacking the boss
 */
public class BloodMoonRainPhase extends TickDecision {
    public static final int BLOOD_RAIN_OBJ_ID = 51046;

    public BloodMoonRainPhase() {
        setSimpleName("Blood moon rain phase");
    }

    public static Tile safeTile = null;
    public static final int SHINE_NPC_ID = 13015;

    @Override
    public boolean evaluate() {
        if (GameObjects.all().stream().noneMatch(x -> x.getId() == BLOOD_RAIN_OBJ_ID)) {
            return false;
        }

        NPC jag = NPCs.closest(BloodMoonJaguarPhase.JAGUAR_NPC_ID);
        if (jag != null) {
            return false;
        }


        Prayers.toggleQuickPrayer(false);
        // pick a tile, when it has rain on it pick a new tile
        Map<Tile, GameObject> bloodTiles = new HashMap<>();
        for (GameObject obj : GameObjects.all(BLOOD_RAIN_OBJ_ID)) {
            bloodTiles.put(obj.getTile(), obj);
        }

        NPC shine = NPCs.closest(x -> x.getRealID() == SHINE_NPC_ID);
        if (shine != null && !shine.getArea().contains(safeTile)) {
            log("Find new safe tile - shine");
            safeTile = Arrays.stream(shine.getArea().getTiles())
                    .filter(x -> !bloodTiles.containsKey(x))
                    .min(Comparator.comparingDouble(Players.getLocal()::distance))
                    .orElse(null);
        }

        if (safeTile == null || bloodTiles.containsKey(safeTile)) {
            log("Find new safe tile");
            safeTile = Arrays.stream(Players.getLocal().getTile()
                            .getArea(3).getTiles())
                    .filter(x -> !bloodTiles.containsKey(x))
                    .min(Comparator.comparingDouble(Players.getLocal()::distance))
                    .orElse(null);
            if (safeTile != null) {
                log(String.format("New safe tile %s - Distance: %.2f", safeTile, safeTile.distance()));
            } else {
                log("Failed to find a safe tile");
                return false; // no action here, might as well try something else. may cause unexpected behaviours
            }
        }

        if (!Players.getLocal().getServerTile().equals(safeTile)) {
            Walking.walkExact(safeTile);
        }

        // eat the full during this phase
        int healAmount = (int) (Math.min(Skill.COOKING.getLevel(), Skill.FISHING.getLevel()) * 0.3);
        int missingHealth = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
        // you can walk and eat in the same tick so this should be fine
        if (healAmount <= missingHealth) {
            log("Rain phase safe to eat");
            Inventory.interact(ItemID.COOKED_BREAM);
        }
        return true;
    }
}
