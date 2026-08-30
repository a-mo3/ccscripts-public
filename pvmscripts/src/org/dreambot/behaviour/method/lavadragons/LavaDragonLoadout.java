package org.dreambot.behaviour.method.lavadragons;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.LavaDragonSettings;

import java.util.function.Supplier;

public enum LavaDragonLoadout {
    WATER_STAFF(
            new InventoryLoadout()
                    .addItem(ItemID.AIR_RUNE, 2500)
                    .setRefill(() -> 2500 * LavaDragonConst.settings.restockMultiple)
                    .setBuyPrice(6)

                    .addItem(() -> LavaDragonLoadout.getCastRune()[0], () -> LavaDragonLoadout.getCastRune()[1])
                    .setRefill(() -> LavaDragonLoadout.getCastRune()[1] * LavaDragonConst.settings.restockMultiple)

                    .addItem(ItemVariants.AMULET_OF_GLORY)
                    .setRefill(10)
                    .setEnabledCondition(() -> LavaDragonConst.settings.useOccult && Skills.getRealLevel(Skill.MAGIC) >= 70)

                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> LavaDragonConst.settings.useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.PESTLE_AND_MORTAR)

                    .addItem(ItemID.KNIFE) // todo rotate with other slash items
                    .setRefill(() -> LavaDragonConst.settings.restockMultiple)
                    .addItem(ItemID.JUG_OF_WINE, 12, 12)
                    .setRefill(200),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .setEnabledCondition(() -> LavaDragonConst.settings.useOccult && Skills.getRealLevel(Skill.MAGIC) >= 70)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(10)
                    .setEnabledCondition(() -> !LavaDragonConst.settings.useOccult || Skills.getRealLevel(Skill.MAGIC) < 70)

                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER),
            () -> !Magic.canCast(getSpell())
    ),
    TRIDENT_SEAS_RECOMMENDED(
            new InventoryLoadout()
                    .addItem(ItemID.AIR_RUNE, 2500)
                    .setBuyPrice(6)
                    .setRefill(() -> 2500 * LavaDragonConst.settings.restockMultiple)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 75)

                    .addItem(() -> LavaDragonLoadout.getCastRune()[0], () -> LavaDragonLoadout.getCastRune()[1])
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 75)
                    .setRefill(() -> LavaDragonLoadout.getCastRune()[1] * LavaDragonConst.settings.restockMultiple)

                    .addItem(ItemVariants.AMULET_OF_GLORY)
                    .setRefill(10)
                    .setEnabledCondition(() -> LavaDragonConst.settings.useOccult && Skills.getRealLevel(Skill.MAGIC) >= 70)

                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> LavaDragonConst.settings.useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.PESTLE_AND_MORTAR)

                    .addItem(ItemID.KNIFE) // todo rotate with other slash items
                    .setRefill(() -> LavaDragonConst.settings.restockMultiple)
                    .addItem(ItemID.JUG_OF_WINE, 12, 12)
                    .setRefill(200),

            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .setEnabledCondition(() -> LavaDragonConst.settings.useOccult && Skills.getRealLevel(Skill.MAGIC) >= 70)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(10)
                    .setEnabledCondition(() -> !LavaDragonConst.settings.useOccult || Skills.getRealLevel(Skill.MAGIC) < 70)

                    // <75 water staff kit
                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 75)

                    .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 75),
            () -> Skills.getRealLevel(Skill.MAGIC) < 75 && !Magic.canCast(getSpell())
    ),
    ;

    final InventoryLoadout inventoryLoadout; // only enforced outside wilderness
    final EquipmentLoadout equipmentLoadout;
    final Supplier<Boolean> shouldExit; // so we can cleanly leave if we are without runes or whatnot

    LavaDragonLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout, Supplier<Boolean> shouldExit) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
        this.shouldExit = shouldExit;
    }

    public static Normal getSpell() {
        int mag = Skill.MAGIC.getLevel();
        LavaDragonSettings s = LavaDragonConst.settings;
        if (mag >= 65 && s.enabledWaterWave) return Normal.WATER_WAVE;
        if (mag >= 47 && s.enabledWaterBlast) return Normal.WATER_BLAST;
        if (mag >= 23 && s.enabledWaterBolt) return Normal.WATER_BOLT;
        return Normal.WATER_STRIKE;
    }

    private static Integer[] getCastRune() {
        return LavaDragonConst.spellMap.get(getSpell());
    }
}
