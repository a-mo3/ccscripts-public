package org.dreambot.behaviour.method.greendragon;

import lombok.Getter;
import org.dreambot.CondHelper;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

@Getter
public enum GDKLoadout {
    URSINE(
            new KillGreenDragons(() -> true),
            new InventoryLoadout(GDKBaseLoadouts.stdInv),
            new EquipmentLoadout(GDKBaseLoadouts.baseMeleeLoadout)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.URSINE_CHAINMACE)
    ),
    VIGGORAS(
            new KillGreenDragons(() -> true),
            new InventoryLoadout(GDKBaseLoadouts.stdInv),
            new EquipmentLoadout(GDKBaseLoadouts.baseMeleeLoadout)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.VIGGORA_CHAINMACE)
    ),
    WHIP(
            new KillGreenDragons(() -> true),
            new InventoryLoadout(GDKBaseLoadouts.stdInv),
            new EquipmentLoadout(GDKBaseLoadouts.baseMeleeLoadout)
                    .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SWORD)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SWORD)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 30, 20))
                    .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SWORD)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 40, 30))
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SWORD)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 60, 40))
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SWORD)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 70, 60))
                    .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 100, 70))
    ),

    URSINE_LOBSTERS(
            new KillGreenDragons(() -> true),
            new InventoryLoadout(GDKBaseLoadouts.stdInv),
            new EquipmentLoadout(GDKBaseLoadouts.baseMeleeLoadout)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.URSINE_CHAINMACE)
    ),
    VIGGORAS_LOBSTERS(
            new KillGreenDragons(() -> true),
            new InventoryLoadout(GDKBaseLoadouts.stdInv),
            new EquipmentLoadout(GDKBaseLoadouts.baseMeleeLoadout)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.VIGGORA_CHAINMACE)
    ),

    RCB_LOBSTERS(
            new KillGreenDragons(() -> true),
            new InventoryLoadout(GDKBaseLoadouts.rangingInv),
            new EquipmentLoadout(GDKBaseLoadouts.dhideBase)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUNITE_BOLTS, 30, 120))
    ),

    DARTS_LOBSTERS(
            new KillGreenDragons(() -> true),
            new InventoryLoadout(GDKBaseLoadouts.stdInv),
            new EquipmentLoadout(GDKBaseLoadouts.dhideBase)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.RUNE_DART, 100, 200))
    ),
    WHIP_LOBSTERS(
            new KillGreenDragons(() -> true),
            new InventoryLoadout(GDKBaseLoadouts.stdInv),
            new EquipmentLoadout(GDKBaseLoadouts.baseMeleeLoadout)
                    .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SWORD)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SWORD)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 30, 20))
                    .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SWORD)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 40, 30))
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SWORD)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 60, 40))
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SWORD)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 70, 60))
                    .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 100, 70))
    ),
    WATER_RUNES(
            new KillGreenDragons(() -> true).setWaterMagic(),
            new InventoryLoadout(GDKBaseLoadouts.wineInv)
                    .addItem(ItemID.MIND_RUNE, 250).setRefill(2000)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.MAGIC, 23, 5))
                    .addItem(ItemID.CHAOS_RUNE, 250).setRefill(2000)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.MAGIC, 47, 23))
                    .addItem(ItemID.DEATH_RUNE, 250).setRefill(2000)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.MAGIC, 65, 47))
                    .addItem(ItemID.BLOOD_RUNE, 250).setRefill(2000)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.MAGIC, 85, 65))
                    .addItem(ItemID.WRATH_RUNE, 250).setRefill(2000)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 85)
                    .addItem(ItemID.AIR_RUNE, 900).setRefill(4000),
            new EquipmentLoadout(GDKBaseLoadouts.baseSaladRobes)
                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER)
    );

    final KillGreenDragons method;
    final InventoryLoadout inventoryLoadout;
    final EquipmentLoadout equipmentLoadout;

    GDKLoadout(KillGreenDragons method, InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.method = method;
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}
