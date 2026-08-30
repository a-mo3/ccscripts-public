package org.dreambot.behaviour.method.vetion;

import lombok.Getter;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

@Getter
public enum VetionLoadout {
    SGS_MONK(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HAT, ItemID.HELM_OF_NEITIZNOT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 55 && PaidQuest.THE_FREMENNIK_ISLES.isFinished())

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setEnabledCondition(() -> !OwnedItems.contains(ItemVariants.SALVE_AMULET))
                    .setRefill(20)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET)
                    .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))

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

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(10)
//                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.GLACIAL_TEMOTLI)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 55)
                    .addItem(EquipmentSlot.WEAPON, ItemID.SARACHNIS_CUDGEL)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 65)
                    .addItem(EquipmentSlot.WEAPON, ItemID.SARADOMIN_SWORD)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 70)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.SKILLS_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),
    HIGHER_RISK_ZOMBIE_AXE(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HAT, ItemID.HELM_OF_NEITIZNOT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 55 && PaidQuest.THE_FREMENNIK_ISLES.isFinished())

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(20)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET)
                    .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))

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

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(10)
//                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.GLACIAL_TEMOTLI)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 55)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ZOMBIE_AXE)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 65)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.SKILLS_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),
    VIGGAORA_DHIDE(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HAT, ItemID.HELM_OF_NEITIZNOT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 55 && PaidQuest.THE_FREMENNIK_ISLES.isFinished())

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setEnabledCondition(() -> !OwnedItems.contains(ItemVariants.SALVE_AMULET))
                    .setRefill(20)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET)
                    .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))

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

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(10)
//                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.GLACIAL_TEMOTLI)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 55)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.VIGGORA_CHAINMACE)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 60)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.SKILLS_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),
    ;


    final EquipmentLoadout equipmentLoadout;
    final InventoryLoadout inventoryLoadout;

    VetionLoadout(EquipmentLoadout equipmentLoadout, InventoryLoadout loadout, boolean isRange) {
        this.equipmentLoadout = equipmentLoadout;
        this.inventoryLoadout = loadout;
    }

    public boolean isFulfilled() {
        return inventoryLoadout.isFulfilled() && equipmentLoadout.isFulfilled();
    }
}
