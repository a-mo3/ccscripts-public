package org.dreambot.behaviour.method.callisto;

import lombok.Getter;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;

@Getter
public enum CallistoLoadout {
    DHIDE_TRIDENT(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(20)

                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)


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

                    .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 50)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),

    ROBE_SCEPTRE(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.MYSTIC_HAT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(2)

                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)


                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.MYSTIC_ROBE_TOP)
                    .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 40 && Skill.DEFENCE.getLevel() >= 20)
                    .setRefill(2)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
                    .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 40 && Skill.DEFENCE.getLevel() >= 20)
                    .setRefill(2)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(10)

                    .addItem(EquipmentSlot.WEAPON, ItemVariants.ACCURSED)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 50)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),

    DHIDE_SCEPTRE(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(20)

                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)


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

                    .addItem(EquipmentSlot.WEAPON, ItemVariants.ACCURSED)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 50)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),


    DHIDE_RCB(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(20)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

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

                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, ItemID.RUBY_BOLTS_E, 50, 50)
                    .setRefill(250)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 50)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),
    DHIDE_WEBWEAVER(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(20)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

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

                    .addItem(EquipmentSlot.WEAPON, ItemVariants.WEBWEAVER_BOW)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 50)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),

    DHIDE_SCEPTRE_NO_OCCULT(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(20)



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

                    .addItem(EquipmentSlot.WEAPON, ItemVariants.ACCURSED)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 50)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),

    LOKIS_C_RAG(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(20)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setRefill(10)

                    .addItem(EquipmentSlot.CHEST, ItemID.SNAKESKIN_BODY)
                    .setRefill(10)

                    .addItem(EquipmentSlot.LEGS, ItemID.SNAKESKIN_CHAPS)
                    .setRefill(10)

                    .addItem(EquipmentSlot.HANDS, ItemID.SNAKESKIN_VAMBRACES)
                    .setRefill(10)

                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, ItemID.EMERALD_BOLTS_E, 20, 20)
                    .setRefill(250)

                    .addItem(EquipmentSlot.RING, ItemID.RING_OF_RECOIL)
                    .setRefill(20),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 50)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),

    ROBE_TRIDENT(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .setRefill(20)

                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.MYSTIC_ROBE_TOP)
                    .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 40 && Skill.DEFENCE.getLevel() >= 20)
                    .setRefill(2)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
                    .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 40 && Skill.DEFENCE.getLevel() >= 20)
                    .setRefill(2)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(10)

                    .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT)

                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 1, 1)
                    .setRefill(40)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.ANTIDOTE4_5952, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 8)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 14)
                    .setRefill(600)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 50)
                    .setRefill(600)
                    .setStrict(true),
            false
    ),
    ;


    final EquipmentLoadout equipmentLoadout;
    final InventoryLoadout inventoryLoadout;
    final boolean isRange;

    CallistoLoadout(EquipmentLoadout equipmentLoadout, InventoryLoadout loadout, boolean isRange) {
        this.equipmentLoadout = equipmentLoadout;
        this.inventoryLoadout = loadout;
        this.isRange = isRange;
    }

    public boolean isFulfilled() {
        return inventoryLoadout.isFulfilled() && equipmentLoadout.isFulfilled();
    }
}
