package org.dreambot.behaviour.method.undeaddruids;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum UndeadDruidLoadout {
    MONK_RANGE(
            new InventoryLoadout()
                    .addItem(ItemID.RANGING_POTION4)
                    .setRefill(20)
                    .addItem(ItemID.PRAYER_POTION4, 4)
                    .setRefill(20)
                    .addItem(ItemVariants.SKILLS_NECKLACE)
                    .setRefill(5)
                    .addItem(ItemID.LOBSTER, 2)
                    .setRefill(300),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET).enabledIfOwned()

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30 && Skill.RANGED.getLevel() >= 30)
                    .addItem(EquipmentSlot.FEET, ItemID.MIXED_HIDE_BOOTS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60 && Skill.DEFENCE.getLevel() >= 50)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)

                    .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70 && Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_DART, 200, 1000),
            Skill.RANGED
    ),
    D_HIDES_RANGE(
            new InventoryLoadout()
                    .addItem(ItemID.RANGING_POTION4)
                    .setRefill(20)
                    .addItem(ItemID.PRAYER_POTION4)
                    .setRefill(20)
                    .addItem(ItemVariants.SKILLS_NECKLACE)
                    .setRefill(5)
                    .addItem(ItemID.LOBSTER, 5, 10)
                    .setRefill(300),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET).enabledIfOwned()

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.GREEN_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)
//                    .addItem(EquipmentSlot.CHEST, ItemID.MIXED_HIDE_TOP)
//                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 50 && Skill.RANGED.getLevel() >= 60)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70)
//                    .addItem(EquipmentSlot.LEGS, ItemID.MIXED_HIDE_LEGS)
//                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30 && Skill.RANGED.getLevel() >= 30)
                    .addItem(EquipmentSlot.FEET, ItemID.MIXED_HIDE_BOOTS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60 && Skill.DEFENCE.getLevel() >= 50)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)

                    .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70 && Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_DART, 200, 1000)
            ,
            Skill.RANGED
    ),
    MIXED_HIDE_MELEE(
            new InventoryLoadout()
                    .addItem(ItemID.STRENGTH_POTION4)
                    .setRefill(20)
                    .addItem(ItemID.PRAYER_POTION4)
                    .setRefill(20)
                    .addItem(ItemVariants.SKILLS_NECKLACE)
                    .setRefill(5)
                    .addItem(ItemID.LOBSTER, 5, 10)
                    .setRefill(300),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET).enabledIfOwned()

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.GREEN_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)
                    .addItem(EquipmentSlot.CHEST, ItemID.MIXED_HIDE_TOP)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 50 && Skill.RANGED.getLevel() >= 60)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70)
                    .addItem(EquipmentSlot.LEGS, ItemID.MIXED_HIDE_LEGS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30 && Skill.RANGED.getLevel() >= 30)
                    .addItem(EquipmentSlot.FEET, ItemID.MIXED_HIDE_BOOTS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60 && Skill.DEFENCE.getLevel() >= 50)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.WEAPON, ItemID.SARADOMIN_SWORD),
            Skill.ATTACK
    ),
    OBSIDIAN_MELEE(
            new InventoryLoadout()
                    .addItem(ItemID.STRENGTH_POTION4)
                    .setRefill(20)
                    .addItem(ItemID.PRAYER_POTION4)
                    .setRefill(20)
                    .addItem(ItemVariants.SKILLS_NECKLACE)
                    .setRefill(5)
                    .addItem(ItemID.LOBSTER, 5, 10)
                    .setRefill(300),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(5)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET).enabledIfOwned()

                    // oby gear
                    .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
                    .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
                    .addItem(EquipmentSlot.HAT, ItemID.OBSIDIAN_HELMET)
                    .addItem(EquipmentSlot.HAT, ItemID.HELM_OF_NEITIZNOT).setEnabledCondition(PaidQuest.THE_FREMENNIK_ISLES::isFinished)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)

                    .addItem(EquipmentSlot.WEAPON, ItemID.SARADOMIN_SWORD)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 70)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned(),
            Skill.ATTACK
    ),
    ;

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;
    public final Skill mode;

    UndeadDruidLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout, Skill mode) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
        this.mode = mode;
    }

    public boolean isFulfilled() {
        return inventoryLoadout.isFulfilled() && equipmentLoadout.isFulfilled();
    }
}
