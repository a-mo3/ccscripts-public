package org.dreambot.behaviour.misc;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;
import java.util.Objects;

public class RechargeBlowpipe extends Fractal implements ChatListener {
    InventoryLoadout rechargeLoadout = new InventoryLoadout()
            .addItem(ItemID.TOXIC_BLOWPIPE_EMPTY)
            .setEnabledCondition(() -> OwnedItems.contains(ItemID.TOXIC_BLOWPIPE_EMPTY))
            .addItem(ItemID.TOXIC_BLOWPIPE)
            .setEnabledCondition(() -> OwnedItems.contains(ItemID.TOXIC_BLOWPIPE) && !OwnedItems.contains(ItemID.TOXIC_BLOWPIPE_EMPTY))
            .addItem(ItemID.ZULRAHS_SCALES, 4000)
            .addItem(ItemID.RUNE_DART, 2000);
    public static boolean shouldRecharge;

    public RechargeBlowpipe() {
        setSimpleName("Recharge blowpipe");
        this.inventoryLoadout = rechargeLoadout;
        this.eventBreakCondition = () -> !Worlds.getCurrent().isMembers();
        Client.getInstance().addEventListener(this);
        prependLogic = () -> {
            // if you have bp equipped get unequip it
            if (Equipment.contains(ItemID.TOXIC_BLOWPIPE, ItemID.TOXIC_BLOWPIPE_EMPTY)) {
                log("Unequip BP");
                // if inv full drop the cheapest item in inv
                if (Inventory.isFull()) {
                    Item cheapest = Inventory.all()
                            .stream()
                            .filter(Objects::nonNull)
                            .filter(Item::isTradable)
                            .min(Comparator.comparingInt(Item::getLivePrice))
                            .orElse(null);
                    log("Drop cheapest item " + cheapest);
                    if (cheapest != null) Inventory.drop(cheapest.getId());
                    return true;
                }
                Equipment.unequip(EquipmentSlot.WEAPON);
                return true;
            }
            return false;
        };

        this.afterLoadouts = () -> {
            if (hasEmptied && ItemVariants.BLOWPIPE.getItem() != null) {
                if (Widgets.isOpen()) Widgets.closeAll();
                Inventory.combine(ItemVariants.BLOWPIPE.getItem().getId(), ItemID.ZULRAHS_SCALES);
                Sleep.sleep(1000);
                Inventory.combine(ItemVariants.BLOWPIPE.getItem().getId(), ItemID.RUNE_DART);
                hasEmptied = false;
                return true;
            }

            return false;
        };
    }

    @Override
    public boolean isValid() {
        return OwnedItems.contains(ItemID.TOXIC_BLOWPIPE_EMPTY) || shouldRecharge;
    }

    // first need to empty a blowpipe so dart and scales dont get unbalanced
    boolean hasEmptied = false;

    @Override
    public int onLoop() {
        if (Client.isDynamicRegion()) {
            Logger.info("Telly out to recharge blowpipe");
            return ReactionGenerator.getQuick();
        }

        if (BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) > 50) {
            Logger.info("Going to GE");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getNormal();
        }

        if (Bank.isOpen() || GrandExchange.isOpen()) {
            Widgets.closeAll();
        }

        if (Inventory.isFull()) new BankAllInventoryEvent().execute();

        // unequip blowpipe OR
        if (Equipment.contains(ItemID.TOXIC_BLOWPIPE, ItemID.TOXIC_BLOWPIPE_EMPTY)) {
            Equipment.unequip(EquipmentSlot.WEAPON);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.TOXIC_BLOWPIPE_EMPTY)) hasEmptied = true;

        // empty out the blowpipe
        if (!hasEmptied) {
            WidgetChild unchargeYes = Widgets.get(584, 1);
            if (unchargeYes != null && unchargeYes.isVisible() && unchargeYes.interact("Yes")) {
                Antiban.sleepUntil(() -> !Inventory.contains(ItemID.TOXIC_BLOWPIPE), 2400);
                hasEmptied = true;
                return ReactionGenerator.getNormal();
            }

            Item blowpipe = Inventory.get(ItemID.TOXIC_BLOWPIPE);
            if (Inventory.interact(blowpipe, "Uncharge")) {
                Antiban.sleepUntil(() -> Widgets.get(584, 1) != null, 2400);
            }

            if (Inventory.contains(ItemID.TOXIC_BLOWPIPE) && !hasEmptied) {
                Inventory.interact(ItemID.TOXIC_BLOWPIPE, "Uncharge");
            }
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        String lower = message.getMessage().toLowerCase();
        boolean isChargeWarning = lower.contains("blowpipe needs to be charged") ||
                lower.contains("toxic blowpipe is out")
                || lower.contains("blowpipe has run out")
                || lower.contains("blowpipe contains no");
        if (isChargeWarning) shouldRecharge = true;
        if (message.getMessage().contains("Darts: <col=007f00>Rune dart")) shouldRecharge = false;
    }

}