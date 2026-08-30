package org.dreambot.fractals.loadout.events;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.GrandExchangeItem;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebPathQuery;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebPathResponse;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.LoadoutItem;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
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

            if (Bank.isCached() && !OwnedItems.contains(ItemID.COINS_995)) {
                Logger.info("You literally have 0 coins");
                new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                        .addRequiredItem(ItemID.COINS_995, 100_000)
                        .setInterruptCondition(getBreakCondition())
                        .execute();
            }

            Logger.info("BuyItem - Withdraw event: " + new WithdrawItemsFromBankEvent()
                    .addWithdrawItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995))
                    .setStrict(false)
                    .setBreakCondition(getBreakCondition())
                    .executed());
            return ReactionGenerator.getNormal();
        }

        if (!GrandExchange.isOpen()) {
            if (Bank.isOpen()) Bank.close();
            WebPathResponse r = WebPathQuery.builder()
                    .useBankCache(true)
                    .from(Players.getLocal().getTile())
                    .to(BankLocation.GRAND_EXCHANGE.getTile())
                    .build()
                    .calculate();

            Logger.info("Path to GE with teleports");
            Logger.info(r.getRequiredItems());

            InventoryLoadout l = new InventoryLoadout();
            r.getRequiredItems().forEach(i -> {
                Logger.info("Adding " + i.getName());
                if (!Inventory.contains(i.getName()) && Bank.contains(i.getName())) {
                    l.addItem(Bank.get(i.getName()).getID(), i.getCount());
                }
            });

            if (!l.isFulfilled()) new WithdrawLoadoutEvent(l, null).executed();

            GrandExchange.open();
            Sleep.sleepUntil(GrandExchange::isOpen, 2000 + ReactionGenerator.getNormal());
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.isReadyToCollect()) {
            GrandExchange.collect();
            Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), 2000);
            return ReactionGenerator.getNormal();
        }

        // add items not brought in this event
        itemsToBuy.forEach(x -> {
            if (GrandExchange.contains(x.getUnnotedBaseID()) && !timeStampMap.containsKey(x.getUnnotedBaseID())) {
                timeStampMap.put(x.getUnnotedBaseID(), System.currentTimeMillis());
            }
        });
        // check for offers that have existed for too long and need to be rebid
        List<LoadoutItem> rebidItems = itemsToBuy.stream()
                .filter(x -> x.ownedCount(true) < x.getRefill())
                .filter(x -> timeStampMap.containsKey(x.getUnnotedBaseID())
                        && (System.currentTimeMillis() - timeStampMap.get(x.getUnnotedBaseID())) > (1000 * 16))
                // allow buying of noted items as unnoted
                .filter(x -> GrandExchange.contains(x.getUnnotedBaseID()))
                .collect(Collectors.toList());

        Logger.info("Rebids " + rebidItems.size());
        timeStampMap.forEach((x, y) -> Logger.info(String.format("%d offered %d ago", x, System.currentTimeMillis() - y)));
        if (!rebidItems.isEmpty()) {
            rebidItems.forEach(x -> Logger.info("Rebidding " + x.getItemName()));
            for (LoadoutItem rebid : rebidItems) {
                Logger.info("Rebidding " + rebid.getItemName());
                GrandExchangeItem geItem = Arrays.stream(GrandExchange.getItems())
                        .filter(s -> s != null && s.getItem() != null && s.getItem().getID() == rebid.getUnnotedBaseID())
                        .findFirst().orElse(null);
                Logger.info("ge item: " + geItem);
                if (geItem != null) {
                    Logger.info("Increasing item price");
//                    rebid.setBuyPrice((int) (rebid.getBuyPrice() * rebid.getPriceIncreases()) + 1);
                    if (rebid.getBuyPrice() < rebid.getMaxPrice()) {
                        GrandExchange.cancelOffer(geItem.getSlot());
                        if (priceMap.containsKey(rebid.getUnnotedBaseID())) {
                            if (priceMap.get(rebid.getUnnotedBaseID()) < 10_000) {
                                priceMap.put(rebid.getUnnotedBaseID(), (int) (priceMap.get(rebid.getUnnotedBaseID()) * 1.3) + 1);
                            } else {
                                priceMap.put(rebid.getUnnotedBaseID(), (int) (priceMap.get(rebid.getUnnotedBaseID()) * rebid.getPriceIncrease()) + 1);
                            }
                        } else {
                            // i dont think this would be possible but ill leave it here, maybe is script is started with offers already made?
                            priceMap.put(rebid.getUnnotedBaseID(), (int) (rebid.getBuyPrice() * rebid.getPriceIncrease() + 1));
                        }
                    } else {
                        Logger.info("Item is at its max price " + rebid.getItemName() + " " + rebid.getMaxPrice());
                    }
                }
//                Sleep.sleepUntil(() -> !GrandExchange.contains(rebid.getUnnotedBaseID()), 2400);
                Sleep.sleep(400);
            }
            return sleep();
        }

        // make bids
        List<LoadoutItem> itemsToBidOn = itemsToBuy.stream()
                .filter(x -> x.ownedCount(true) < x.getMax())
                .collect(Collectors.toList());

        if (itemsToBidOn.isEmpty()) {
            Logger.info("no items to buy");
            setResponse(Response.SUCCESS);
            return sleep();
        }

        for (LoadoutItem bidItem : itemsToBidOn) {
            if (GrandExchange.getOpenSlots() <= 0) {
                Logger.info("GE full waiting for order to be filled, will raise prices soon dont you worry princess.");
                return sleep();
            }

            if (!GrandExchange.contains(bidItem.getUnnotedBaseID())) {
                int price = Math.max(bidItem.getBuyPrice(), priceMap.getOrDefault(bidItem.getUnnotedBaseID(), 0)) * bidItem.getRefill();
                if (price > OwnedItems.count(ItemID.COINS_995)) {
                    Logger.info("Not enough coins");
                    setResponse(Response.NO_GP);
                    return sleep();
                }

                if (!priceMap.containsKey(bidItem.getUnnotedBaseID()))
                    priceMap.put(bidItem.getUnnotedBaseID(), bidItem.getBuyPrice());
                GrandExchange.buyItem(bidItem.getUnnotedBaseID(), bidItem.getRefill(), priceMap.get(bidItem.getUnnotedBaseID()));
                timeStampMap.put(bidItem.getUnnotedBaseID(), System.currentTimeMillis());
                Sleep.sleepUntil(() -> GrandExchange.contains(bidItem.getUnnotedBaseID()), 1000);
            }
        }

        return sleep();
    }
}
