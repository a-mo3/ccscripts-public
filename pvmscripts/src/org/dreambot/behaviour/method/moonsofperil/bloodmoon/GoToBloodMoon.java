package org.dreambot.behaviour.method.moonsofperil.bloodmoon;

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
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class GoToBloodMoon extends TickDecision {
    public static final Area BLOOD_MOON_ARENA = new Area(1371, 9650, 1407, 9612);
    public static final Area BLOOD_MOON_STATUE = new Area(1410, 9634, 1414, 9630);

    public GoToBloodMoon() {
        setSimpleName("Go to blood moon");
    }

    @Override
    public boolean evaluate() {
        if (BLOOD_MOON_ARENA.contains(Players.getLocal())) {
            return false;
        }

        if (Combat.isAutoRetaliateOn()) {
            log("Disable auto retaliate");
            Combat.toggleAutoRetaliate(false);
            // shouldn't need to wait a tick between this and anything else
        }

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

        Prayers.toggleQuickPrayer(false);

        int healAmount = (int) (Math.min(Skill.COOKING.getLevel(), Skill.FISHING.getLevel()) * 0.3);
        int missingHealth = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
        // you can walk and eat in the same tick so this should be fine
        if (healAmount <= missingHealth && Inventory.contains(ItemID.COOKED_BREAM)) {
            log("safe to eat");
            Inventory.interact(ItemID.COOKED_BREAM);
        }

        if (Dialogues.inDialogue()) {
            log("Solve warning dialogue");
            Dialog.solve("don't ask me again");
            return true;
        }

        if (BLOOD_MOON_STATUE.contains(Players.getLocal())) {
            log("Enter blood moon arena");
            GameObject statue = GameObjects.closest("Statue");
            if (statue != null) {
                statue.interact();
            } else {
                log("Failed to find statue");
            }
            return true;
        }

        log("Go to blood moon");
        if (Walking.shouldWalk()) Walking.walk(BLOOD_MOON_STATUE);
        return true;
    }
}
