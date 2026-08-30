package org.dreambot.behaviour.gdk;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.script.ScriptSettings;

import java.util.Arrays;
import java.util.function.Supplier;

public class Restock extends Fractal {
    public Restock(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.SHORTBOW)
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 20)
                .addItem(EquipmentSlot.WEAPON, ItemID.WILLOW_SHORTBOW)
                .setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 30, 20))

                .addItem(EquipmentSlot.WEAPON, ItemID.YEW_SHORTBOW)
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50 && Skills.getRealLevel(Skill.RANGED) >= 40)

                .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW)
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 50)

                .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.IRON_ARROW, 1, 500))
                .setRefill(2000)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 20)

                .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.MITHRIL_ARROW, 1, 500))
                .setRefill(2000)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, ScriptSettings.getSettingsData().useRuneArrows ? 40 : 100, 20))

                .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUNE_ARROW, 1, 500))
                .setRefill(2000)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 40 && ScriptSettings.getSettingsData().useRuneArrows)

                // legs
                .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS).setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
                .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)


                .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY).setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50 || Skills.getRealLevel(Skill.DEFENCE) < 40)
                .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                .setEnabledCondition(() -> OwnedItems.containsAny(
                        Arrays.stream(ItemVariants.AVAS.getIds()).mapToInt(x -> x).toArray())
                )

                .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .setRefill(5)
        ;

        this.acceptCondition = () -> acceptCondition.get() || !equipmentFulfilledCached();

        this.inventoryLoadout = new InventoryLoadout()
                .setStrictSupplier(() -> !Combat.isInWild())
                .addItem(ItemID.JUG_OF_WINE, ScriptSettings.getSettingsData().foodCount).setRefill(1000)
                .addItem(ItemVariants.LOOTING_BAG)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().useLootingBag
                        && OwnedItems.containsAny(Arrays.stream(ItemVariants.LOOTING_BAG.getIds())
                        .mapToInt(x -> x)
                        .toArray()))
                .addItem(ItemVariants.STAMINA_POTION)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().useStaminaPotion)
                .addItem(ItemVariants.RANGE_POTION)
                .setRefill(50)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().useBoostPotions)
        ;
    }

    Timer cache = new Timer(5000);
    Boolean isFulfilled = null;

    private boolean equipmentFulfilledCached() {
        if (isFulfilled == null) {
            isFulfilled = this.equipmentLoadout.isFulfilled();
        }

        if (cache.finished()) {
            cache.reset();
            isFulfilled = this.equipmentLoadout.isFulfilled();
        }

        return isFulfilled;
    }
}
