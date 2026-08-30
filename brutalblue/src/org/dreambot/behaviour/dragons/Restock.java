package org.dreambot.behaviour.dragons;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class Restock extends Fractal {
    public static int FOOD = ItemID.SHARK;

    public Restock() {
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 61)

                .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 500, 1000))
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 61) && !Equipment.contains(ItemID.DIAMOND_BOLTS_E))

                // legs
//                .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS).setRefill(5)
//                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
//                .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS).setRefill(5)
//                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
//                .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS).setRefill(5)
//                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
//                .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS).setRefill(5)
//                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
//                .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS).setRefill(5)
//                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
//
//
//                .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY).setRefill(5)
//                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50)
//                .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY).setRefill(5)
//                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
//                .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY).setRefill(5)
//                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
//                .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY).setRefill(5)
//                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                .addItem(EquipmentSlot.CHEST, ScriptSettings.getChestID())
                .setRefill(5)
                .addItem(EquipmentSlot.LEGS, ScriptSettings.getLegID())
                .setRefill(5)

                .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                .setEnabledCondition(() -> OwnedItems.containsAny(
                        Arrays.stream(ItemVariants.AVAS.getIds()).mapToInt(x -> x).toArray())
                )

                .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)
                .setRefill(20)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 61) // only when you have a crossbow

                .addItem(EquipmentSlot.HAT, ScriptSettings.getHatID())
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                .addItem(EquipmentSlot.FEET, ScriptSettings.getBootID())
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                .addItem(EquipmentSlot.AMULET, ItemVariants.SKILLS_NECKLACE)
                .setRefill(5)

                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .setRefill(5)
        ;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(FOOD, 8)
                .setEnabledCondition(() -> !Inventory.contains(FOOD)
                        || BankLocation.GRAND_EXCHANGE.getArea(25).contains(Players.getLocal()))
                .setRefill(1000)

                .addItem(ItemVariants.RANGE_POTION, 1, 2)
//                .setEnabledCondition(() -> !Inventory.contains(ItemVariants.RANGE_POTION.getIds())
//                        || BankLocation.GRAND_EXCHANGE.getArea(25).contains(Players.getLocal()))
                .setRefill(25)

                .addItem(ItemID.PRAYER_POTION4, 2, 2)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.PRAYER_POTION4)
                        || BankLocation.GRAND_EXCHANGE.getArea(25).contains(Players.getLocal()))
                .setRefill(25)


                .addItem(ItemID.STAMINA_POTION4, 1, 1)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.STAMINA_POTION4)
                        || BankLocation.GRAND_EXCHANGE.getArea(25).contains(Players.getLocal()))
                .setRefill(25)

                .addItem(ItemID.KOUREND_CASTLE_TELEPORT, 5)
                .setEnabledCondition(() -> BankLocation.GRAND_EXCHANGE.getArea(25).contains(Players.getLocal()))
                .setRefill(25)
                // todo add high alch runes
                .setStrictSupplier(() -> !Inventory.contains(FOOD))

                .addItem(ItemVariants.ANTI_FIRE_POTION, 1, 1)
                .setRefill(10)
        ;

        this.setPrependLogic(() -> {
            Prayers.toggle(false, Prayer.PROTECT_FROM_MAGIC);


            if (SpecialWalker.INSIDE_AVAS_ROOM.contains(Players.getLocal())) {
                Magic.castSpell(Normal.HOME_TELEPORT);
                Sleep.sleepUntil(() -> !SpecialWalker.INSIDE_AVAS_ROOM.contains(Players.getLocal()), 35_000);
            }

            return false;
        });
    }

    @Override
    public boolean isValid() {
        if (BankLocation.GRAND_EXCHANGE.getArea(50).contains(Players.getLocal())) {
            return !equipmentLoadout.isFulfilled() || !inventoryLoadout.isFulfilled();
        }
        return !Inventory.contains(FOOD)
                || ItemVariants.PRAYER_POTION.getItem() == null
                || !equipmentLoadout.isFulfilled()
                || ItemVariants.ANTI_FIRE_POTION.getItem() == null && PlayerSettings.getBitValue(3981) < 3;
    }

    @Override
    public int onLoop() {
        Logger.info("Restock...");
        return ReactionGenerator.getNormal();
    }
}
