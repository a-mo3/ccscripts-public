package org.dreambot.behaviour.method.scurrius;

import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.*;

import java.util.function.Supplier;

import static org.dreambot.behaviour.method.bluedragons.KillBlueDragon.AUGURY_UNLOCKED;
import static org.dreambot.behaviour.method.spindel.range.RangeAttackSpindel.RIGOUR_UNLOCKED;

public enum ScurriusMode {
    MELEE(new InventoryLoadout()
            .addItem(ItemID.PRAYER_POTION4, 4)
            .setRefill(32)
            .addItem(ItemID.SUPER_COMBAT_POTION4, 4)
            .setRefill(32)
            .addItem(ItemID.SHARK, 12)
            .setRefill(50)
            .addItem(ItemID.NATURE_RUNE, 100)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 55)
            .addItem(ItemID.FIRE_RUNE, 500)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 55)
            .addItem(ItemVariants.RING_OF_WEALTH),
            new EquipmentLoadout()
//                    .addItem(EquipmentSlot.HAT, ItemID.PURPLE_PARTYHAT)
                    // todo some prayer bonus here

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.FIGHTER_TORSO).enabledIfOwned()

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.FEET, ItemID.RUNE_BOOTS)
                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
                    .addItem(EquipmentSlot.WEAPON, ItemID.SARACHNIS_CUDGEL)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 65)
                    .addItem(EquipmentSlot.WEAPON, ItemID.BONE_MACE)
                    .enabledIfOwned(() -> Skills.getRealLevel(Skill.ATTACK) >= 50)

//                    .addItem(EquipmentSlot.SHIELD, ItemID.RUNE_KITESHIELD)
                    .addItem(EquipmentSlot.SHIELD, ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
                    .addItem(EquipmentSlot.SHIELD, ItemID.DRAGON_DEFENDER).enabledIfOwned()

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH),
            () -> isPietyUnlocked() ? Prayer.PIETY : Prayer.ULTIMATE_STRENGTH, // need 43 at least so ult str is safe assumption
            ItemVariants.SUPER_COMBAT_POTION,
            () -> {
                int missingStrengthBoost = Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getRealLevel(Skill.STRENGTH);
                int maxStrBoost = (5 + (int) (Skills.getRealLevel(Skill.STRENGTH) * 0.15));
                return missingStrengthBoost < (maxStrBoost / 2);
            }
    ),
    MAGIC(new InventoryLoadout()
            .addItem(ItemVariants.RING_OF_WEALTH)
            .addItem(ItemID.PRAYER_POTION4, 4)
            .setRefill(32)
            .addItem(ItemID.MAGIC_POTION4, 4)
            .setRefill(32)
            .addItem(ItemID.SHARK, () -> Equipment.contains(ItemID.BONE_STAFF) ? 10 : 14)
            .setRefill(50)

            // cast runes, these bring runes 1 level before they're needed so on level up it can be switched
            .addItem(ItemID.MIND_RUNE, 200, 1600)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 35)
            .addItem(ItemID.CHAOS_RUNE, 200, 1600)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 34 && Skills.getRealLevel(Skill.MAGIC) < 59)
            .addItem(ItemID.DEATH_RUNE, 200, 1600)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 58 && Skills.getRealLevel(Skill.MAGIC) < 75)
            .addItem(ItemID.BLOOD_RUNE, 200, 1600)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 74)

            .addItem(ItemID.NATURE_RUNE, 100)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 55)
            .addItem(ItemID.FIRE_RUNE, 500)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 55),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.HAT, ItemID.MYSTIC_HAT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.HAT, ItemID.SPLITBARK_HELM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.HAT, ItemID.BLOODBARK_HELM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.MYSTIC_ROBE_TOP)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.CHEST, ItemID.SPLITBARK_BODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLOODBARK_BODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.SPLITBARK_LEGS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLOODBARK_LEGS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING).enabledIfOwned()
                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING_I).enabledIfOwned()

                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
                    .addItem(EquipmentSlot.WEAPON, ItemID.BONE_STAFF)
                    .enabledIfOwned(() -> Skills.getRealLevel(Skill.MAGIC) >= 50)

                    // we bring a melee shield cause the magic -atk bonus is negligible, scurr has only +10 magic def, and rats kinda hurt
                    .addItem(EquipmentSlot.SHIELD, ItemID.RUNE_KITESHIELD)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 40),
            ScurriusMode::getBestMagePray,
            ItemVariants.MAGIC_POTION,
            () -> {
                int missingBoost = Skills.getBoostedLevel(Skill.MAGIC) - Skills.getRealLevel(Skill.MAGIC);
                return missingBoost <= 2;
            }
    ),
    RANGE(new InventoryLoadout()
            .addItem(ItemVariants.RING_OF_WEALTH)
            .addItem(ItemID.PRAYER_POTION4, 4)
            .setRefill(32)
            .addItem(ItemID.RANGING_POTION4, 6)
            .setRefill(32)
            .addItem(ItemID.SHARK, 14)
            .setRefill(50)
            .addItem(ItemID.NATURE_RUNE, 100)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 55)
            .addItem(ItemID.FIRE_RUNE, 500)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 55),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemID.COIF)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 50 && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 70 && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                    .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 70)

                    .addItem(EquipmentSlot.CAPE, ItemID.AVAS_ATTRACTOR).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.AVAS_ACCUMULATOR).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.AVAS_ASSEMBLER).enabledIfOwned()

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.IRON_ARROW, 400, 500))
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.MITHRIL_ARROW, 400, 500))
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 20)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.ADAMANT_ARROW, 400, 500))
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUNE_ARROW, 400, 500))
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemID.SHORTBOW)
                    .addItem(EquipmentSlot.WEAPON, ItemID.WILLOW_SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.YEW_SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 40)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 60)
                    .addItem(EquipmentSlot.WEAPON, ItemID.BONE_SHORTBOW)
                    .enabledIfOwned(() -> Skills.getRealLevel(Skill.RANGED) >= 50),
            ScurriusMode::getBestRangePray,
            ItemVariants.RANGE_POTION,
            () -> {
                int missingBoost = Skills.getBoostedLevel(Skill.RANGED) - Skills.getRealLevel(Skill.RANGED);
                int maxBoost = (4 + (int) (Skills.getRealLevel(Skill.RANGED) * 0.10));
                return missingBoost < (maxBoost / 2);
            }

    ),
    ;

    final InventoryLoadout inventoryLoadout;
    final EquipmentLoadout equipmentLoadout;
    final Supplier<Prayer> boostPrayerSupplier;
    final ItemVariant boostPotion;
    final Supplier<Boolean> boostPotCondition;

    ScurriusMode(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout, Supplier<Prayer> boostPrayerSupplier, ItemVariant boostPotion, Supplier<Boolean> boostPotCondition) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
        this.boostPrayerSupplier = boostPrayerSupplier;
        this.boostPotion = boostPotion;
        this.boostPotCondition = boostPotCondition;
    }

    public static Prayer getBestRangePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 74 && PlayerSettings.getBitValue(RIGOUR_UNLOCKED) == 1) return Prayer.RIGOUR;
        if (lvl >= 44) return Prayer.EAGLE_EYE;
        return Prayer.HAWK_EYE;
    }

    public static Prayer getBestMagePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 77 && PlayerSettings.getBitValue(AUGURY_UNLOCKED) == 1) return Prayer.AUGURY;
        if (lvl >= 45) return Prayer.MYSTIC_MIGHT;
        return Prayer.MYSTIC_LORE;
    }

    public static boolean isPietyUnlocked() {
        return Skill.PRAYER.getLevel() >= 70 && PlayerSettings.getBitValue(3909) == 8;
    }
}
