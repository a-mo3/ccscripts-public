package org.dreambot.behaviour.misc;

import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

public class RechargeBoneStaff extends Fractal implements ChatListener {
    InventoryLoadout rechargeLoadout = new InventoryLoadout()
            .addItem(ItemID.BONE_STAFF)
            .setEnabledCondition(() -> !Equipment.contains(ItemID.BONE_STAFF))
            .addItem(ItemID.CHAOS_RUNE, 2500)
            .setStrict(true);


    private static boolean needsToCharge = false;

    public RechargeBoneStaff() {
        super(() -> needsToCharge);
        Client.getInstance().addEventListener(this);
        this.inventoryLoadout = rechargeLoadout;
    }

    @Override
    public int onLoop() {
        if (Bank.isOpen()) {
            Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (!BankLocation.GRAND_EXCHANGE.getArea(20).contains(Players.getLocal())) {
            Logger.info("Walking to GE");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getQuick();
        }

        if (Equipment.contains(ItemID.BONE_STAFF)) {
            if (Inventory.isFull()) {
                new BankAllInventoryEvent().execute();
                return ReactionGenerator.getNormal();
            }

            Equipment.unequip(EquipmentSlot.WEAPON);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.canEnterInput()) {
            Logger.info("should type");
            Keyboard.type("1m", true);
            needsToCharge = false;
            return ReactionGenerator.getNormal() + 3400;
        }

        Item coins = Inventory.get(ItemID.CHAOS_RUNE);
        Item bStaff = Inventory.get(ItemID.BONE_STAFF);
        if (coins != null && bStaff != null) {
            coins.useOn(bStaff);
            Antiban.sleepUntil(Dialogues::canEnterInput, 2400);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }

    private boolean hasChargedTrident() {
//        if (Equipment.contains(ItemID.TRIDENT_OF_THE_SWAMP_E)) return true;
        if (Equipment.contains(ItemID.TRIDENT_OF_THE_SWAMP)) return true;
        return false;
    }

    private int getUnchargedTrident() {
        if (OwnedItems.contains(ItemID.UNCHARGED_TOXIC_TRIDENT) || Equipment.contains(ItemID.UNCHARGED_TRIDENT))
            return ItemID.UNCHARGED_TOXIC_TRIDENT;
        return ItemID.UNCHARGED_TRIDENT;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().contains("Your Bone staff now has")) needsToCharge = false;
        if (!Equipment.contains(ItemID.BONE_STAFF)) return;
        log("Msg " + message.getMessage());
        if (message.getMessage().contains("Your weapon has run")) {
            log("Needs to charge");
            needsToCharge = true;
        }
    }
}