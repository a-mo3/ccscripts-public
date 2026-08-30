package org.dreambot.behaviour.gdk.melee;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.script.ScriptSettings;

import java.util.Arrays;
import java.util.function.Supplier;

public class MeleeRestock extends Fractal {

    public MeleeRestock(Supplier<Boolean> acceptCondition) {
        // this is replaced after the equipment loadout is made to combine and checked, for empty glories mostly
        super(acceptCondition);

        this.equipmentLoadout = new EquipmentLoadout()
                // rune, adamant, mithril, iron chestplate
                .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 20)
                .setRefill(5)
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
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 60, 40))
                .setRefill(5)
                // todo dragon sword
                .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SWORD)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, ScriptSettings.getSettingsData().useWhip ? 70 : 99, 60))
                .setRefill(5)
                .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 100, 70) && ScriptSettings.getSettingsData().useWhip)

                // glory
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .setRefill(5)

                // climbing boots
//                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)

                // anti dragon shield
                .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)
                .setRefill(10)
        ;
        this.acceptCondition = () -> acceptCondition.get() || !equipmentLoadout.isFulfilled();

        this.inventoryLoadout = new InventoryLoadout()
                .setStrictSupplier(() -> !Combat.isInWild())
                .addItem(ScriptSettings.getFoodId(), ScriptSettings.getSettingsData().foodCount)
                .setRefill(200)
                .addItem(ItemVariants.PRAYER_POTION, 1, ScriptSettings.getSettingsData().prayerPotCount)
                .setRefill(ScriptSettings.getSettingsData().prayerPotCount * 5)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().prayerMelee && ScriptSettings.getSettingsData().prayerPotCount > 0)
                .addItem(ItemVariants.ANTI_FIRE_POTION).setRefill(20)
                .addItem(ItemVariants.LOOTING_BAG)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().useLootingBag
                        && OwnedItems.containsAny(Arrays.stream(ItemVariants.LOOTING_BAG.getIds())
                        .mapToInt(x -> x)
                        .toArray()))
                .addItem(ItemVariants.COMBAT_POTION)
                .setRefill(50)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().useBoostPotions)

                .addItem(ItemVariants.STAMINA_POTION)
                .setRefill(30)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().useStaminaPotion)
        ; // todo make this configurable add antifire pots
    }
}
