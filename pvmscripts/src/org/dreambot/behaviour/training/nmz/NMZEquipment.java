package org.dreambot.behaviour.training.nmz;

import lombok.Getter;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.training.slayer.Helper;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum NMZEquipment {
    //    CHEAP(new EquipmentLoadout(CombatLoadouts.newerLoadout).addItem),
    OBSIDIAN(new EquipmentLoadout()
            .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 30, 20))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 40, 30))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 70, 40))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 100, 70))

            // oby gear
            .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
            .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
            .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
            .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
            .addItem(EquipmentSlot.HAT, ItemID.OBSIDIAN_HELMET)
            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            // todo add burger ring
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY) // todo consider fury
    ),

    FULL_OBSIDIAN(new EquipmentLoadout()
            .addItem(EquipmentSlot.WEAPON, ItemID.OBBY_SWORD_TOKTZXILAK)
            // oby gear
            .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
            .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
            .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
            .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
            .addItem(EquipmentSlot.HAT, ItemID.OBSIDIAN_HELMET)
            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .addItem(EquipmentSlot.SHIELD, ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD)
//            .addItem(EquipmentSlot.AMULET, ItemID.OBSIDIAN_AMULET)
    ),
    ;

    @Getter
    final EquipmentLoadout loadout;

    NMZEquipment(EquipmentLoadout loadout) {
        this.loadout = loadout;
    }
}
