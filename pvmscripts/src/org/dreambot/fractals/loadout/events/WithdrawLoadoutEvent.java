package org.dreambot.fractals.loadout.events;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.BankAllEquipmentEvent;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.loadout.*;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
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
    public static int[] sellList = new int[0];

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
//                .forEach(x -> Logger.info(String.format("%s -%d * %d", x.getName(), x.getId(), x.getAmount())));
//        Logger.info("Inventory state");
//        Inventory.all()
//                .stream()
//                .filter(Objects::nonNull)
//                .forEach(x -> Logger.info(String.format("%s -%d * %d", x.getName(), x.getId(), x.getAmount())));
//        Logger.info("Equipment state");
//        Equipment.all()
//                .stream()
//                .filter(Objects::nonNull)
//                .forEach(x -> Logger.info(String.format("%s -%d * %d", x.getName(), x.getId(), x.getAmount())));
    }

    public enum Response {
        SUCCESS,
        NOT_ENOUGH_GP,
        UNTRADABLE,
        DC_CHECK,
        UNEQUIPPABLE,
    }

    /**
     * unequippable empty items that are brought in an inventory event but the event need to break to handle the fractal
     * that charges them
     */
    final List<Integer> unequippables = Arrays.asList(
            ItemID.ROSEWOOD_BLOWPIPE_EMPTY,
            ItemID.TOXIC_BLOWPIPE_EMPTY,
            ItemID.TOME_OF_EARTH_EMPTY,
            ItemID.SANGUINESTI_STAFF_UNCHARGED,
            ItemID.TOXIC_STAFF_UNCHARGED
    );

    @Override
    public int onLoop() {
        Logger.info("WLE");
        // ensure bank cache
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            Logger.info("Get bank cache WLE");
            if (Bank.isOpen()) Bank.updateCache();
            if (Walking.shouldWalk()) BankUtil.openClosest();
            return sleep();
        }

        if (equipmentLoadout != null && !equipmentLoadout.isFulfilled()) {
            if (equipmentLoadout.isStrict() && !equipmentLoadout.getStrictItems().isEmpty()) {
                Logger.info("Strict equipment loadout enforcement");
                new BankAllEquipmentEvent().execute();
            }

            // do equipment loadout
            Logger.info("Doing equipment " + Client.getGameStateID());
            List<EquipmentLoadoutItem> missingItems = equipmentLoadout.getMissingItems();
            List<LoadoutItem> loadoutItems = missingItems.stream().map(x -> (LoadoutItem) x).collect(Collectors.toList());

            LoadoutMapEntry i = equipmentLoadout.getLoadoutList()
                    .stream()
                    .filter(x -> Client.isMembers() || !new Item(x.getItem().getItemId(), 1).isMembersOnly())
                    .filter(x -> x.getSlot() == EquipmentSlot.ARROWS || x.getSlot() == EquipmentSlot.WEAPON)
                    .filter(x -> x.getItem().getEnabledCondition() == null || x.getItem().getEnabledCondition().get())
                    .filter(x -> Equipment.count(x.getItem().getItemId()) > x.getItem().getMax()
                            || Equipment.count(x.getItem().getItemId()) < x.getItem().getMin()
                    )
                    .findFirst()
                    .orElse(null);
            if (i != null && (Equipment.count(i.getItem().getItemId()) > i.getItem().getMax() || Equipment.count(i.getItem().getItemId()) < i.getItem().getMin())) {
                Logger.info("Too much or little ammo");
                new BankAllEquipmentEvent().execute();
            }

            if (Inventory.contains(Item::isNoted)) {
                Logger.info("Banking all for noted equipment item");
                new BankAllInventoryEvent()
//                        .setInterruptCondition(getBreakCondition())
                        .execute();
            }

            missingItems.forEach(x -> Logger.info("Missing " + x.getItemName()));
            if (Dialogues.areOptionsAvailable()
                    && Arrays.stream(Dialogues.getOptions()).anyMatch(x -> x.contains("PK skull."))) {
                // items like avarice that skull you require accepting it
                Dialog.solve("PK skull");
            }

            List<LoadoutItem> itemsToBuy = loadoutItems.stream()
                    .filter(x -> Client.isMembers() || !new Item(x.getItemId(), 1).isMembersOnly())
                    .filter(x -> !Equipment.contains(x.getItemId()))
                    .filter(x -> OwnedItems.count(x.getItemId(), true) < x.getMin())
                    .collect(Collectors.toList());
            if (!itemsToBuy.isEmpty()) {
                Logger.info("Equipment buy items");
                buyItems(itemsToBuy);
                return sleep();
            }

            List<LoadoutItem> itemsToWithdraw = loadoutItems.stream()
                    .filter(x -> Client.isMembers() || !new Item(x.getItemId(), 1).isMembersOnly())
                    .filter(x -> !Equipment.contains(x.getItemId()))
                    .filter(x -> !Inventory.contains(x.getItemId()))
                    .filter(x -> OwnedItems.contains(x.getItemId()))
                    .collect(Collectors.toList());
            if (!itemsToWithdraw.isEmpty()) {
                itemsToWithdraw.forEach(x -> Logger.info(x.getItemName()));
                Logger.info("Equipment withdrawal: " + new WithdrawItemsFromBankEvent(itemsToWithdraw)
//                        .setStrict(true)
//                                .setBreakCondition(getBreakCondition())
                                .executed()
                );
                return sleep();
            }

            // equip
            List<Integer> idsToEquip = loadoutItems.stream()
                    .map(LoadoutItem::getItemId)
                    .filter(x -> !Equipment.contains(x))
                    .filter(x -> Client.isMembers() || !new Item(x, 1).isMembersOnly())
                    .filter(Inventory::contains)
                    .filter(OwnedItems::contains)
                    .collect(Collectors.toList());

            if (Bank.isOpen() || GrandExchange.isOpen()) {
                Widgets.closeAll();
                Antiban.sleepUntil(() -> !Bank.isOpen(), 1000);
            }

            for (Integer id : idsToEquip) {
                if (unequippables.contains(id)) {
                    Logger.info("This item cant be equipped " + id);
                    setResponse(Response.UNEQUIPPABLE);
                    return sleep();

                }
                Logger.info("Equip " + id);
                Inventory.interact(id);
                Sleep.sleep(ReactionGenerator.getQuick());
            }

            Antiban.sleepUntil(() -> !Inventory.contains(x -> idsToEquip.contains(x.getId())), 1200);

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
        Logger.info("Item to be brought: " + itemsToBeBrought.size());
        if (!itemsToBeBrought.isEmpty()) {
            itemsToBeBrought.forEach(x -> Logger.info("Item to be brought: " + x.getItemName()));
            buyItems(itemsToBeBrought);
            return sleep();
        }

        // if strict and has items shouldnt, deposit all
        List<Item> strictItems = inventoryLoadout.getStrictItems();
        if (!strictItems.isEmpty()) {
            Logger.info("Strict items deposit all");
            new BankAllInventoryEvent()
//                    .setInterruptCondition(getBreakCondition())
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
//                    .setInterruptCondition(getBreakCondition())
                    .execute();
            return sleep();
        }

        List<Item> a = Equipment.all(x -> withdrawList.stream().anyMatch(e -> x.getId() == e.getBaseID()));
        if (unequipItems && !a.isEmpty()) {
            Logger.info("unequipping");
            a.forEach(e -> Equipment.unequip(EquipmentSlot.forSlotId(e.getSlot())));
            return sleep();
        }

        // withdraw items you need from bank
        Logger.info("Withdraw items event: " + new WithdrawItemsFromBankEvent(withdrawList)
                .setStrict(strict)
//                .setBreakCondition(getBreakCondition())
                .executed());
        return sleep();
    }

    private void buyItems(List<LoadoutItem> itemsToBuy) {
        BuyItemsEvent.Response response = new BuyItemsEvent(itemsToBuy)
//                .setBreakCondition(getBreakCondition())
                .executed();
        Logger.info("BuyItemsEvent " + response);
        if (response == BuyItemsEvent.Response.NO_GP && muleRequiredGP) {
            int ttlPrice = (int) (itemsToBuy.stream()
                    .filter(x -> x.getEnabledCondition() == null || x.getEnabledCondition().get())
                    .mapToInt(x -> (x.getBuyPrice() + 1) * x.getRefill())
                    .sum() * 1.2
            );
            Logger.info("Requesting gp for this loadout: " + ttlPrice + " " + Arrays.toString(itemsToBuy.toArray()));
            itemsToBuy.forEach(x -> Logger.info("Item " + x.getItemName() + " Cost: " + x.getBuyPrice() * x.getRefill()));

            if (OwnedItems.containsAnyUnworn(sellList)) {
                Logger.info("Selling all items in global sell list");
                new SellAllEvent(sellList)
                        .execute();
                return;
            }

            new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                    .addRequiredItem(ItemID.COINS_995, ttlPrice)
                    .execute();
        } else if (response == BuyItemsEvent.Response.NO_GP) {
            setResponse(Response.NOT_ENOUGH_GP);
        } else if (response == BuyItemsEvent.Response.BUYING_AN_UNTRADEABLE) setResponse(Response.UNTRADABLE);
    }

    @Override
    public Response executed() {
        // log all skills to debug the one disconnect timing thing
        Logger.info("Loadout event fire debug");
        Arrays.stream(Skill.values()).forEach(x -> Logger.log(x.getName() + " " + x.getBoostedLevel() + "/" + x.getLevel()));
        Logger.info(Client.getGameState() + " " + Client.getGameStateID());
        if (Arrays.stream(Skill.values()).anyMatch(x -> x.getLevel() == 0) || Arrays.stream(Skill.values()).allMatch(x -> x.getBoostedLevel() == 0)) {
            Logger.info("Skill matched 0 when firing event, cancel");
            return Response.DC_CHECK;
        }

        return super.executed();
    }
}
