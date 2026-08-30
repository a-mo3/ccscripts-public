package org.dreambot.behaviour.method.moonsofperil.bluemoon;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.method.moonsofperil.GrabFrozenItem;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;

public class GoToBlueMoon extends TickDecision {
    public static final Area BLUE_MOON_STATUE = new Area(1434, 9660, 1446, 9653);
    public static final Area BLUE_MOON_AREA = new Area(1418, 9697, 1461, 9665);

    public GoToBlueMoon() {
        setSimpleName("Go to blue moon");
    }

    @Override
    public boolean evaluate() {
        if (BLUE_MOON_AREA.contains(Players.getLocal())) {
            return false;
        }

        if (Combat.isAutoRetaliateOn()) {
            log("Disable auto retaliate");
            Combat.toggleAutoRetaliate(false);
            // shouldn't need to wait a tick between this and anything else
        }

        Prayers.toggleQuickPrayer(false);

        if ((Inventory.contains(ItemID.ABYSSAL_WHIP) && !Equipment.contains(ItemID.ABYSSAL_WHIP))
                || (!Inventory.contains(ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD) && !Equipment.contains(ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD))) {
            log("Equip whip and shield");
            Equipment.equip(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP);
            Equipment.equip(EquipmentSlot.SHIELD, ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD);
            return true;
        }

        if (Combat.getCombatStyle() != CombatStyle.SHARED) {
            log("Set shared style");
            Combat.setCombatStyle(CombatStyle.SHARED);
        }

        int healAmount = (int) (Math.min(Skill.COOKING.getLevel(), Skill.FISHING.getLevel()) * 0.3);
        int missingHealth = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
        // you can walk and eat in the same tick so this should be fine
        if (healAmount <= missingHealth && Inventory.contains(ItemID.COOKED_BREAM)) {
            log("safe to eat");
            Inventory.interact(ItemID.COOKED_BREAM);
        }

        if (Dialogues.inDialogue()) {
            if (BLUE_MOON_STATUE.contains(Players.getLocal())) {
                log("Cant enter, get frozen items");
                GrabFrozenItem.setNeedsToRestock(true);
                return true;
            }
            log("Solve warning dialogue");
            Dialog.solve("don't ask me again", "Goodbye");
            return true;
        }

        if (BLUE_MOON_STATUE.contains(Players.getLocal())) {
            log("Enter blue moon arena");
            GameObject statue = GameObjects.closest("Statue");
            if (statue != null) {
                statue.interact();
            } else {
                log("Failed to find statue");
            }
            return true;
        }

        log("Go to blood moon");
        if (Walking.shouldWalk()) Walking.walk(BLUE_MOON_STATUE);
        return true;
    }
}
