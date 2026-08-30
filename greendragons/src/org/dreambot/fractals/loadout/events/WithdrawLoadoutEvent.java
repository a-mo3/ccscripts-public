package org.dreambot.fractals.loadout.events;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.*;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Accessors(chain = true)
@Unobfuscated
public class WithdrawLoadoutEvent extends AbstractResponseEvent<WithdrawLoadoutEvent.Response> {
    private final InventoryLoadout inventoryLoadout;
    private final EquipmentLoadout equipmentLoadout;
    @Setter
    private boolean buyRemainder = true;
    @Setter
    private boolean strict = false;
    @Setter
    private boolean muleRequiredGP = true;
    @Setter
    private boolean unequipItems = false;

    public WithdrawLoadoutEvent(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;

//        Logger.info("Withdraw event");
//        if (inventoryLoadout != null) {
//            Logger.info("INVENTORY LOADOUT " + inventoryLoadout.isFulfilled());
//            inventoryLoadout.loadoutItems
//                    .forEach(x -> Logger.info(String.format("%s - %s - Min: %d Max: %d ", x.getItemName(), x.getItemId(), x.getMin(), x.getMax())));
//        }
//
//        if (equipmentLoadout != null) {
//            Logger.info("EQUIPMENT LOADOUT " + equipmentLoadout.isFulfilled());
//            equipmentLoadout.getLoadoutList()
//                    .stream()
//                    .map(LoadoutMapEntry::getItem)
//                    .forEach(x -> Logger.info(String.format("%s - %s - Min: %d Max: %d ", x.getItemName(), x.getItemId(), x.getMin(), x.getMax())));
//        }
//
//        Logger.info("Bank State");
//        Bank.all()
//                .stream()
//                .filter(Objects::nonNull)
//                .forEach(x -> Logger.info(String.format("%s -%d * %d", x.getName(), x.getID(), x.getAmount())));
//        Logger.info("Inventory state");
//        Inventory.all()
//                .stream()
//                .filter(Objects::nonNull)
//                .forEach(x -> Logger.info(String.format("%s -%d * %d", x.getName(), x.getID(), x.getAmount())));
//        Logger.info("Equipment state");
//        Equipment.all()
//                .stream()
//                .filter(Objects::nonNull)
//                .forEach(x -> Logger.info(String.format("%s -%d * %d", x.getName(), x.getID(), x.getAmount())));
    }

    enum Response {
        SUCCESS,
        NOT_ENOUGH_GP
    }

    @Override
    public int onLoop() {
        // ensure bank cache
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.isOpen()) Bank.close();
            if (Walking.shouldWalk()) BankUtil.openClosest();
            return sleep();
        }

        if (equipmentLoadout != null && !equipmentLoadout.isFulfilled()) {
            // do equipment loadout
            Logger.info("Doing equipment");
            List<EquipmentLoadoutItem> missingItems = equipmentLoadout.getMissingItems();
            List<LoadoutItem> loadoutItems = missingItems.stream().map(x -> (LoadoutItem) x).collect(Collectors.toList());

            if (Inventory.contains(Item::isNoted)) {
                Logger.info("Banking all for noted equipment item");
                new BankAllInventoryEvent()
                        .setInterruptCondition(getBreakCondition())
                        .execute();
            }

            missingItems.forEach(x -> Logger.info("Missing " + x.getItemName()));

            List<LoadoutItem> itemsToBuy = loadoutItems.stream()
                    .filter(x -> !Equipment.contains(x.getItemId()))
                    .filter(x -> OwnedItems.count(x.getItemId(), true) < x.getMin())
                    .collect(Collectors.toList());
            if (!itemsToBuy.isEmpty()) {
                Logger.info("Equipment buy items");
                buyItems(itemsToBuy);
                return sleep();
            }

            List<LoadoutItem> itemsToWithdraw = loadoutItems.stream()
                    .filter(x -> !Equipment.contains(x.getItemId()))
                    .filter(x -> !Inventory.contains(x.getItemId()))
                    .filter(x -> OwnedItems.contains(x.getItemId()))
                    .collect(Collectors.toList());
            if (!itemsToWithdraw.isEmpty()) {
                itemsToWithdraw.forEach(x -> Logger.info(x.getItemName()));
                Logger.info("Equipment withdrawal: " + new WithdrawItemsFromBankEvent(itemsToWithdraw)
//                        .setStrict(true)
                                .setBreakCondition(getBreakCondition())
                                .executed()
                );
                return sleep();
            }

            // equip
            List<Integer> idsToEquip = loadoutItems.stream()
                    .map(LoadoutItem::getItemId)
                    .filter(x -> !Equipment.contains(x))
                    .filter(Inventory::contains)
                    .filter(OwnedItems::contains)
                    .collect(Collectors.toList());

            if (Bank.isOpen() || GrandExchange.isOpen()) {
                Widgets.closeAll();
                Sleep.sleepUntil(() -> !Bank.isOpen(), 1000);
            }
            for (Integer id : idsToEquip) {
                Inventory.interact(id);
                Sleep.sleep(ReactionGenerator.getQuick());
            }
            Sleep.sleepUntil(() -> !Inventory.contains(x -> idsToEquip.contains(x.getID())), 1200);

            return sleep();
        }

        if (inventoryLoadout == null || inventoryLoadout.isFulfilled()) {
            setResponse(Response.SUCCESS);
            return sleep();
        }

        List<InventoryLoadoutItem> missingInventoryItems = inventoryLoadout.getMissingItems();
        // check for missing items you dont own and need to buy
        List<LoadoutItem> itemsToBeBrought = missingInventoryItems.stream()
                .filter(x -> x.ownedCount(true) < x.getMin())
                .collect(Collectors.toList());
        if (!itemsToBeBrought.isEmpty()) {
            buyItems(itemsToBeBrought);
            return sleep();
        }

        // if strict and has items shouldnt, deposit all
        List<Item> strictItems = inventoryLoadout.getStrictItems();
        if (!strictItems.isEmpty()) {
            new BankAllInventoryEvent()
                    .setInterruptCondition(getBreakCondition())
                    .execute();
            return ReactionGenerator.getNormal();
        }

        List<LoadoutItem> withdrawList = missingInventoryItems.stream().map(x -> (LoadoutItem) x).collect(Collectors.toList());
        // if owns noted variant of a require item, unnote
        // todo if i need to add notes to a loadout this might break it
        boolean hasANotedVariant = withdrawList.stream().anyMatch(x -> Inventory.contains(x.getItemName()));
        if (hasANotedVariant) {
            Logger.info("Bank all because of a noted variant");
            new BankAllInventoryEvent()
                    .setInterruptCondition(getBreakCondition())
                    .execute();
            return sleep();
        }

        List<Item> a = Equipment.all(x -> withdrawList.stream().anyMatch(e -> x.getID() == e.getBaseID()));
        if (unequipItems && a != null && !a.isEmpty()) {
            Logger.info("unequipping");
            a.forEach(e -> Equipment.unequip(EquipmentSlot.forSlotId(e.getSlot())));
            return sleep();
        }

        // withdraw items you need from bank
        Logger.info("Withdraw items event: " + new WithdrawItemsFromBankEvent(withdrawList)
                .setStrict(strict)
                .setBreakCondition(getBreakCondition())
                .executed());


        return sleep();
    }

    private void buyItems(List<LoadoutItem> itemsToBuy) {
        BuyItemsEvent.Response response = new BuyItemsEvent(itemsToBuy)
                .setBreakCondition(getBreakCondition())
                .executed();
        Logger.info("BuyItemsEvent " + response);
        if (response == BuyItemsEvent.Response.NO_GP && muleRequiredGP) {
            int ttlPrice = (int) (itemsToBuy.stream().mapToInt(x -> x.getBuyPrice() * x.getRefill()).sum() * 1.2);
            Logger.info("Requesting gp for this loadout: " + ttlPrice + " " + Arrays.toString(itemsToBuy.toArray()));

            new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                    .addRequiredItem(ItemID.COINS_995, ttlPrice)
                    .execute();
        } else if (response == BuyItemsEvent.Response.NO_GP) {
            setResponse(Response.NOT_ENOUGH_GP);
        }
    }
}
