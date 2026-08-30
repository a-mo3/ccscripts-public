package org.dreambot.behaviour.method.corp.behaviour;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.map.Region;
import org.dreambot.behaviour.training.slayer.SlayerMode;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.CombatMode;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * if not already, attack corp, or dark core
 * if spec is active only attack corp, dont want to waste spec on core
 */
public class AttackCorp extends TickDecision {
    List<Integer> spears = Arrays.asList(
            ItemID.ZAMORAKIAN_SPEAR,
            ItemID.RUNE_SPEAR,
            ItemID.DRAGON_SPEAR,
            ItemID.DRAGON_DEFENDER,
            ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD,
            ItemID.OSMUMTENS_FANG
    );

    final Area CORP_INSIDE = new Area(2974, 4397, 2998, 4370, 2);
    List<Integer> rangeRequip = Arrays.asList(
            ItemID.RUNE_CROSSBOW,
            ItemID.RUBY_BOLTS_E,
            ItemID.BLACK_DHIDE_SHIELD
    );

    @Override
    public boolean evaluate() {
        GameObject passage = GameObjects.closest("Passage");
        if (passage != null && passage.getX() > Players.getLocal().getX()) {
            log("Go into passage");
            passage.interact();
            Sleep.sleepUntil(() -> passage.getX() < Players.getLocal().getX(), 1200);
            return true;
        }

        NPC corp = NPCs.closest("Corporeal Beast");
        if (corp == null) {
            log("Corps not found");
            GroundItem loot = GroundItems.closest(x -> x.canReach() && x.getItem().getId() != ItemID.RUBY_BOLTS_E
                    && (x.getItem().getLivePrice() * (x.getItem().isStackable() ? x.getAmount() : 1)) > LivePrices.get(PVMUtil.getCheapest()));
            if (loot == null) {
                log("Going to center of the room");
                Tile center = Region.toInstance(CORP_INSIDE.getCenter()).get(0);
                if (center != null) Walking.walk(center);
                return true;
            }
            // todo it might be possible hes out of render, but i think npc loading distance is way more than render
            return false;
        }

        // tdodge 315 projectile
        Projectile shouldDodge = Projectiles.closest(x -> x.getId() == 315);
        if (shouldDodge != null && shouldDodge.getTargetTile().distance() < 2) {
            log("Need to dodge corp mage attack");
            Area underCorp = corp.getArea();
            Tile dodgeTile = Arrays.stream(shouldDodge.getTile().getArea(5)
                            .getTiles())
                    .filter(x -> shouldDodge.distance(x) > 2)
                    // make sure it not under corp
                    .filter(x -> !underCorp.contains(x))
                    .findAny()
                    .orElse(null);
            if (dodgeTile != null) {
                log("Get onto dodge tile " + dodgeTile);
                Walking.walkExact(dodgeTile);
                return true;
            }

        }

        Character target = Players.getLocal().getInteractingCharacter();
        NPC darkCore = NPCs.closest("Dark energy core");
        if (darkCore != null && !Combat.isSpecialActive()) {
            log("Attack dark core");
            if (!darkCore.equals(target)) darkCore.interact();
            return true;
        }


        Area meleeDistance = corp.getSurroundingArea(3);
        if (meleeDistance.contains(Players.getLocal()) && Equipment.slotContains(EquipmentSlot.WEAPON, x -> rangeRequip.contains(x.getId()))) {
            log("Get out of melee distance");
            Tile dodge = Arrays.stream(Players.getLocal().getSurroundingArea(4).getTiles())
                    .filter(x -> !meleeDistance.contains(x))
                    .findFirst()
                    .orElse(null);
            if (dodge != null) {
                Walking.walkExact(dodge);
                return true;
            }
        }

        if (!Combat.isSpecialActive()
                && Inventory.contains(x -> spears.contains(x.getId()))
                && !Equipment.contains(x -> spears.contains(x.getId()))) {
            log("Re-equip spear");
            Inventory.interact(x -> spears.contains(x.getId()));
        }

        if (!Combat.isSpecialActive()
                && Inventory.contains(x -> spears.contains(x.getId()))
                && !Equipment.contains(x -> spears.contains(x.getId()))) {
            log("Re-equip spear");
            Inventory.interact(x -> spears.contains(x.getId()));
        }

        if (!Combat.isSpecialActive()
                && Inventory.contains(x -> rangeRequip.contains(x.getId()))) {
            log("Re-equip spear");
            Inventory.interact(x -> rangeRequip.contains(x.getId()));
        }

        if (Equipment.contains(x -> spears.contains(x.getId()))) {
            if (Combat.getCombatModeIndex() != 0) {
                log("Enforce stab style");
                Combat.setCombatModeIndex(0);
            }
        }

        if (Equipment.slotContains(EquipmentSlot.WEAPON, x -> rangeRequip.contains(x.getId()))) {
            if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
                log("Enforce ranged rapid");
                Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
            }
        }


        if (!corp.equals(target)) {
            log("No target or not corp, attack corp");
            corp.interact();
        }
        return true;
    }
}
