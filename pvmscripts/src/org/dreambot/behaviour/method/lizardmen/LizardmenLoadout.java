package org.dreambot.behaviour.method.lizardmen;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum LizardmenLoadout {
    D_HIDES_RANGE(
            new InventoryLoadout()
                    .addItem(ItemID.RANGING_POTION4)
                    .setRefill(20)
                    .addItem(ItemID.PRAYER_POTION4)
                    .setRefill(20)
                    .addItem(ItemVariants.ANTI_DOTE_PP)
                    .setRefill(20)
                    .addItem(ItemVariants.SKILLS_NECKLACE)
                    .setRefill(5)
                    .addItem(ItemID.NATURE_RUNE, 50, 250)
                    .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 55)
                    .addItem(ItemID.FIRE_RUNE, 500, 1500)
                    .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 55)
                    .addItem(ItemID.SHARK, 15)
                    .setRefill(300),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(10)
//                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET).enabledIfOwned()

                    .addItem(EquipmentSlot.HAT, ItemID.SHAYZIEN_HELM_5)
                    .addItem(EquipmentSlot.CHEST, ItemID.SHAYZIEN_BODY_5)
                    .addItem(EquipmentSlot.LEGS, ItemID.SHAYZIEN_GREAVES_5)
                    .addItem(EquipmentSlot.FEET, ItemID.SHAYZIEN_BOOTS_5)
                    .addItem(EquipmentSlot.HANDS, ItemID.SHAYZIEN_GLOVES_5)

//                    .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
//                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70 && Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW_I).enabledIfOwned()

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH).setRefill(10)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS).enabledIfOwned()
                    .addItem(EquipmentSlot.ARROWS, ItemID.AMETHYST_ARROW, 250, 500)
            ,
            Skill.RANGED
    ),

    D_HIDES_SUNLIGHT(
            new InventoryLoadout()
                    .addItem(ItemID.RANGING_POTION4)
                    .setRefill(20)
                    .addItem(ItemID.PRAYER_POTION4)
                    .setRefill(20)
                    .addItem(ItemVariants.ANTI_DOTE_PP)
                    .setRefill(20)
                    .addItem(ItemVariants.SKILLS_NECKLACE)
                    .setRefill(5)
                    .addItem(ItemID.NATURE_RUNE, 50, 250)
                    .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 55)
                    .addItem(ItemID.FIRE_RUNE, 500, 1500)
                    .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 55)
                    .addItem(ItemID.SHARK, 15)
                    .setRefill(300),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(10)
//                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET).enabledIfOwned()

                    .addItem(EquipmentSlot.HAT, ItemID.SHAYZIEN_HELM_5)
                    .addItem(EquipmentSlot.CHEST, ItemID.SHAYZIEN_BODY_5)
                    .addItem(EquipmentSlot.LEGS, ItemID.SHAYZIEN_GREAVES_5)
                    .addItem(EquipmentSlot.FEET, ItemID.SHAYZIEN_BOOTS_5)
                    .addItem(EquipmentSlot.HANDS, ItemID.SHAYZIEN_GLOVES_5)

                    .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70 && Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemID.HUNTERS_SUNLIGHT_CROSSBOW)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH).setRefill(10)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS).enabledIfOwned()
                    .addItem(EquipmentSlot.ARROWS, ItemID.MOONLIGHT_ANTLER_BOLTS, 250, 500)
            ,
            Skill.RANGED
    ),

    D_HIDES_ROSEWOOD_BLOWPIPE(
            new InventoryLoadout()
                    .addItem(ItemID.RANGING_POTION4)
                    .setRefill(20)
                    .addItem(ItemID.PRAYER_POTION4)
                    .setRefill(20)
                    .addItem(ItemVariants.ANTI_DOTE_PP)
                    .setRefill(20)
                    .addItem(ItemVariants.SKILLS_NECKLACE)
                    .setRefill(5)
                    .addItem(ItemID.NATURE_RUNE, 50, 250)
                    .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 55)
                    .addItem(ItemID.FIRE_RUNE, 500, 1500)
                    .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 55)
                    .addItem(ItemID.SHARK, 15)
                    .setRefill(300),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(10)
//                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET).enabledIfOwned()

                    .addItem(EquipmentSlot.HAT, ItemID.SHAYZIEN_HELM_5)
                    .addItem(EquipmentSlot.CHEST, ItemID.SHAYZIEN_BODY_5)
                    .addItem(EquipmentSlot.LEGS, ItemID.SHAYZIEN_GREAVES_5)
                    .addItem(EquipmentSlot.FEET, ItemID.SHAYZIEN_BOOTS_5)
                    .addItem(EquipmentSlot.HANDS, ItemID.SHAYZIEN_GLOVES_5)

//                    .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
//                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70 && Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemVariants.ROSEWOOD_BLOWPIPE)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH).setRefill(10)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS).enabledIfOwned()
            ,
            Skill.RANGED
    ),
    ;

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;
    public final Skill mode;

    LizardmenLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout, Skill mode) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
        this.mode = mode;
    }
}
