package org.dreambot.behaviour.method.rdk;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.method.lavadragons.LavaDragonConst;
import org.dreambot.behaviour.method.lavadragons.LavaDragonLoadout;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.LavaDragonSettings;
import org.dreambot.scripts.RedDragonScript;

import java.util.Arrays;

public enum RedDragonLoadout {
    RANGE(
            new InventoryLoadout()
                    .addItem(ItemID.JUG_OF_WINE, 12)
                    .setRefill(600)
                    .addItem(ItemID.KNIFE)
                    .setRefill(20),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemID.SHORTBOW)

                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.WILLOW_SHORTBOW)

                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 30, 20))

                    .addItem(EquipmentSlot.WEAPON, ItemID.YEW_SHORTBOW)

                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50 && Skills.getRealLevel(Skill.RANGED) >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW)

                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 50 && Skills.getRealLevel(Skill.RANGED) < 61)

                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)

                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 61)

                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.IRON_ARROW, 1, 500))
                    .setRefill(2000)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 20)

                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.MITHRIL_ARROW, 1, 500))
                    .setRefill(2000)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 61, 20))

                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUBY_BOLTS_E, 500, 1000))
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 61) && !Equipment.contains(ItemID.RUBY_BOLTS_E))
                    // legs
                    .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)


                    .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .setEnabledCondition(() -> OwnedItems.containsAny(
                            Arrays.stream(ItemVariants.AVAS.getIds()).mapToInt(x -> x).toArray())
                    )

                    .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 61) // only when you have a crossbow

                    .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.SKILLS_NECKLACE)
                    .setRefill(5)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                    .setRefill(5)
    ),
    MAGIC(new InventoryLoadout()
            .addItem(() -> getCastRune()[0], () -> getCastRune()[1])
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 75)
            .addItem(ItemID.JUG_OF_WINE, 12)
            .setRefill(600)
            .addItem(ItemID.KNIFE)
            .setRefill(20),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 75)
                    // water staff < 75 then trident
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 75)
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)

                    .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)

                    .addItem(EquipmentSlot.CHEST, ItemID.MYSTIC_ROBE_TOP)
                    .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
                    .addItem(EquipmentSlot.HAT, ItemID.MYSTIC_HAT)
    ),
    ;

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    RedDragonLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }

    // gets the appropriate spell for your level
    public static Normal getSpell() {
        int mag = Skill.MAGIC.getLevel();
        if (mag >= 65) return Normal.WATER_WAVE;
        if (mag >= 47) return Normal.WATER_BLAST;
        if (mag >= 23) return Normal.WATER_BOLT;
        return Normal.WATER_STRIKE;
    }

    private static Integer[] getCastRune() {
        return LavaDragonConst.spellMap.get(getSpell());
    }
}
