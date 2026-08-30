package org.dreambot.behaviour.method.sarachnis;

import lombok.Getter;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.method.huey.HueyConst;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum SarachnisLoadout {
    FULL_BLOOD(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemVariants.BLOOD_MOON_HELM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.STRENGTH) >= 75 && Skills.getRealLevel(Skill.DEFENCE) >= 50)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.FIGHTER_TORSO).enabledIfOwned()
                    .addItem(EquipmentSlot.CHEST, ItemVariants.BLOOD_MOON_CHESTPLATE)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.STRENGTH) >= 75 && Skills.getRealLevel(Skill.DEFENCE) >= 50)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.BLOOD_MOON_LEGS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.STRENGTH) >= 75 && Skills.getRealLevel(Skill.DEFENCE) >= 50)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.WEAPON, ItemID.GLACIAL_TEMOTLI)
                    .addItem(EquipmentSlot.WEAPON, ItemID.SARADOMIN_SWORD).setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 70)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DUAL_MACUAHUITL)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.STRENGTH) >= 75 && Skills.getRealLevel(Skill.ATTACK) >= 70)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS),
            HueyConst.MELEE_INV,
            Skill.ATTACK),
    BUDGET_PLUS(new EquipmentLoadout()
//            .addItem(EquipmentSlot.HAT, ItemID.)
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

            .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
            .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
            .addItem(EquipmentSlot.CHEST, ItemID.FIGHTER_TORSO).enabledIfOwned()

            .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
            .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)

            .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
            .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

            .addItem(EquipmentSlot.WEAPON, ItemID.GLACIAL_TEMOTLI)
            .addItem(EquipmentSlot.WEAPON, ItemID.SARADOMIN_SWORD).setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 70)

            .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)

            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned(),
            new InventoryLoadout(HueyConst.MELEE_INV)
                    // more than 70 attack you'll be using sara sword, less and you will be using glacials as default weapon.
                    .addItem(ItemID.GLACIAL_TEMOTLI).setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 70),
            Skill.ATTACK),
    MONK_MODE_EXTREME_BUDGET(new EquipmentLoadout()
//            .addItem(EquipmentSlot.HAT, ItemID.)
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

            .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)

            .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)

//            .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
            .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

            .addItem(EquipmentSlot.WEAPON, ItemID.GLACIAL_TEMOTLI)
            .addItem(EquipmentSlot.WEAPON, ItemID.SARADOMIN_SWORD).setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 70)

            .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)

            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned(),
            new InventoryLoadout(HueyConst.MELEE_INV)
                    // more than 70 attack you'll be using sara sword, less and you will be using glacials as default weapon.
                    .addItem(ItemID.GLACIAL_TEMOTLI).setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 70),
            Skill.ATTACK),

    MAGIC_EARTH_STAFF(
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

                    .addItem(EquipmentSlot.WEAPON, ItemID.DUST_BATTLESTAFF),
            new InventoryLoadout()
//                    .addItem(ItemID.AIR_RUNE, 1000, 3000)

                    // cast runes, these bring runes 1 level before they're needed so on level up it can be switched
                    .addItem(ItemID.MIND_RUNE, 1000, 4000)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 29)
                    .addItem(ItemID.CHAOS_RUNE, 1000, 4000)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 28 && Skills.getRealLevel(Skill.MAGIC) < 53)
                    .addItem(ItemID.DEATH_RUNE, 1000, 4000)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 52 && Skills.getRealLevel(Skill.MAGIC) < 70)
                    .addItem(ItemID.BLOOD_RUNE, 1000, 4000)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70)
                    .addItem(ItemVariants.PRAYER_POTION, 6, 6)
                    .setRefill(50)
                    .addItem(ItemID.SHARK, 20)
                    .setRefill(500), Skill.MAGIC),

    MAGE_AHRIMS_DHW(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemID.MYSTIC_HAT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.HAT, ItemID.BLOODBARK_HELM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)
                    .addItem(EquipmentSlot.HAT, ItemVariants.AHRIMS_HOOD)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70 && Skills.getRealLevel(Skill.DEFENCE) >= 70)

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
                    .addItem(EquipmentSlot.CHEST, ItemVariants.AHRIMS_ROBETOP)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 760 && Skills.getRealLevel(Skill.DEFENCE) >= 70)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLOODBARK_LEGS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.AHRIMS_ROBEBOTTOM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70 && Skills.getRealLevel(Skill.DEFENCE) >= 70)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING).enabledIfOwned()
                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING_I).enabledIfOwned()
                    // todo add and charge earth tome

                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_HUNTER_WAND),
            new InventoryLoadout()
                    .addItem(ItemID.AIR_RUNE, 5000, 15_000)
                    .addItem(ItemID.EARTH_RUNE, 10_000, 15_000)

                    // cast runes, these bring runes 1 level before they're needed so on level up it can be switched
                    .addItem(ItemID.MIND_RUNE, 1000, 4000)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 29)
                    .addItem(ItemID.CHAOS_RUNE, 1000, 4000)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 28 && Skills.getRealLevel(Skill.MAGIC) < 53)
                    .addItem(ItemID.DEATH_RUNE, 1000, 4000)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 52 && Skills.getRealLevel(Skill.MAGIC) < 70)
                    .addItem(ItemID.BLOOD_RUNE, 1000, 4000)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 79)
                    .addItem(ItemID.PRAYER_POTION4, 6, 6)
                    .setRefill(50)
                    .addItem(ItemID.SHARK, 16)
                    .setRefill(500), Skill.MAGIC);

    final EquipmentLoadout equipmentLoadout;
    final InventoryLoadout loadout;
    @Getter
    final Skill mode;

    SarachnisLoadout(EquipmentLoadout equipmentLoadout, InventoryLoadout loadout, Skill mode) {
        this.equipmentLoadout = equipmentLoadout;
        this.loadout = loadout;
        this.mode = mode;
    }
}
