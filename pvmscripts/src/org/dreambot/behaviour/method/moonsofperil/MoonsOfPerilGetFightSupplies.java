package org.dreambot.behaviour.method.moonsofperil;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MoonsOfPerilGetFightSupplies extends Fractal {
    public static final Area RESOURCES_STEAMWORKS = new Area(1509, 9696, 1521, 9684);

    public MoonsOfPerilGetFightSupplies(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Supplies");
    }

    @Override
    public int onLoop() {
        if ((Inventory.contains(ItemID.ABYSSAL_WHIP) && !Equipment.contains(ItemID.ABYSSAL_WHIP))
                || (!Inventory.contains(ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD) && !Equipment.contains(ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD))) {
            log("Equip whip and shield");
            Equipment.equip(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP);
            Equipment.equip(EquipmentSlot.SHIELD, ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD);
            return ReactionGenerator.getNormal();
        }

        Prayers.toggleQuickPrayer(false);

        if (!RESOURCES_STEAMWORKS.contains(Players.getLocal())) {
            log("Go to resource area");
            if (Walking.shouldWalk()) Walking.walk(RESOURCES_STEAMWORKS);
            return ReactionGenerator.getNormal();
        }

        if (Walking.getRunEnergy() < 80) {
            log("Get cuppa");
            ObjectUtil.interact("Cooking stove", "Make-cuppa");
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.VIAL, ItemID.KNIFE)) {
            log("Drop waste knives and vials");
            Inventory.dropAll(ItemID.VIAL, ItemID.KNIFE);
            return ReactionGenerator.getNormal();
        }

        GameObject crates = GameObjects.closest("Supply crates");
        if (crates == null) {
            log("Cant find supply crates");
            return ReactionGenerator.getNormal();
        }

        // get at least 2 moonlight potions
        int currentPotions = ItemVariants.MOONLIGHT_POTION.getInventoryCount();
        if (currentPotions < 2) {
            log("Get at least 2 potions");

            int vialsOfWater = Inventory.count(ItemID.VIAL_OF_WATER);
            if (vialsOfWater < (2 - currentPotions) || !Inventory.contains(ItemID.PESTLE_AND_MORTAR)) {
                if (Inventory.isFull()) {
                    log("Need space");
                    Inventory.dropAll(ItemID.VIAL, ItemID.VIAL_OF_WATER, ItemID.MOONLIGHT_GRUB);
                }
                log("Get vials of water & pestle");
                log(Arrays.toString(crates.getActions()));
                crates.interact(x -> x.contains("Herblore"));
                Sleep.sleepUntil(() -> Inventory.count(ItemID.VIAL_OF_WATER) >= 2, 4400);
                return ReactionGenerator.getNormal();
            }

            if (Inventory.count(ItemID.MOONLIGHT_GRUB_PASTE) < vialsOfWater) {
                log("Get grub paste");
                // grind grubs
                if (Inventory.count(ItemID.MOONLIGHT_GRUB) >= vialsOfWater) {
                    log("Grind grubs");
                    Inventory.combine(ItemID.PESTLE_AND_MORTAR, ItemID.MOONLIGHT_GRUB);
                    Sleep.sleepUntil(() -> !Inventory.contains(ItemID.MOONLIGHT_GRUB), 800);
                    return ReactionGenerator.getNormal();
                }

                // get grubs if needed
                GameObject grubbySapling = GameObjects.closest("Grubby sapling");
                if (grubbySapling != null) {
                    log("Get grubs");
                    grubbySapling.interact();
                    Sleep.sleepUntil(() -> Inventory.count(ItemID.MOONLIGHT_GRUB) == 2, 2400);
                } else {
                    log("Cant find grubby sapling");
                }
            }

            // mix grub and water
            Inventory.combine(ItemID.VIAL_OF_WATER, ItemID.MOONLIGHT_GRUB_PASTE);
            return ReactionGenerator.getNormal();
        }

        // combine and drop excess potions
        if (ItemVariants.MOONLIGHT_POTION.getInventoryCount() > 2) {
            log("Excess potions");
            // find 2 potions to combine
            List<Item> partialMoonPots = Inventory.all(x -> x.getName().contains("Moonlight potion"))
                    .stream()
                    .filter(x -> Integer.parseInt(x.getName().replaceAll("\\D", "")) < 4)
                    .collect(Collectors.toList());
            if (partialMoonPots.size() == 1) {
                log("drop one partial potion");
                partialMoonPots.get(0).interact("Drop");
                return ReactionGenerator.getNormal();
            }

            if (partialMoonPots.isEmpty()) {
                log("Drop full potion");
                Inventory.drop(ItemID.MOONLIGHT_POTION4);
                return ReactionGenerator.getNormal();
            }

            log("Combine two pots");
            partialMoonPots.get(0).useOn(partialMoonPots.get(1));
            return ReactionGenerator.getNormal();
        }

        log("Bream");
        if (Inventory.contains(ItemID.VIAL_OF_WATER, ItemID.MOONLIGHT_GRUB, ItemID.MOONLIGHT_GRUB_PASTE)) {
            log("Drop potion stuff");
            Inventory.dropAll(ItemID.VIAL_OF_WATER, ItemID.MOONLIGHT_GRUB, ItemID.MOONLIGHT_GRUB_PASTE);
            return ReactionGenerator.getNormal();
        }

        // get and cook bream
        if (Inventory.contains(ItemID.PESTLE_AND_MORTAR)) {
            log("Drop mortar");
            Inventory.drop(ItemID.PESTLE_AND_MORTAR);
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.contains(ItemID.BIG_FISHING_NET)) {
            crates.interact(x -> x.contains("Fishing"));
            Sleep.sleepUntil(() -> Inventory.contains(ItemID.BIG_FISHING_NET), 4400);
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.isFull()) {
            boolean currentlyFishing = PlayerSettings.getBitValue(9875) == 1;
            if (!currentlyFishing) {
                log("Start fishing");
                GameObject fishingSpot = GameObjects.closest("Fishing spot");
                if (fishingSpot != null) fishingSpot.interact();
                // todo level up dialogue will cause an issue
                Sleep.sleepUntil(() -> PlayerSettings.getBitValue(9875) == 1, 4400);
            }
            // todo could add logic to face the different bream directions here, i dont do that when im actually playing though
            return ReactionGenerator.getNormal();
        }

        // cook bream
        if (Inventory.contains(ItemID.RAW_BREAM)) {
            log("Cook bream");
            ObjectUtil.interact("Cooking stove", "Cook");
            Sleep.sleepUntil(() -> !Inventory.contains(ItemID.RAW_BREAM), () -> Players.getLocal().isAnimating(), 2400, 100);
        }
        return ReactionGenerator.getNormal();
    }
}
