package org.dreambot.behaviour;

import com.google.common.collect.ImmutableList;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.script.ScriptSettings;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BarrowSettings {
    public static final int MIN_PRAYER = 5;
    public static final int MIN_HEALTH = 30;
    public static final ImmutableList<Integer> FOOD_LIST = ImmutableList.of(
            ItemID.SHARK
    );

    public static final EquipmentLoadout RANGE_SWITCH = new EquipmentLoadout()
            .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW)
            .addItem(EquipmentSlot.CHEST, ScriptSettings::getRangeTorso)
            .addItem(EquipmentSlot.LEGS, ScriptSettings::getRangeLegs)
            .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
            .setEnabledCondition(() -> ItemVariants.AVAS.getItem() != null)
            .addItem(EquipmentSlot.AMULET, ItemID.AMULET_OF_FURY);

    public static final EquipmentLoadout MAGE_SWITCH = new EquipmentLoadout()
            .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT)
//            .addCape(ItemID.GUTHIX_CAPE)
            .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
            .addItem(EquipmentSlot.CHEST, ScriptSettings.getMeleeTorso())
            .addItem(EquipmentSlot.LEGS, ScriptSettings.getMeleeLegs())
            ;

    public static final InventoryLoadout START_INV = new InventoryLoadout()
            .addItem(ItemID.MAGIC_SHORTBOW, 1)
            .addItem(ScriptSettings::getRangeTorso, 1)
            .addItem(ScriptSettings::getRangeLegs, 1)
            .addItem(ItemVariants.AVAS)
            .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.AVAS))
            .addItem(ItemID.AMULET_OF_FURY)
            .addItem(ItemVariants.RING_OF_DUELING).setRefill(10)
//            .addItem(ItemID.UNCHARGED_TOXIC_TRIDENT)
//            .setEnabled(() -> !OwnedItems.contains(ItemID.UNCHARGED_TOXIC_TRIDENT) && !OwnedItems.contains(ItemID.TRIDENT_OF_THE_SWAMP))
            // ^ range loadout
            .addItem(ItemID.PRAYER_POTION4, 4, ScriptSettings.getPrayerPotionCount()).setRefill(100)
            .addItem(ItemID.STAMINA_POTION4, 1, 2).setRefill(20)
            .addItem(ItemID.SHARK, 5, 8).setRefill(100)
            .addItem(ItemID.BARROWS_TELEPORT, 1, 10).setRefill(100)
            .addItem(ItemID.SPADE)
//            .setMuleRequestAmount(500_000)
            ;

    public static final EquipmentLoadout START_EQUIPMENT = new EquipmentLoadout()
            .addItem(EquipmentSlot.CAPE, ItemVariants.MAGE_CAPE)

            .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT)
//            .setEnabledCondition(() -> Skills.getRealLevel(Skill.StaffMode.TRIDENT)

            .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
//            .addCape(ItemID.GUTHIX_CAPE)
            .addItem(EquipmentSlot.CHEST, ScriptSettings::getMeleeTorso)
            .addItem(EquipmentSlot.LEGS, ScriptSettings::getMeleeLegs)
            .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUNE_ARROW, 100, 700))
            .setEnabledCondition(() -> Equipment.count(ItemID.RUNE_ARROW) < 100)
            .addItem(EquipmentSlot.FEET, ItemID.RUNE_BOOTS)
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH).setRefill(10)
            .addItem(EquipmentSlot.HAT, ScriptSettings.getSettingsData().hatID)
            .setEnabledCondition(() -> ScriptSettings.getSettingsData().hatID > 0)
            .addItem(EquipmentSlot.HANDS, ScriptSettings.getSettingsData().gloveID)
            .setEnabledCondition(() -> ScriptSettings.getSettingsData().hatID > 0)

//            .setMuleRequestAmount(500_000)
            ;

    // weakness, switch
//    public static final ImmutableMap<Skill, EquipmentLoadout> SWITCH_MAP = ImmutableMap.of(
//            Skill.RANGED, RANGE_SWITCH,
//            Skill.MAGIC, MAGE_SWITCH
//    );

    public static final Map<Skill, EquipmentLoadout> SWITCH_MAP =
            Stream.of(new Object[][]{
                    {Skill.RANGED, RANGE_SWITCH},
                    {Skill.MAGIC, MAGE_SWITCH},
            }).collect(Collectors.toMap(data -> (Skill) data[0], data -> (EquipmentLoadout) data[1]));

}
