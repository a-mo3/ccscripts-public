package org.dreambot.fractals.loadout.events;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadoutItem;
import org.dreambot.fractals.loadout.LoadoutItem;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Accessors(chain = true)
@Unobfuscated
public class WithdrawItemsFromBankEvent extends AbstractResponseEvent<WithdrawItemsFromBankEvent.Response> {
    private final List<LoadoutItem> itemsToWithdraw;
    @Setter
    private boolean strict;

    public WithdrawItemsFromBankEvent(List<LoadoutItem> itemsToWithdraw) {
        this.itemsToWithdraw = itemsToWithdraw;
        itemsToWithdraw.forEach(x -> Logger.info(String.format("To be withdrawn %s max: %d is variant:  %b", x.getItemName(), x.getMax(), x.getVariant() != null)));
    }

    public WithdrawItemsFromBankEvent() {
        this.itemsToWithdraw = new ArrayList<>();
    }

    public WithdrawItemsFromBankEvent addWithdrawItem(int id, int quantity) {
        itemsToWithdraw.add(new InventoryLoadoutItem(id, quantity));
        return this;
    }

    enum Response {
        SUCCESS,
        DOESNT_OWN_ENOUGH,
        IMPOSSIBLE_WITHDRAW,
    }

    @Override
    public int onLoop() {
        if (!Bank.isOpen()) {
            if (Walking.shouldWalk(8)) BankUtil.openClosest();
            return sleep();
        }

        List<LoadoutItem> itemsToDeposit = itemsToWithdraw.stream()
                .filter(x -> Inventory.count(x.getItemId()) > x.getMax())
//                .filter(x -> Inventory.count(x.getItemId()) > x.getMin())
                .collect(Collectors.toList());

        if (strict && Inventory.contains(x -> itemsToWithdraw.stream().noneMatch(i -> i.idMatches(x.getId())))) {
            Logger.info("Strict deposit all");
            new BankAllInventoryEvent()
//                    .setInterruptCondition( todo pass down exit conditions with new lists method
                    .execute();
            return sleep();
        }

        // deposit items you have too many of
        if (!itemsToDeposit.isEmpty()) {
            itemsToDeposit.forEach(x -> Logger.info("Depositing " + x.getItemName()));
            for (LoadoutItem loadoutItem : itemsToDeposit) {
                // todo might have to limit this to 6 interactions
                Bank.deposit(loadoutItem.getItemId(), Inventory.count(loadoutItem.getItemId()) - loadoutItem.getMax());
                Sleep.sleep(ReactionGenerator.getQuick());
            }
            Sleep.sleep(750);
            return sleep();
        }


        List<LoadoutItem> filteredItemsToWithdraw = itemsToWithdraw.stream()
//                .filter(x -> Inventory.count(x.getItemId()) < x.getMax())
                .filter(x -> x.inventoryCount() < x.getMin())
                .filter(x -> Bank.contains(x.getUnnotedBaseID())
                        || (x.getVariant() != null && Bank.contains(x.getVariant().getIds())))
                .collect(Collectors.toList());
        Logger.info("Filtered items to withdraw: " + filteredItemsToWithdraw.size());

        if (!filteredItemsToWithdraw.isEmpty()) {
            filteredItemsToWithdraw.forEach(x -> Logger.info("withdrawing " + x.getItemName()));
            for (LoadoutItem withdraw : filteredItemsToWithdraw) {
                if (Inventory.emptySlotCount() < 1) {
                    Logger.info("Banking all inv full");
                    new BankAllInventoryEvent()
//                            .setInterruptCondition(getBreakCondition()) todo pass down exit conditions
                            .execute();
                }
                if (withdraw.ownedCount(true) < withdraw.getMin()) {
                    Logger.info("doesnt own enough " + withdraw.getItemName() + " " + withdraw.getItemId());
                    setResponse(Response.DOESNT_OWN_ENOUGH);
                    return sleep();
                }
                // todo might have to limit this to 6 interactions
                int quantity = withdraw.getMax() - withdraw.inventoryCount();
                Logger.info("Withdrawing " + quantity + " * " + withdraw.getItemName());
//                if (!(new Item(withdraw.getItemId(), 1).isStackable()) && quantity > 28) {
//                    setResponse(Response.IMPOSSIBLE_WITHDRAW);
//                    return sleep();
//                }
                if (withdraw.getVariant() != null) {
                    Item inBank = Bank.get(x -> Arrays.stream(withdraw.getVariant().getIds()).anyMatch(i -> i == x.getId()));
                    if (inBank == null) {
                        Logger.warn("inBank variant withdraw null");
                        return sleep();
                    }
                    Logger.info("Variant withdraw " + inBank);
                    Bank.withdraw(inBank.getId(), quantity);
                } else {
                    if (new Item(withdraw.getItemId(), 0).isNoted()) {
                        Bank.setWithdrawMode(BankMode.NOTE);
                    } else {
                        Bank.setWithdrawMode(BankMode.ITEM);
                    }
                    Logger.info("normal withdraw " + withdraw);
                    Bank.withdraw(withdraw.getUnnotedBaseID(), quantity);
                }
                // todo might need sleep until here
                Sleep.sleep(ReactionGenerator.getQuick());
            }
            Sleep.sleep(750);
            return sleep();
        }

        // variant handle for when you have a half potion but need to withdraw full potion

        setResponse(Response.SUCCESS);
        return sleep();
    }
}
