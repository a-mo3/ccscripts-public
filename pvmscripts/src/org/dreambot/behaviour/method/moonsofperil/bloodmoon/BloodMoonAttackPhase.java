package org.dreambot.behaviour.method.moonsofperil.bloodmoon;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;

/**
 * normal no mechanic cycle of blood moon, stand on the shining spot and attack.
 */
public class BloodMoonAttackPhase extends TickDecision {
    public BloodMoonAttackPhase() {
        setSimpleName("Blood moon attack");
    }

    public static final int ATK_PHASE_SHINE_NPC_ID = 13015; // different id in different phases
    public static final int BLOOD_MOON_NPC_ID = 13016;

    @Override
    public boolean evaluate() {
        // get onto shining tile, im not sure if all tiles will have the shine graphic on it or just the south east or w/e
        NPC shine = NPCs.closest(ATK_PHASE_SHINE_NPC_ID);
        if (shine != null && !shine.getArea().contains(Players.getLocal().getServerTile())) {
            Tile dest = Walking.getDestination();
            if (dest == null || !shine.getArea().contains(dest)) {
                log("Walk onto shining circle");
                Walking.walkExact(shine.getTile());
            } else {
                log("In process of getting onto shining");
            }
            return true;
        }

        // reset the safe tile
        BloodMoonRainPhase.safeTile = null;

        NPC bloodMoon = NPCs.closest(BLOOD_MOON_NPC_ID);
        Character targeting = Players.getLocal().getInteractingCharacter();
        if (bloodMoon == null) {
            log("Failed to find blood moon.");
            return false;
        }

        if (targeting == null || !targeting.equals(bloodMoon)) {
            log("Attack blood moon.");
            bloodMoon.interact();
        }

        return true;
    }
}
