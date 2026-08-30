package org.dreambot.behaviour.method.brutals;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public class BrutalLoadouts {
    // we're prayer flicking and safe spotting we shouldn't rly need anything
    public static final InventoryLoadout BRUTAL_INVENTORY = new InventoryLoadout()
            .addItem(ItemID.PRAYER_POTION4, 2, 2)
            .setRefill(20)
            .setEnabledCondition(() -> !BrutalPrayerFlick.CATACOMBS_OF_KOUREND.contains(Players.getLocal()) || ItemVariants.PRAYER_POTION.getItem() == null)
            .addItem(ItemID.ANTIFIRE_POTION4, 2, 2)
            .setRefill(20)
            .setEnabledCondition(() -> !BrutalPrayerFlick.CATACOMBS_OF_KOUREND.contains(Players.getLocal()) || ItemVariants.ANTI_FIRE_POTION.getItem() == null)
            .addItem(ItemID.JUG_OF_WINE, 12)
            .setRefill(100)
            .setEnabledCondition(() -> !BrutalPrayerFlick.CATACOMBS_OF_KOUREND.contains(Players.getLocal()) || !Inventory.contains(ItemID.JUG_OF_WINE))

            .addItem(ItemID.AIR_RUNE, 100, 5000)
            // cast runes, these bring runes 1 level before they're needed so on level up it can be switched
            .addItem(ItemID.MIND_RUNE, 100, 1600)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 23)
            .addItem(ItemID.CHAOS_RUNE, 100, 1600)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 25 && Skills.getRealLevel(Skill.MAGIC) < 47)
            .addItem(ItemID.DEATH_RUNE, 100, 1600)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 46 && Skills.getRealLevel(Skill.MAGIC) < 65)
            .addItem(ItemID.BLOOD_RUNE, 100, 1600)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 64 && Skills.getRealLevel(Skill.MAGIC) < 85)
            .addItem(ItemID.WRATH_RUNE, 100, 1600)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 84);

    public static final EquipmentLoadout WATER_STAFF = new EquipmentLoadout()
            .addItem(EquipmentSlot.HAT, ItemID.MYSTIC_HAT)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
            .addItem(EquipmentSlot.HAT, ItemID.BLOODBARK_HELM)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70)

            .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
            .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()
            .addItem(EquipmentSlot.CAPE, ItemID.GUTHIX_CAPE).enabledIfOwned()
            .addItem(EquipmentSlot.CAPE, ItemID.SARADOMIN_CAPE).enabledIfOwned()
            .addItem(EquipmentSlot.CAPE, ItemID.ZAMORAK_CAPE).enabledIfOwned()
            .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_GUTHIX_CAPE).enabledIfOwned()
            .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_SARADOMIN_CAPE).enabledIfOwned()
            .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_ZAMORAK_CAPE).enabledIfOwned()

            .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
            .addItem(EquipmentSlot.CHEST, ItemID.MYSTIC_ROBE_TOP)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
            .addItem(EquipmentSlot.CHEST, ItemID.BLOODBARK_BODY)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

            .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
            .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
            .addItem(EquipmentSlot.LEGS, ItemID.BLOODBARK_LEGS)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

            .addItem(EquipmentSlot.RING, ItemID.SEERS_RING)
            .addItem(EquipmentSlot.RING, ItemID.SEERS_RING_I).enabledIfOwned()
            .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER);
}
