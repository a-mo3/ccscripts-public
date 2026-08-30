package org.dreambot.fractals.loadout.events;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.GrandExchangeItem;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.LoadoutItem;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Unobfuscated
public class BuyItemsEvent extends AbstractResponseEvent<BuyItemsEvent.Response> {
    private final List<LoadoutItem> itemsToBuy;

    public BuyItemsEvent(List<LoadoutItem> itemsToBuy) {
        this.itemsToBuy = itemsToBuy;
        Logger.info("Buying items");
        itemsToBuy.forEach(x -> Logger.info(x.getItemName() + " * " + x.getRefill()));
    }

    HashMap<Integer, Long> timeStampMap = new HashMap<>();
    HashMap<Integer, Integer> priceMap = new HashMap<>();

    enum Response {
        SUCCESS,
        NO_GP
    }

    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            Logger.info("Inventory was full, depositing all");
            new BankAllInventoryEvent()
                    .setInterruptCondition(getBreakCondition())
                    .execute();
            return sleep();
        }

        if (!Inventory.contains(ItemID.COINS_995) || Bank.contains(ItemID.COINS_995)) {
            if (Inventory.emptySlotCount() < 24) {
                new BankAllInventoryEvent()
                        .setInterruptCondition(getBreakCondition())
                        .execute();
            }

            int required = itemsToBuy.stream().mapToInt(LoadoutItem::getBuyPrice).sum();
            Logger.info(String.format("Requires %d owns %d", required, OwnedItems.count(ItemID.COINS_995)));
            if (OwnedItems.count(ItemID.COINS_995) < required) {
                Logger.info("not enough");
                setResponse(Response.NO_GP);
                return ReactionGenerator.getNormal();
            }

            Logger.info("BuyItem - Withdraw event: " + new WithdrawItemsFromBankEvent()
                    .addWithdrawItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995))
                    .setStrict(false)
                    .executed());
            return ReactionGenerator.getNormal();
        }

        if (!GrandExchange.isOpen()) {
            if (Bank.isOpen()) Bank.close();

            GrandExchange.open();
            Sleep.sleepUntil(GrandExchange::isOpen, 2000);
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.isReadyToCollect()) {
            GrandExchange.collect();
            Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), 2000);
            return ReactionGenerator.getNormal();
        }

        // add items not brought in this event
        itemsToBuy.forEach(x -> {
            if (GrandExchange.contains(x.getBaseID()) && !timeStampMap.containsKey(x.getBaseID())) {
                timeStampMap.put(x.getBaseID(), System.currentTimeMillis());
            }
        });
        // check for offers that have existed for too long and need to be rebid
        List<LoadoutItem> rebidItems = itemsToBuy.stream()
                .filter(x -> x.ownedCount(true) < x.getRefill())
                .filter(x -> timeStampMap.containsKey(x.getBaseID())
                        && (System.currentTimeMillis() - timeStampMap.get(x.getBaseID())) > (1000 * 16))
                .filter(x -> GrandExchange.contains(x.getBaseID()))
                .collect(Collectors.toList());

        Logger.info("Rebids " + rebidItems.size());
        timeStampMap.forEach((x, y) -> Logger.info(String.format("%d offered %d ago", x, System.currentTimeMillis() - y)));
        if (!rebidItems.isEmpty()) {
            rebidItems.forEach(x -> Logger.info("Rebidding " + x.getItemName()));
            for (LoadoutItem rebid : rebidItems) {
                Logger.info("Rebidding " + rebid.getItemName());
                GrandExchangeItem geItem = Arrays.stream(GrandExchange.getItems())
                        .filter(s -> s != null && s.getItem() != null && s.getItem().getID() == rebid.getBaseID())
                        .findFirst().orElse(null);
                Logger.info("ge item: " + geItem);
                if (geItem != null) {
                    GrandExchange.cancelOffer(geItem.getSlot());
                    Logger.info("Increasing item price");
//                    rebid.setBuyPrice((int) (rebid.getBuyPrice() * rebid.getPriceIncreases()) + 1);
                    if (rebid.getBuyPrice() < rebid.getMaxPrice()) {
                        if (priceMap.containsKey(rebid.getBaseID())) {
                            if (priceMap.get(rebid.getBaseID() )< 10_000) {
                                priceMap.put(rebid.getBaseID(), (int) (priceMap.get(rebid.getBaseID()) * 1.3) + 1);
                            } else {
                                priceMap.put(rebid.getBaseID(), (int) (priceMap.get(rebid.getBaseID()) * rebid.getPriceIncrease()) + 1);
                            }
                        } else {
                            // i dont think this would be possible but ill leave it here, maybe is script is started with offers already made?
                            priceMap.put(rebid.getBaseID(), (int) (rebid.getBuyPrice() * rebid.getPriceIncrease() + 1));
                        }
                    } else {
                        Logger.info("Item is at its max price " + rebid.getItemName() + " " + rebid.getMaxPrice());
                    }
                }
//                Sleep.sleepUntil(() -> !GrandExchange.contains(rebid.getBaseID()), 2400);
                Sleep.sleep(400);
            }
            return sleep();
        }

        // make bids
        List<LoadoutItem> itemsToBidOn = itemsToBuy.stream()
                .filter(x -> x.ownedCount(true) < x.getMax())
                .collect(Collectors.toList());

        if (itemsToBidOn.isEmpty()) {
            Logger.info("No items to big on");
            setResponse(Response.SUCCESS);
            return sleep();
        }

        for (LoadoutItem bidItem : itemsToBidOn) {
            if (GrandExchange.getOpenSlots() <= 0) {
                Logger.info("GE full waiting for order to be filled, will raise prices soon dont you worry princess.");
                return sleep();
            }

            if (!GrandExchange.contains(bidItem.getBaseID())) {
                int price = Math.max(bidItem.getBuyPrice(), priceMap.getOrDefault(bidItem.getBaseID(), 0)) * bidItem.getRefill();
                if (price > OwnedItems.count(ItemID.COINS_995)) {
                    Logger.info("Not enough coins");
                    setResponse(Response.NO_GP);
                    return sleep();
                }

                if (!priceMap.containsKey(bidItem.getBaseID()))
                    priceMap.put(bidItem.getBaseID(), bidItem.getBuyPrice());
                GrandExchange.buyItem(bidItem.getBaseID(), bidItem.getRefill(), priceMap.get(bidItem.getBaseID()));
                timeStampMap.put(bidItem.getBaseID(), System.currentTimeMillis());
                Sleep.sleepUntil(() -> GrandExchange.contains(bidItem.getBaseID()), 1000);
            }
        }

        return sleep();
    }
}
