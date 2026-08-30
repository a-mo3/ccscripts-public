package org.dreambot.behaviour.method.gemstone;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

// gemstone starts from lvl 3
public enum GemstoneCrabRangeLoadout {
    KNIVES_NO_POTS(new InventoryLoadout()
            .addItem(ItemID.SHARK, 2, 22)
            .setRefill(200)
            .addItem(ItemID.COINS_995, 1000, 5000),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.CAPE, ItemID.AVAS_ATTRACTOR).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.AVAS_ACCUMULATOR).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.AVAS_ASSEMBLER).enabledIfOwned()
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    .addItem(EquipmentSlot.WEAPON, ItemID.IRON_KNIFE, 1, 500).setRefill(200)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_KNIFE, 1, 500).setEnabledCondition(() -> Skill.RANGED.getLevel() >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_KNIFE, 1, 500).setEnabledCondition(() -> Skill.RANGED.getLevel() >= 30)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_KNIFE, 1, 500).setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)

    ),

    KNIVES(new InventoryLoadout()
            .addItem(ItemVariants.DIVINE_RANGING_POTION, 1, 20)
            .addItem(ItemID.SHARK, 2)
            .addItem(ItemID.COINS_995, 1000, 5000),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.CAPE, ItemID.AVAS_ATTRACTOR).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.AVAS_ACCUMULATOR).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.AVAS_ASSEMBLER).enabledIfOwned()

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    .addItem(EquipmentSlot.WEAPON, ItemID.IRON_KNIFE, 1, 500).setRefill(200)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_KNIFE, 1, 500).setEnabledCondition(() -> Skill.RANGED.getLevel() >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_KNIFE, 1, 500).setEnabledCondition(() -> Skill.RANGED.getLevel() >= 30)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_KNIFE, 1, 500).setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)

    );

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    GemstoneCrabRangeLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}
