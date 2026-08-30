package org.dreambot.behaviour.method.greendragon;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.GDKSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;

public class GDKBaseLoadouts {
    public static final EquipmentLoadout dhideBase = new EquipmentLoadout()
            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)

            // legs
            .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
            .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40))
            .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50))
            .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60))
            .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70))


            .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY).setRefill(5)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40 || Skills.getRealLevel(Skill.DEFENCE) < 40)
            .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
            .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
            .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)

            .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

            .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)

            .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30);

    public static final EquipmentLoadout baseMeleeLoadout = new EquipmentLoadout()
            .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 20)
            .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 30, 20))
            .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 40, 30))
            .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 100, 40))
            // rune adamant mithril iron platelegs
            .addItem(EquipmentSlot.LEGS, ItemID.IRON_PLATESKIRT)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 20)
            .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 30, 20))
            .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 40, 30))
            .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 100, 40))

            .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)

            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .setRefill(5)
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
            .setRefill(5);

    public static final InventoryLoadout stdInv = new InventoryLoadout()
            .addItem(ItemVariants.LOOTING_BAG)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new GDKSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
            .addItem(ItemID.ANTIFIRE_POTION4)
            .setRefill(25)
            .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new GDKSettings()).prayMelee)
            .setRefill(25)

            .addItem(ItemID.STRENGTH_POTION4)
            .setRefill(25)

            .addItem(ItemID.ATTACK_POTION3)
            .setRefill(25)

            .addItem(ItemID.LOBSTER, 16)
            .setRefill(300);

    public static final InventoryLoadout rangingInv = new InventoryLoadout()
            .addItem(ItemVariants.LOOTING_BAG)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new GDKSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
            .addItem(ItemID.ANTIFIRE_POTION4)
            .setRefill(25)
            .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new GDKSettings()).prayMelee)
            .setRefill(25)

            .addItem(ItemID.RANGING_POTION4)
            .setRefill(25)

            .addItem(ItemID.LOBSTER, 16)
            .setRefill(300);

//
//    public static final InventoryLoadout lobsterInv = new InventoryLoadout()
//            .addItem(ItemVariants.LOOTING_BAG)
//            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new GDKSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
//            .addItem(ItemID.ANTIFIRE_POTION4)
//            .setRefill(25)
//            .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
//            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new GDKSettings()).prayMelee)
//            .setRefill(25)
//            .addItem(ItemID.LOBSTER, 16)
//            .setRefill(300);

    public static final InventoryLoadout wineInv = new InventoryLoadout()
            .addItem(ItemVariants.LOOTING_BAG)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new GDKSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
            .addItem(ItemID.ANTIFIRE_POTION4)
            .setRefill(25)
            .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new GDKSettings()).prayMelee)
            .setRefill(25)
            .addItem(ItemID.JUG_OF_WINE, 16)
            .setRefill(300);

    public static final EquipmentLoadout baseSaladRobes = new EquipmentLoadout()
            .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)

            // todo reqs for salad robes
            .addItem(EquipmentSlot.HAT, ItemID.XERICIAN_HAT)
            .addItem(EquipmentSlot.CHEST, ItemID.XERICIAN_TOP)
            .addItem(EquipmentSlot.LEGS, ItemID.XERICIAN_ROBE)

            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING);
}
