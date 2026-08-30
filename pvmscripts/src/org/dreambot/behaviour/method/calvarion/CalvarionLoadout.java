package org.dreambot.behaviour.method.calvarion;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.CalvarionSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;

public enum CalvarionLoadout {
    URSINE_DHIDE(
            new EquipmentLoadout(CalvarionBaseLoadouts.CALVARION_DHIDE_BASE)
                    .setStrict(true)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.URSINE_CHAINMACE),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)

                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 16)
                    .setRefill(600),
            false

    ),

    VIGGORAS_DHIDE(
            new EquipmentLoadout(CalvarionBaseLoadouts.CALVARION_DHIDE_BASE)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.VIGGORA_CHAINMACE),
            new InventoryLoadout()
                    .setStrict(true)
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20).addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 16)
                    .setRefill(600),
            false

    ),

    URSINE_MONK(
            new EquipmentLoadout()
                    .setStrict(true)
//                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setEnabledCondition(() -> !OwnedItems.contains(ItemVariants.SALVE_AMULET))
                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET)
                    .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))
                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .setRefill(20)
//                    .addItem(EquipmentSlot.FEET, ItemID.LEAT)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(10)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.URSINE_CHAINMACE)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 16)
                    .setRefill(600),
            false

    ),

    VIGGORAS_MONKS(
            new EquipmentLoadout()
                    .setStrict(true)
//                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setEnabledCondition(() -> !OwnedItems.contains(ItemVariants.SALVE_AMULET))
                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET)
                    .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))
                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(10)
                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .setRefill(20)
//                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.VIGGORA_CHAINMACE)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20).addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 16)
                    .setRefill(600),
            false

    ),


    URSINE_ANTIPK(
            new EquipmentLoadout(CalvarionBaseLoadouts.CALVARION_DHIDE_BASE)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.URSINE_CHAINMACE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.SARADOMIN_BREW4, 4)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 6)
                    .setRefill(600)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 10)
                    .setRefill(600)
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
                    .setEnabledCondition(() -> !OwnedItems.contains(ItemVariants.SALVE_AMULET))
                    .setRefill(20)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET)
                    .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))
                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .setRefill(20)
                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .setRefill(20)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .setRefill(10)
//                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
//                    .setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.SARACHNIS_CUDGEL)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(20),
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS_DEVICE),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20).addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 16)
                    .setRefill(600),
            false
    ),


    CHUDGEL_DHIDE(
            new EquipmentLoadout(CalvarionBaseLoadouts.CALVARION_DHIDE_BASE)
                    .addItem(EquipmentSlot.WEAPON, ItemID.SARACHNIS_CUDGEL),
            new InventoryLoadout()
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 4, 4)
                    .setRefill(40)
                    .addItem(ItemID.STAMINA_POTION4, 1)
                    .setEnabledCondition(() -> !Combat.isInWild())
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 4)
                    .setRefill(200)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 16)
                    .setRefill(600),
            false
    ),
    ;

    final EquipmentLoadout equipmentLoadout;
    final InventoryLoadout loadout;
    public final boolean isRange;

    CalvarionLoadout(EquipmentLoadout equipmentLoadout, InventoryLoadout loadout, boolean isRange) {
        this.equipmentLoadout = equipmentLoadout;
        this.loadout = loadout;
        this.isRange = isRange;
    }
}
