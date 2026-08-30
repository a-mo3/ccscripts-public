package org.dreambot.behaviour.method.calvarion;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

public class CalvarionBaseLoadouts {
    public static final EquipmentLoadout CALVARION_DHIDE_BASE = new EquipmentLoadout()
            .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
            .setRefill(20)
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .setEnabledCondition(() -> !OwnedItems.contains(ItemVariants.SALVE_AMULET))
            .setRefill(20)
            .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET)
            .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))

            .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
            .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40))
            .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50))
            .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60))
            .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70))


            .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY).setRefill(5)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40 || Skills.getRealLevel(Skill.DEFENCE) < 40)
            .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
            .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
            .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)


            .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
            .setRefill(20)
            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .setRefill(10)
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
            .setRefill(20);
}
