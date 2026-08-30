package org.dreambot.behaviour.method.spindel;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.SpindelSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;

public enum SpindelLoadout {
    CRAWS_DHIDE(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.CRAWS_BOW)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new SpindelSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.ENERGY_POTION4, 3)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4)
                    .setRefill(10)
                    .addItem(ItemID.RANGING_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 12)
                    .setRefill(600),
            true
    ),
    WEBWEAVER_DHIDE(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.WEBWEAVER_BOW)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new SpindelSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.ENERGY_POTION4, 3)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4)
                    .setRefill(10)
                    .addItem(ItemID.RANGING_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 12)
                    .setRefill(600),
            true
    ),

    URSINE_DHIDE(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.URSINE_CHAINMACE)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new SpindelSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.ENERGY_POTION4, 3)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4)
                    .setRefill(10)
                    .addItem(ItemID.STRENGTH_POTION4, 1) // todo maybe super combats but they are kinda pricey
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 12)
                    .setRefill(600)
                    .addItem(ItemID.ADAMANT_DART, 100)
                    .setRefill(800),
            false

    ),

    VIGGORAS_DHIDE(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.VIGGORA_CHAINMACE)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .setStrict(true)
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new SpindelSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.ENERGY_POTION4, 3)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4)
                    .setRefill(10)
                    .addItem(ItemID.STRENGTH_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 12)
                    .setRefill(600)
                    .addItem(ItemID.ADAMANT_DART, 100)
                    .setRefill(800),
            false

    ),

    URSINE_MONK(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(20)
//                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .setRefill(20)
//                    .addItem(EquipmentSlot.FEET, ItemID.LEAT)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.URSINE_CHAINMACE)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new SpindelSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.ENERGY_POTION4, 3)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4)
                    .setRefill(10)
                    .addItem(ItemID.STRENGTH_POTION4, 1) // todo maybe super combats but they are kinda pricey
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 12)
                    .setRefill(600)
                    .addItem(ItemID.ADAMANT_DART, 100)
                    .setRefill(800),
            false

    ),

    VIGGORAS_MONKS(
            new EquipmentLoadout()
                    .setStrict(true)
//                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .setRefill(20)
//                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(20)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.VIGGORA_CHAINMACE)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new SpindelSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.ENERGY_POTION4, 3)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4)
                    .setRefill(10)
                    .addItem(ItemID.STRENGTH_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 12)
                    .setRefill(600)
                    .addItem(ItemID.ADAMANT_DART, 100)
                    .setRefill(800),
            false

    ),


    URSINE_ANTIPK(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.URSINE_CHAINMACE)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new SpindelSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.ENERGY_POTION4, 3)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4)
                    .setRefill(10)
                    .addItem(ItemID.STRENGTH_POTION4, 1) // todo maybe super combats but they are kinda pricey
                    .setRefill(20)
                    .addItem(ItemID.SARADOMIN_BREW4, 4)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 15)
                    .setRefill(600)
                    .addItem(ItemID.ADAMANT_DART, 100)
                    .setRefill(800)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 5)
                    .setRefill(35),
            false

    ),

    CHUDGEL_MONKS(
            new EquipmentLoadout()
                    .setStrict(true)
//                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .setRefill(20)
//                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
//                    .setRefill(20)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.SARACHNIS_CUDGEL)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.ADAMANT_DART, 100)
                    .setRefill(800)
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new SpindelSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.ENERGY_POTION4, 3)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4)
                    .setRefill(10)
                    .addItem(ItemID.STRENGTH_POTION4, 1)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 12)
                    .setRefill(600),
            false
    ),


    CHUDGEL_DHIDE(
            new EquipmentLoadout()
                    .setStrict(true)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.SARACHNIS_CUDGEL)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemID.ADAMANT_DART, 100)
                    .setRefill(800)
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new SpindelSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.ENERGY_POTION4, 3)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4)
                    .setRefill(10)
                    .addItem(ItemID.STRENGTH_POTION4, 1) // todo maybe super combats but they are kinda pricey
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 12)
                    .setRefill(600),
            false
    ),
    ;

    final EquipmentLoadout equipmentLoadout;
    final InventoryLoadout loadout;
    public final boolean isRange;

    SpindelLoadout(EquipmentLoadout equipmentLoadout, InventoryLoadout loadout, boolean isRange) {
        this.equipmentLoadout = equipmentLoadout;
        this.loadout = loadout;
        this.isRange = isRange;
    }
}
