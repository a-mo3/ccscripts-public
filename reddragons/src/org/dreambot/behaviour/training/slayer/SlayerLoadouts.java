package org.dreambot.behaviour.training.slayer;


import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;


public class SlayerLoadouts {
    public static final int FOOD = ItemID.SHARK;

    public static final EquipmentLoadout MELEE_LOADOUT = new EquipmentLoadout()
//            .addItem(EquipmentSlot.HAT, ItemID.BLACK_MASK)
//            .setEnabledCondition(() -> Skills.getRealLevel(Skill.STRENGTH) >= 20
//                    && Skills.getRealLevel(Skill.DEFENCE) >= 10
//                    && Combat.getCombatLevel() >= 40
//            )
            // rune, adamant, mithril, iron chestplate
            .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 20)
            .setRefill(5)
            .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 30, 20))
            .setRefill(5)
            .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 40, 30))
            .setRefill(5)
            .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 100, 40))
            .setRefill(5)

            // rune adamant mithril iron platelegs
            .addItem(EquipmentSlot.LEGS, ItemID.IRON_PLATESKIRT)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 20)
            .setRefill(5)
            .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 30, 20))
            .setRefill(5)
            .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 40, 30))
            .setRefill(5)
            .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 100, 40))
            .setRefill(5)

            // dragon sword or rune sword or mithril
            .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 30, 20))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 40, 30))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 70, 40))
            .setRefill(5)
            // todo dragon sword
//            .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SWORD)
//            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 100, 60) && !ScriptSettings.getSettingsData().useWhip)
//            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 100, 70))

            // glory
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .setRefill(5)

            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .setRefill(5)


            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .setEnabledCondition(() -> Combat.getCombatLevel() >= 85)
            .setRefill(5)

            // climbing boots
//                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)

            // anti dragon shield
//            .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)
//            .setRefill(10)
            ;

    public static final EquipmentLoadout PRAYER_LOADOUT = new EquipmentLoadout()
//            .addItem(EquipmentSlot.HAT, ItemID.BLACK_MASK)
//            .setEnabledCondition(() -> Skills.getRealLevel(Skill.STRENGTH) >= 20
//                    && Skills.getRealLevel(Skill.DEFENCE) >= 10
//                    && Combat.getCombatLevel() >= 40
//            )

            .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
            .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)

            .addItem(EquipmentSlot.CAPE, ItemID.GUTHIX_CLOAK)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.PRAYER) >= 40)

            // dragon sword or rune sword or mithril
            .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 30, 20))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 40, 30))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 70, 40))
            .setRefill(5)
            // todo dragon sword
//            .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SWORD)
//            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 100, 60) && !ScriptSettings.getSettingsData().useWhip)
//            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 100, 70))

            // glory
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .setRefill(5)

            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .setRefill(5)


            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .setEnabledCondition(() -> Combat.getCombatLevel() >= 85)
            .setRefill(5);


    public static final InventoryLoadout FOOD_GEM_INVENTORY = new InventoryLoadout()
            .addItem(ItemID.SHARK, 1, 18)
            .setRefill(200)
            .addItem(ItemID.ENCHANTED_GEM, 1)
            // teleports for Xieve & turael
            .addItem(ItemVariants.GAMES_NECKLACE)
            .setEnabledCondition(() -> Combat.getCombatLevel() < 85)
            .setRefill(5);


    public static final InventoryLoadout PRAYER_GEM_INVENTORY = new InventoryLoadout()
            .addItem(ItemID.SHARK, 1, 6)
            .setRefill(200)
            .addItem(ItemVariants.PRAYER_POTION, 1, 8)
            .setRefill(50)
            .addItem(ItemID.ENCHANTED_GEM, 1)
            // teleports for Xieve & turael
            .addItem(ItemVariants.GAMES_NECKLACE)
            .setEnabledCondition(() -> Combat.getCombatLevel() < 85)
            .setRefill(5);

    public static final InventoryLoadout CANNON_LOADOUT = new InventoryLoadout(FOOD_GEM_INVENTORY);
//            .addItem(ItemID.CANNONBALL, 5, 2000)
//            .addItem(ItemID.CANNON_BASE)
//            .setEnabledCondition(() -> PlayerSettings.getConfig(2) < 1)
//            .addItem(ItemID.CANNON_STAND)
//            .setEnabledCondition(() -> PlayerSettings.getConfig(2) < 2)
//            .addItem(ItemID.CANNON_BARRELS)
//            .setEnabledCondition(() -> PlayerSettings.getConfig(2) < 3)
//            .addItem(ItemID.CANNON_FURNACE)
//            .setEnabledCondition(() -> PlayerSettings.getConfig(2) < 4);
}
