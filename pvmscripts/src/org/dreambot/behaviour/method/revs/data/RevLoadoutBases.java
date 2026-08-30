package org.dreambot.behaviour.method.revs.data;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.RevenantSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;

/**
 * base loadouts to be clones for some others, eg dhide base has all the dhides without a weapon
 */
public class RevLoadoutBases {
    public static final EquipmentLoadout dhideBase = new EquipmentLoadout()
            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new RevenantSettings()).useEtherBracelet)
            .addItem(EquipmentSlot.HANDS, ItemVariants.BRACELET_OF_ETHEREUM)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useEtherBracelet)

            // legs
            .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
            .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40))
            .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50))
            .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60))
            .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
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
            .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)
            .addItem(EquipmentSlot.AMULET, ItemID.AMULET_OF_AVARICE)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)

            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
            .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)

            .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
            .setRefill(5)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

            .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
            .setRefill(5)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30);


    public static final EquipmentLoadout xericanBase = new EquipmentLoadout()
            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new RevenantSettings()).useEtherBracelet)
            .addItem(EquipmentSlot.HANDS, ItemVariants.BRACELET_OF_ETHEREUM)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useEtherBracelet)

            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)
            .addItem(EquipmentSlot.AMULET, ItemID.AMULET_OF_AVARICE)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)

            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
            .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)

            .addItem(EquipmentSlot.HAT, ItemID.XERICIAN_HAT)
            .setRefill(5)

            .addItem(EquipmentSlot.CHEST, ItemID.XERICIAN_TOP)
            .setRefill(5)

            .addItem(EquipmentSlot.LEGS, ItemID.XERICIAN_ROBE)
            .setRefill(5);
}
