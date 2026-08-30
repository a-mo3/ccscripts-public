package org.dreambot.behaviour.method.moonsofperil.eclipsemoon;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

/**
 * normal no mechanic cycle of eclipse moon, stand on the shining spot and attack.
 */
public class EclipseMoonAttackPhase extends TickDecision {
    public EclipseMoonAttackPhase() {
        setSimpleName("Eclipse moon attack");
    }

    public static final int ATK_PHASE_SHINE_NPC_ID = 13015; // different id in different phases
    public static final int ECLIPSE_MOON_NPC_ID = 13012;

    @Override
    public boolean evaluate() {
        if (Inventory.contains(ItemID.ABYSSAL_WHIP) || Inventory.contains(ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD)) {
            log("Equip main weapons");
            Inventory.interact(ItemID.ABYSSAL_WHIP);
            Inventory.interact(ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD);
        }

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

        NPC eclipseMoon = NPCs.closest(x -> x.getRealID() == ECLIPSE_MOON_NPC_ID);
        Character targeting = Players.getLocal().getInteractingCharacter();
        if (eclipseMoon == null) {
            log("Failed to find moon.");
            return false;
        }

        if (targeting == null || !targeting.equals(eclipseMoon)) {
            log("Attack eclipse moon.");
            eclipseMoon.interact();
        }
        return true;
    }
}
