package org.dreambot.behaviour.training.agility.wild;

import lombok.Getter;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.ItemVariants;

@Getter
public enum RagLoadout {
    RAG_LOADOUT(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemID.SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.WILLOW_SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.YEW_SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 40)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 50)

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

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(10)

//                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
//                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
//                    .addItem(EquipmentSlot.FEET, ItemID.MIXED_HIDE_BOOTS)
//                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 50)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET).setRefill(10)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS).enabledIfOwned()

                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.IRON_ARROW, 1, 50))
                    .setRefill(2000)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.MITHRIL_ARROW, 1, 50))
                    .setRefill(2000)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 20)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUNE_ARROW, 1, 50))
                    .setRefill(2000)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
    ),

    EMERALD_E(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)

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

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(10)

//                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
//                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
//                    .addItem(EquipmentSlot.FEET, ItemID.MIXED_HIDE_BOOTS)
//                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 50)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET).setRefill(10)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS).enabledIfOwned()

                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.EMERALD_BOLTS_E, 50, 150))
                    .setRefill(2000)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
    )

    ;

    final EquipmentLoadout equipmentLoadout;

    RagLoadout(EquipmentLoadout equipmentLoadout) {
        this.equipmentLoadout = equipmentLoadout;
    }
}
