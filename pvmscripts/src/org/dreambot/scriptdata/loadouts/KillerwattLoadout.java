package org.dreambot.scriptdata.loadouts;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.RevenantSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;

import java.util.Arrays;

public class KillerwattLoadout {
    public static final EquipmentLoadout DHIDE_DARTS = new EquipmentLoadout()
            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            // legs
            .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS).setRefill(5)
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
            .setEnabledCondition(() -> !SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)

            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)

            .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
            .setRefill(5)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

            .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
            .setRefill(5)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

            .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
            .setEnabledCondition(() -> OwnedItems.containsAny(
                    Arrays.stream(ItemVariants.AVAS.getIds()).mapToInt(x -> x).toArray())
            )
            // darts
            .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.IRON_KNIFE, 1, 1000))
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 10, 0))
            .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.MITHRIL_KNIFE, 1, 1000))
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 30, 10))
            .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.ADAMANT_KNIFE, 1, 1000))
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 40, 30))
            .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.RUNE_KNIFE, 1, 1000))
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 40);

    public static final InventoryLoadout FOOD = new InventoryLoadout()
            .addItem(ItemID.SWORDFISH, 22)
            .addItem(ItemID.HERB_SACK)
            .setEnabledCondition(() -> OwnedItems.contains(ItemID.HERB_SACK));
}
