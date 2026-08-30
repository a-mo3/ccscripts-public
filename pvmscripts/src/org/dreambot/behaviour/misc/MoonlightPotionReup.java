package org.dreambot.behaviour.misc;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.quests.perilousmoon.InstanceWalking;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class MoonlightPotionReup extends Fractal {
    public static final Area WHOLE_MOONLIGHT_DUNGEON = new Tile(1437, 9628, 1).getArea(100);
    public static final Tile NEAR_FOUNTAIN = new Tile(1442, 9625, 1);
    public static final Tile GRUBBY_SAPLING = new Tile(1483, 9687);

    @Override
    public boolean isValid() {
        WHOLE_MOONLIGHT_DUNGEON.setZ(Players.getLocal().getZ());
        return (WHOLE_MOONLIGHT_DUNGEON.contains(Players.getLocal()) || Client.isDynamicRegion())
                && (ItemVariants.MOONLIGHT_POTION.getItem() == null || Inventory.contains(ItemID.VIAL_OF_WATER));
    }

    @Override
    public int onLoop() {
        if (Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
            Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
        }

        if (!Inventory.contains(ItemID.VIAL, ItemID.VIAL_OF_WATER) && ItemVariants.MOONLIGHT_POTION.getItem() == null) {
            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getNormal();
        }


        // todo decant half pots into each other

        if (Inventory.contains(ItemID.VIAL)) {
            Logger.info("Getting water");
            GameObject fountain = GameObjects.closest("Ancient fountainhead");
            if (fountain == null || NEAR_FOUNTAIN.distance() > 10) {
                if (Walking.shouldWalk()) InstanceWalking.walk(NEAR_FOUNTAIN);
                return ReactionGenerator.getNormal();
            }

            Item vial = Inventory.get(ItemID.VIAL);
            if (vial != null) {
                vial.useOn(fountain);
                Antiban.sleepUntil(() -> !Inventory.contains(ItemID.VIAL), () -> Players.getLocal().isAnimating(), 1600, 100);
            }
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.VIAL_OF_WATER)) {
            Logger.info("Getting grubs and that");

            if (Inventory.contains(ItemID.MOONLIGHT_GRUB_PASTE)) {
                Inventory.get(ItemID.MOONLIGHT_GRUB_PASTE).useOn(ItemID.VIAL_OF_WATER);
                Sleep.sleepUntil(() -> !Inventory.contains(ItemID.MOONLIGHT_GRUB_PASTE), 6600);
                return ReactionGenerator.getNormal();
            }

            if (Inventory.contains(ItemID.MOONLIGHT_GRUB)) {
                Inventory.get(ItemID.MOONLIGHT_GRUB).useOn(ItemID.PESTLE_AND_MORTAR);
                Sleep.sleepUntil(() -> !Inventory.contains(ItemID.MOONLIGHT_GRUB), 6600);
                return ReactionGenerator.getNormal();
            }

            if (Inventory.emptySlotCount() < 1) {
                // shouldnt happen & idk what id want to drop if it does
                Logger.warn("no inv space???");
                new BankAllInventoryEvent().execute();
                return ReactionGenerator.getNormal();
            }

            GameObject sapling = GameObjects.closest("Grubby Sapling");
            if (sapling == null || sapling.distance() > 5) {
                if (Walking.shouldWalk()) InstanceWalking.walk(GRUBBY_SAPLING);
                return ReactionGenerator.getNormal();
            }

            Logger.info("Picking grubs");
            sapling.interact("Collect-from");
            Sleep.sleepUntil(() -> Inventory.isFull() || Inventory.count(ItemID.MOONLIGHT_GRUB) >= Inventory.count(ItemID.VIAL_OF_WATER),
                    () -> Players.getLocal().isAnimating(),
                    1200, 100);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
