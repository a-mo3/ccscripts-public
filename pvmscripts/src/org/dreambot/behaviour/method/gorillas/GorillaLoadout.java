package org.dreambot.behaviour.method.gorillas;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

public enum GorillaLoadout {
    STANDARD(
            new InventoryLoadout()
                    .addItem(ItemVariants.BLOWPIPE)
                    .addItem(ItemVariants.AVAS).enabledIfOwned()
                    // todo alch runes / rune pouch

                    .addItem(ItemVariants.RING_OF_DUELING).setRefill(10)
                    .addItem(ItemID.ROYAL_SEED_POD).enabledIfOwned()
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1).setRefill(20)
                    .addItem(ItemID.RANGING_POTION4, 1).setRefill(20)
                    .addItem(ItemID.SUPER_RESTORE4, 4).setRefill(30)
                    .addItem(ItemID.SHARK, 17).setRefill(200),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH).setRefill(10)

                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.VERAC_SKIRT)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET).setRefill(10)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.HAT, ItemID.HELM_OF_NEITIZNOT).setEnabledCondition(PaidQuest.THE_FREMENNIK_ISLES::isFinished)

                    .addItem(EquipmentSlot.WEAPON, ItemID.SARADOMIN_SWORD)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ARCLIGHT).enabledIfOwned()
                    .addItem(EquipmentSlot.WEAPON, ItemID.EMBERLIGHT).enabledIfOwned()

                    .addItem(EquipmentSlot.SHIELD, ItemID.DRAGON_DEFENDER).enabledIfOwned(() -> OwnedItems.contains(ItemID.ARCLIGHT, ItemID.EMBERLIGHT))

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(10)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
    ),
    SUNLIGHT_WHIP(
            new InventoryLoadout()
                    .addItem(ItemID.HUNTERS_SUNLIGHT_CROSSBOW)
                    .addItem(ItemVariants.AVAS).enabledIfOwned()
                    // todo alch runes / rune pouch

                    .addItem(ItemVariants.RING_OF_DUELING).setRefill(10)
                    .addItem(ItemID.ROYAL_SEED_POD).enabledIfOwned()
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1).setRefill(20)
                    .addItem(ItemID.RANGING_POTION4, 1).setRefill(20)
                    .addItem(ItemID.SUPER_RESTORE4, 4).setRefill(30)
                    .addItem(ItemID.SHARK, 17).setRefill(200),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH).setRefill(10)

                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)

                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.VERAC_SKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET).setRefill(10)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.HAT, ItemID.RUNE_FULL_HELM)
                    .addItem(EquipmentSlot.HAT, ItemID.HELM_OF_NEITIZNOT).setEnabledCondition(PaidQuest.THE_FREMENNIK_ISLES::isFinished)

                    .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ARCLIGHT).enabledIfOwned()
                    .addItem(EquipmentSlot.WEAPON, ItemID.EMBERLIGHT).enabledIfOwned()

                    .addItem(EquipmentSlot.SHIELD, ItemID.DRAGON_DEFENDER).enabledIfOwned(() -> OwnedItems.contains(ItemID.ARCLIGHT, ItemID.EMBERLIGHT))


                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(10)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.ARROWS, ItemID.MOONLIGHT_ANTLER_BOLTS, 100, 2000)

                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)
    ),
    STANDARD_KARILS(
            new InventoryLoadout()
                    .addItem(ItemVariants.BLOWPIPE)
                    .addItem(ItemVariants.AVAS).enabledIfOwned()
                    // todo alch runes / rune pouch

                    .addItem(ItemVariants.RING_OF_DUELING).setRefill(10)
                    .addItem(ItemID.ROYAL_SEED_POD).enabledIfOwned()
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1).setRefill(20)
                    .addItem(ItemID.RANGING_POTION4, 1).setRefill(20)
                    .addItem(ItemID.SUPER_RESTORE4, 4).setRefill(30)
                    .addItem(ItemID.SHARK, 17).setRefill(200),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH).setRefill(10)

                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)

                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.VERAC_SKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET).setRefill(10)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.HAT, ItemID.HELM_OF_NEITIZNOT).setEnabledCondition(PaidQuest.THE_FREMENNIK_ISLES::isFinished)

                    .addItem(EquipmentSlot.WEAPON, ItemID.SARADOMIN_SWORD)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ARCLIGHT).enabledIfOwned()
                    .addItem(EquipmentSlot.WEAPON, ItemID.EMBERLIGHT).enabledIfOwned()

                    .addItem(EquipmentSlot.SHIELD, ItemID.DRAGON_DEFENDER).enabledIfOwned(() -> OwnedItems.contains(ItemID.ARCLIGHT, ItemID.EMBERLIGHT))

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(10)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)
    ),
    ;

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    GorillaLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}
