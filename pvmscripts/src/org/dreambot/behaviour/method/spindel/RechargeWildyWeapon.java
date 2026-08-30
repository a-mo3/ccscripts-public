package org.dreambot.behaviour.method.spindel;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.method.revs.behaviour.ExitRevs;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class RechargeWildyWeapon extends Fractal implements ChatListener {
    public static boolean shouldRecharge;
    // i have no idea what this was for.
    Timer t = new Timer(3 * 1000 * 60);
    int chargedId;
    int unchargedId;
    int rechargeQuantity;

    public static boolean needsToRecharge = false;

    public RechargeWildyWeapon(int unchargedId, int chargedId, Supplier<Boolean> exitDangerSupplier, int rechargeQuantity) {
        this.rechargeQuantity = rechargeQuantity;
        this.chargedId = chargedId;
        this.unchargedId = unchargedId;
        this.eventBreakCondition = () -> !Worlds.getCurrent().isMembers() || (Inventory.emptySlotCount() > 0 && !Equipment.isSlotEmpty(EquipmentSlot.WEAPON));
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(unchargedId)
                .setEnabledCondition(() -> OwnedItems.contains(unchargedId) && !OwnedItems.contains(chargedId))
                .addItem(chargedId)
                .setEnabledCondition(() -> OwnedItems.contains(chargedId))
                .addItem(ItemID.REVENANT_ETHER, 1000 + rechargeQuantity)
                .setEnabledCondition(() -> !OwnedItems.contains(chargedId) || Inventory.contains(unchargedId))
                .addItem(ItemID.REVENANT_ETHER, rechargeQuantity)
                .setRefill(rechargeQuantity * 2)
                .setEnabledCondition(() -> OwnedItems.contains(chargedId))
        ;

        this.prependLogic = () -> {
            if (Equipment.isSlotFull(EquipmentSlot.WEAPON)) {
                if (Inventory.isFull()) new BankAllInventoryEvent().execute();
                if (Widgets.isOpen()) Widgets.closeAll();
                Equipment.unequip(EquipmentSlot.WEAPON);
            }

            return exitDangerSupplier.get();
        };

        Client.getInstance().addEventListener(this);
    }

    @Override
    public int onLoop() {
        if (OwnedItems.contains(chargedId)) shouldRecharge = false;
        // if not recharging sceptre_u you only need like 300
        if (Inventory.contains(chargedId) && Inventory.count(ItemID.REVENANT_ETHER) > rechargeQuantity + 50) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getQuick();
            }

            Bank.deposit(ItemID.REVENANT_ETHER, 1000);
            Sleep.sleepUntil(() -> Inventory.count(ItemID.REVENANT_ETHER) < rechargeQuantity + 50, 2400);
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.isOpen() || Bank.isOpen()) {
            Logger.info("Close all widgets when recharging a wildy item " + unchargedId);
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }

        Logger.info(chargedId);
        Logger.info(unchargedId);
        Item s = Inventory.get(x -> x.getId() == unchargedId || x.getId() == chargedId);
        Item e = Inventory.get(ItemID.REVENANT_ETHER);
        if (e == null || s == null) {
            Logger.info("Tried to recharge but an item was null ");
            Logger.info("S " + s);
            Logger.info("E " + e);
            t.reset();
            return ReactionGenerator.getLong();
        }
        if (s.useOn(e)) {
            t.reset();
            shouldRecharge = false;
        }
        return ReactionGenerator.getNormal();
    }


    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().toLowerCase().contains("no ammo left")) {
            ExitRevs.setForceLeave(true);
        }

        if (message.getMessage().toLowerCase().contains("not enough revenant ether")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("has run out of revenant")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("chainmace is out of charges")) {
            needsToRecharge = true;
        }
        if (message.getMessage().toLowerCase().contains("giving it a total of")) {
            needsToRecharge = false;
        }
    }
}
