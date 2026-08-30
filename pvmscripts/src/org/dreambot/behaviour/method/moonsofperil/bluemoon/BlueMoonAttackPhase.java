package org.dreambot.behaviour.method.moonsofperil.bluemoon;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;

/**
 * normal no mechanic cycle of blue moon, stand on the shining spot and attack.
 */
public class BlueMoonAttackPhase extends TickDecision {
    public BlueMoonAttackPhase() {
        setSimpleName("Blue moon attack");
    }

    public static final int ATK_PHASE_SHINE_NPC_ID = 13015; // different id in different phases
    public static final int BLUE_MOON_NPC_ID = 13017;

    @Override
    public boolean evaluate() {
        Item blueSwitch = Inventory.get(ItemID.GLACIAL_TEMOTLI, ItemID.DUAL_MACUAHUITL);
        if (blueSwitch != null) {
            if (blueSwitch.getId() == ItemID.DUAL_MACUAHUITL) {
                if (Skills.getRealLevel(Skill.STRENGTH) >= 75 && Skills.getRealLevel(Skill.ATTACK) >= 70) {
                    if (Inventory.isFull()) {
                        log("Drop net");
                        Inventory.drop(ItemID.BIG_FISHING_NET);
                    }
                    log("dual wield switch");
                    blueSwitch.interact();
                }

            } else {
                if (Inventory.isFull()) {
                    log("Drop net");
                    Inventory.drop(ItemID.BIG_FISHING_NET);
                }
                log("dual wield switch");
                blueSwitch.interact();
            }
            // you can keep going after this.
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

        NPC blueMoon = NPCs.closest(BLUE_MOON_NPC_ID);
        Character targeting = Players.getLocal().getInteractingCharacter();
        if (blueMoon == null) {
            log("Failed to find blue moon.");
            return false;
        }

        if (targeting == null || !targeting.equals(blueMoon)) {
            log("Attack blue moon.");
            blueMoon.interact();
        }

        return true;
    }
}
