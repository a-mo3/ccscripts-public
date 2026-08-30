package org.dreambot.behaviour.method.moonsofperil.bluemoon;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Locatable;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * The phase where it freezes your weapon in ice
 */
public class BlueMoonIcePhase extends TickDecision {
    public static final int FROZEN_WEAPON = 13025;
    public static final int OUR_WEAPON_ANIMATION = 11031;
    public static final int ICE_ATTACK_GRAPHIC = 2770;

    public BlueMoonIcePhase() {
        setSimpleName("Blue moon ice");
    }

    @Override
    public boolean evaluate() {
        NPC ourFrozenWeapons = NPCs.closest(x -> x.getRealID() == FROZEN_WEAPON && x.getAnimation() == OUR_WEAPON_ANIMATION);
        NPC frozenWeapons = NPCs.closest(x -> x.getRealID() == FROZEN_WEAPON);
        if (ourFrozenWeapons == null && frozenWeapons == null) {
            return false;
        }

        // eat to full if we have our weapon back
        if (ourFrozenWeapons == null) {
            int healAmount = (int) (Math.min(Skill.COOKING.getLevel(), Skill.FISHING.getLevel()) * 0.3);
            int missingHealth = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
            if (healAmount <= missingHealth) {
                log("We have our weapon back in ice phase, safe to eat to full");
                Inventory.interact(ItemID.COOKED_BREAM);
            }
        }

        Map<Tile, GraphicsObject> iceAttacks = new HashMap<>();
        for (GraphicsObject g : GraphicsObjects.all(ICE_ATTACK_GRAPHIC)) {
            iceAttacks.put(g.getTile(), g);
        }


        if (!iceAttacks.isEmpty()) {
            Tile safe = Arrays.stream(Players.getLocal().getSurroundingArea(3).getTiles())
                    .filter(x -> !iceAttacks.containsKey(x))
                    .filter(Locatable::canReach)
                    .min(Comparator.comparingDouble(Players.getLocal()::distance))
                    .orElse(null);
            if (safe != null) {
                log("Move off ice attack " + safe + " " + safe.distance());
                Walking.walkExact(safe);
            } else {
                log("Failed to find a safe tile ");
            }
            return true;
        }

        Character target = Players.getLocal().getInteractingCharacter();
        if (ourFrozenWeapons != null && !ourFrozenWeapons.equals(target)) {
            log("Attack frozen weapons");
            ourFrozenWeapons.interact();
        }
        return true;
    }
}
