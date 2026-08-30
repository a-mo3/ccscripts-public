package org.dreambot.scriptdata.loadouts;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.*;
import org.dreambot.fractals.util.OwnedItems;

public enum AviansieLoadout {
    DEFAULT(
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4).setRefill(20)
                    .setEnabledCondition(() -> !Combat.isInWild() || ItemVariants.BLIGHTED_SUPER_RESTORE.getItem() == null)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 20)
                    .setEnabledCondition(() -> !Combat.isInWild() || !Inventory.contains(ItemID.BLIGHTED_MANTA_RAY))
                    .setRefill(1_000)
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemVariants.BURNING_AMULET)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(45)
                    .addItem(ItemVariants.RANGE_POTION, 1, 1)
                    .setRefill(45)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setStrictSupplier(() -> !Combat.isInWild()),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_FULL_HELM)
//                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 70)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 70)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS).enabledIfOwned()

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.GREEN_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40 && FreeQuest.DRAGON_SLAYER_I.isFinished())
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 70)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(10)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(5)

                    .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.ADAMANT_DART, 1, 200))
                    .setRefill(5_000)

                    .addItem(EquipmentSlot.ARROWS, ItemID.UNHOLY_BLESSING),
            Skill.RANGED),

    ROSEWOOD_BLOWPIPE(
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4).setRefill(20)
                    .setEnabledCondition(() -> !Combat.isInWild() || ItemVariants.BLIGHTED_SUPER_RESTORE.getItem() == null)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 20)
                    .setEnabledCondition(() -> !Combat.isInWild() || !Inventory.contains(ItemID.BLIGHTED_MANTA_RAY))
                    .setRefill(1_000)
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemVariants.BURNING_AMULET)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(45)
                    .addItem(ItemVariants.RANGE_POTION, 1, 1)
                    .setRefill(45)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setStrictSupplier(() -> !Combat.isInWild()),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_FULL_HELM)
//                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 70)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 70)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS).enabledIfOwned()

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.GREEN_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40 && FreeQuest.DRAGON_SLAYER_I.isFinished())
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(10)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(5)

                    .addItem(EquipmentSlot.WEAPON, ItemVariants.ROSEWOOD_BLOWPIPE)

                    .addItem(EquipmentSlot.ARROWS, ItemID.UNHOLY_BLESSING),
            Skill.RANGED),
    ;
    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;
    public final Skill mode;

    AviansieLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout, Skill mode) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
        this.mode = mode;
    }
}
