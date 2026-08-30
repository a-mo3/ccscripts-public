package org.dreambot.behaviour.method.gemstone;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

// gemstone starts from lvl 3
public enum GemstoneCrabMagicLoadout {

    KNIVES(
            new InventoryLoadout()
                    // cast runes, these bring runes 1 level before they're needed so on level up it can be switched
                    .addItem(ItemID.MIND_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 22)
                    .addItem(ItemID.CHAOS_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 22 && Skills.getRealLevel(Skill.MAGIC) < 41)
                    .addItem(ItemID.DEATH_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.MAGIC) < 62)
                    .addItem(ItemID.BLOOD_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 61)
                    .addItem(ItemVariants.DIVINE_MAGIC_POTION, 1, 20)
                    .addItem(ItemID.SHARK, 2)
                    .addItem(ItemID.COINS_995, 1000, 5000),
            new EquipmentLoadout()
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

                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING).enabledIfOwned()
                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING_I).enabledIfOwned()

                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
    ),

    AIR_STAFF_NO_POTION(
            new InventoryLoadout()
                    // cast runes, these bring runes 1 level before they're needed so on level up it can be switched
                    .addItem(ItemID.MIND_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 22)
                    .addItem(ItemID.CHAOS_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 22 && Skills.getRealLevel(Skill.MAGIC) < 41)
                    .addItem(ItemID.DEATH_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.MAGIC) < 62)
                    .addItem(ItemID.BLOOD_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 61)
                    .addItem(ItemID.SHARK, 2, 22)
                    .setRefill(200)
                    .addItem(ItemID.COINS_995, 1000, 5000),
            new EquipmentLoadout()
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

                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING).enabledIfOwned()
                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING_I).enabledIfOwned()

                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
    ),

    AIR_STAFF(
            new InventoryLoadout()
                    // cast runes, these bring runes 1 level before they're needed so on level up it can be switched
                    .addItem(ItemID.MIND_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 17)
                    .addItem(ItemID.CHAOS_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 22 && Skills.getRealLevel(Skill.MAGIC) < 41)
                    .addItem(ItemID.DEATH_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.MAGIC) < 62)
                    .addItem(ItemID.BLOOD_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 61)
                    .addItem(ItemVariants.DIVINE_MAGIC_POTION, 1, 20)
                    .addItem(ItemID.SHARK, 2)
                    .addItem(ItemID.COINS_995, 1000, 5000),
            new EquipmentLoadout()
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

                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING).enabledIfOwned()
                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING_I).enabledIfOwned()

                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
    );

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    GemstoneCrabMagicLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}
