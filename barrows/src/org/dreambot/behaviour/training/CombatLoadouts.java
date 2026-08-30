package org.dreambot.behaviour.training;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.function.Supplier;

public class CombatLoadouts {

    private static final Supplier<Integer> appropriateScimitar = () -> {
        int atk = Skills.getRealLevel(Skill.ATTACK);
        if (atk >= 40) return ItemID.RUNE_SCIMITAR;
        if (atk >= 30) return ItemID.ADAMANT_SCIMITAR;
        if (atk >= 20) return ItemID.MITHRIL_SCIMITAR;
        return ItemID.IRON_SCIMITAR;
    };

    private static final Supplier<Integer> appropriateHelm = () -> {
        int def = Skills.getRealLevel(Skill.DEFENCE);
        if (def >= 40) return ItemID.RUNE_FULL_HELM;
        if (def >= 30) return ItemID.ADAMANT_FULL_HELM;
        if (def >= 20) return ItemID.MITHRIL_FULL_HELM;
        return ItemID.IRON_FULL_HELM;
    };

    private static final Supplier<Integer> appropriateChest = () -> {
        int def = Skills.getRealLevel(Skill.DEFENCE);
        if (def >= 40) return ItemID.RUNE_CHAINBODY; // platebody reqs ds 1
        if (def >= 30) return ItemID.ADAMANT_PLATEBODY;
        if (def >= 20) return ItemID.MITHRIL_PLATEBODY;
        return ItemID.IRON_PLATEBODY;
    };

    private static final Supplier<Integer> appropriateLegs = () -> {
        int def = Skills.getRealLevel(Skill.DEFENCE);
        if (def >= 40) return ItemID.RUNE_PLATELEGS;
        if (def >= 30) return ItemID.ADAMANT_PLATELEGS;
        if (def >= 20) return ItemID.MITHRIL_PLATELEGS;
        return ItemID.IRON_PLATELEGS;
    };

    private static final Supplier<Integer> appropriateShield = () -> {
        int def = Skills.getRealLevel(Skill.DEFENCE);
        if (def >= 40) return ItemID.RUNE_KITESHIELD;
        if (def >= 30) return ItemID.ADAMANT_KITESHIELD;
        if (def >= 20) return ItemID.MITHRIL_KITESHIELD;
        return ItemID.IRON_KITESHIELD;
    };

    public static final InventoryLoadout COMBAT_INVENTORY_F2P = new InventoryLoadout()
            .addItem(ItemID.LOBSTER, 1, 25).setRefill(80);

    public static final EquipmentLoadout SCIMITAR_LOADOUT_F2P = new EquipmentLoadout()
            .addItem(EquipmentSlot.WEAPON, appropriateScimitar)
            .addItem(EquipmentSlot.HAT, appropriateHelm)
            .addItem(EquipmentSlot.CHEST, appropriateChest)
            .addItem(EquipmentSlot.LEGS, appropriateLegs)
            .addItem(EquipmentSlot.SHIELD, appropriateShield)
            ;

    public static final EquipmentLoadout newerLoadout = new EquipmentLoadout()
            .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 20)

            .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 30, 20))
            .setRefill(5)
            .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 40, 30))
            .setRefill(5)
            .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 100, 40))
            .setRefill(5)

            // rune adamant mithril iron platelegs
            .addItem(EquipmentSlot.LEGS, ItemID.IRON_PLATESKIRT)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 20)
            .setRefill(5)
            .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 30, 20))
            .setRefill(5)
            .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 40, 30))
            .setRefill(5)
            .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.DEFENCE, 100, 40))
            .setRefill(5)

            // dragon sword or rune sword or mithril
            .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SWORD)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SWORD)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 30, 20))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SWORD)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 40, 30))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SWORD)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 990, 40))
//            .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SWORD)
//            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 100, 60))
            ;


    public static final EquipmentLoadout SCIMITAR_LOADOUT_P2P = new EquipmentLoadout()
            .addItem(EquipmentSlot.WEAPON, appropriateScimitar)
//            .addItem(EquipmentSlot.HAT, ItemID.SANTA_HAT)
//            .setEnabledCondition(() -> !ScriptSettings.getSettingsData().disableSanta)
//            .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
//            .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
            .addItem(EquipmentSlot.SHIELD, appropriateShield)
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
//            .setMuleRequestAmount(100_000)
            ;
}
