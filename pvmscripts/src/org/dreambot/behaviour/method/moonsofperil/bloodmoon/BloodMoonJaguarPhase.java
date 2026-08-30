package org.dreambot.behaviour.method.moonsofperil.bloodmoon;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;

/**
 * Jaguar phase has jaguars surround the boss, if they hit you the boss is healed
 * you have to
 * 1. stand on the highlighted circle
 * 2. blood rain spawns on the tiles next to the boss
 * 1-2 (i think 2) ticks after they spawn, you walk onto them to avoid the jaguars attack
 * too early you take damage from the rain, too late and the jaguar hits you.
 */
public class BloodMoonJaguarPhase extends TickDecision implements SpawnListener {
    public BloodMoonJaguarPhase() {
        setSimpleName("Jaguars");
        Client.getInstance().addEventListener(this);
        GameObjects.setIncludeNullNames(true);
    }

    // the shine on the circle you are meant to stand on
    public static final int SHINE_NPC_ID = 13015;
    public static final int JAGUAR_NPC_ID = 13021; // all jaguars have the same ID
    public static final int BLOOD_POOL_OBJECT_ID = 51046;
    // when the blood pools damage their animation ID changes from 10966 -> 10968, it may be too late to use that to move
    int lastPool = -1;

    @Override
    public boolean evaluate() {
        NPC shine = NPCs.closest(x -> x.getRealID() == SHINE_NPC_ID);
        if (shine == null) return false;
        // check closest to shine not player because the shine can spawn opposite side of the player
        NPC jag = NPCs.closest(x -> x.getId() == JAGUAR_NPC_ID, shine.getTile());
        if (jag == null) {
            // no jaguars
            return false;
        }

        // get onto shining tile, im not sure if all tiles will have the shine graphic on it or just the south east or w/e
        if (!shine.getArea().contains(Players.getLocal().getServerTile())) {
            Tile dest = Walking.getDestination();
            if (dest == null || !shine.getArea().contains(dest)) {
                log("Walk onto shining circle");
                Walking.walkExact(shine.getTile());
            } else {
                log("In process of getting onto shining");
            }
            return true;
        }


        int currentTick = Client.getGameTick();
        if (lastPool + 3 <= currentTick && lastPool + 4 > currentTick) {
            log(lastPool + " Last pool");
            GameObject pool = GameObjects.closest(BLOOD_POOL_OBJECT_ID);
            if (pool != null && !pool.getTile().equals(Players.getLocal().getServerTile())) {
                log("Move onto blood");
                Walking.walkExact(pool.getTile());
            }
            return true;
        }
        // re aggro the jaguar
        Character target = Players.getLocal().getInteractingCharacter();
        if (target != null && target.equals(jag)) {
            log("Already has target " + target.getName());
            return true;
        } else {
            jag.interact();
        }

        return true;
    }

    @Override
    public void onGameObjectSpawn(GameObject object) {
        // any area check out be more expensive than checking IDs
        if (object.getId() == BLOOD_POOL_OBJECT_ID) {
            lastPool = Client.getGameTick();
        }
    }
}
