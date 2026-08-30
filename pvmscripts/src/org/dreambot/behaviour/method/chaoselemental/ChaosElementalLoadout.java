package org.dreambot.behaviour.method.chaoselemental;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum ChaosElementalLoadout {
    MSB_AMETHYST(
            new InventoryLoadout()
                    .addItem(ItemVariants.RANGING_POTION)
                    .addItem(ItemVariants.BLIGHTED_SUPER_RESTORE, 1, 1).setRefill(20)
                    .addItem(ItemID.KNIFE)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 25).setRefill(500),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)

                    .addItem(EquipmentSlot.ARROWS, ItemID.MITHRIL_BOLTS, 100, 200)
                    .setRefill(1_000)

                    .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70 && Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                    .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY).setRefill(5)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY).setRefill(5)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY).setRefill(5)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY).setRefill(5)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                    .addItem(EquipmentSlot.HANDS, ItemID.BLUE_DHIDE_VAMBRACES)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() > 50)
                    .addItem(EquipmentSlot.HANDS, ItemID.RED_DHIDE_VAMBRACES)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() > 60)
                    .addItem(EquipmentSlot.HANDS, ItemID.BLACK_DHIDE_VAMBRACES)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() > 70)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setRefill(5)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setRefill(5)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
    ),
    RUNE_CB(
            new InventoryLoadout()
                    .addItem(ItemVariants.RANGING_POTION)
                    .addItem(ItemVariants.BLIGHTED_SUPER_RESTORE, 1, 1).setRefill(20)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 26).setRefill(500),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)

                    .addItem(EquipmentSlot.ARROWS, ItemID.MITHRIL_BOOTS, 100, 200)
                    .setRefill(1_000)

                    .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70 && Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                    .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY).setRefill(5)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY).setRefill(5)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY).setRefill(5)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY).setRefill(5)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                    .addItem(EquipmentSlot.HANDS, ItemID.BLUE_DHIDE_VAMBRACES)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() > 50)
                    .addItem(EquipmentSlot.HANDS, ItemID.RED_DHIDE_VAMBRACES)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() > 60)
                    .addItem(EquipmentSlot.HANDS, ItemID.BLACK_DHIDE_VAMBRACES)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() > 70)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setRefill(5)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setRefill(5)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
    ),
    RCB_DIAMOND_E(
            new InventoryLoadout()
                    .addItem(ItemVariants.RANGING_POTION)
                    .addItem(ItemVariants.BURNING_AMULET)
                    .addItem(ItemVariants.BLIGHTED_SUPER_RESTORE, 3, 3)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 21),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, ItemID.DIAMOND_BOLTS_E, 50, 100)
                    .setRefill(1_000)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                    .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY).setRefill(5)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY).setRefill(5)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY).setRefill(5)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY).setRefill(5)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                    .addItem(EquipmentSlot.HANDS, ItemID.BLUE_DHIDE_VAMBRACES)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() > 50)
                    .addItem(EquipmentSlot.HANDS, ItemID.RED_DHIDE_VAMBRACES)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() > 60)
                    .addItem(EquipmentSlot.HANDS, ItemID.BLACK_DHIDE_VAMBRACES)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() > 70)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setRefill(5)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setRefill(5)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
    ),
    ;

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    ChaosElementalLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }

    public boolean isFulfilled() {
        return inventoryLoadout.isFulfilled() && equipmentLoadout.isFulfilled();
    }
}
