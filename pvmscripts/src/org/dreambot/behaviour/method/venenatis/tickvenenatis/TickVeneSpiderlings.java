package org.dreambot.behaviour.method.venenatis.tickvenenatis;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.venenatis.VenenatisData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;

/**
 * use darts to clear spider lings, and get off web if its present
 */
public class TickVeneSpiderlings extends TickDecision {
    public TickVeneSpiderlings() {
        setSimpleName("Spiderlings");
    }

    List<Integer> fastWeapons = Arrays.asList(
            ItemID.RUNE_KNIFE,
            ItemID.ADAMANT_DART,
            ItemID.MITHRIL_DART,
            ItemID.STEEL_DART,
            ItemID.IRON_DART
    );

    public static final int SPIDERLING_ID = 12000; // this is an npc
    @Override
    public boolean evaluate() {
        List<NPC> spiderlings = NPCs.all(x -> x.getId() == SPIDERLING_ID && x.distance() < 6);
        if (spiderlings.isEmpty()) return false;
        log("Attack spiderlings");
        // todo get off web

        List<GameObject> webObjects = GameObjects.all(x -> VenenatisData.isWeb(x.getId()));
        if (webObjects.stream().anyMatch(x -> x.getTile().equals(Players.getLocal().getTile()))) {
            log("On web obj");
            if (Walking.shouldWalk()) {
                log("Walk");
                NPC venenatis = NPCs.closest(VenenatisData.VENENATIS_NAME);
                Walking.walkExact(
                        VenenatisWebTickAttack.getAttackable(venenatis,
                                webObjects.stream().map(GameObject::getTile).toArray(Tile[]::new),
                               null
                        ));
            }
            return true;
        }

        if (!Equipment.contains(x -> fastWeapons.contains(x.getId())) && Inventory.contains(x -> fastWeapons.contains(x.getId()))) {
            log("Switch to fast weapon");
            Inventory.interact(x -> fastWeapons.contains(x.getId()));
            return true;
        }

        Character target = Players.getLocal().getInteractingCharacter();
        if (target == null || !target.getName().equalsIgnoreCase("Spiderling") || target.getHealthPercent() == 0) {
            log("Not attacking spiderlings");
            NPC s = spiderlings.get(Calculations.random(spiderlings.size()));
            s.interact();
        }
        return true;
    }
}
