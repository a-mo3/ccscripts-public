package org.dreambot.fractals.loadout;

import lombok.Setter;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.settings.timing.ReactionGenerator;

public class LoadoutExecutor {
    @Setter
    public static boolean buyRemainder = true;

    static Area GRAND_EXCHANGE = BankLocation.GRAND_EXCHANGE.getArea(5);

    /**
     * should withdraw all items required & buy if buyRemainder is true
     *
     * @param loadout an inventory loadout you wish to equip
     * @return int sleep
     */
    public static int execInvLoadout(InventoryLoadout loadout) {
        if (loadout.isFulfilled()) {
            Logger.info("Loadout is already fulfilled");
            return ReactionGenerator.getNormal();
        }


        if (Bank.getLastBankHistoryCacheTime() < 1) {
            Logger.info("No bank cache, opening the bank.");
            if (Bank.open()) Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isFull()) {
            Logger.info("Inv is full");
            if (GrandExchange.isOpen()) {
                Widgets.closeAll();
                return ReactionGenerator.getNormal();
            }

            if (!Bank.isOpen()) {
                Logger.info("Inv full bank open");
                Bank.open();
                return ReactionGenerator.getNormal();
            }

            Logger.info("inv full deposit inv");
            Bank.depositAllItems();
            return ReactionGenerator.getNormal();
        }

        if (loadout.isStrict()) {
            Item strictItem = loadout.getStrictItem(true);
            Logger.info("strictItem: " + strictItem);
            if (strictItem != null && strictItem.getID() != ItemID.COINS_995) {
                Logger.info("Found strict item " + strictItem.getID());
                strictDeposit(loadout);
                return ReactionGenerator.getNormal();
            }
        }


        InventoryLoadoutItem missingItem = loadout.getMissingItem();
        if (missingItem == null) {
            // if missing item is null that means the loadout is strict and we have just other items left
            return strictDeposit(loadout);
        }

        // noted item check
        if (Inventory.contains(missingItem.getItemId() + 1)) {
            if (!Bank.isOpen()) {
                Logger.info("Depositing noted items bank open");
                Bank.open();
                return ReactionGenerator.getNormal();
            }

            Logger.info("Depositing noted items ");
            Bank.depositAllItems();
            return ReactionGenerator.getNormal();
        }

        Logger.info(String.format("Found missing item %s", missingItem.getItemId()));
        int invCount = Inventory.count(missingItem.getItemId());
        if (invCount > missingItem.getMax()) {
            Logger.info("over max depositing remainder");
            return depositRemainder(missingItem);
        }

        // idk how bank cache works on inubot so this might break
        if (Bank.count(missingItem.getItemId()) + invCount < missingItem.getMax()) {
            Logger.info(String.format("Owns less than max inv: %d bank: %d max: %d", invCount, Bank.count(missingItem.getItemId()), missingItem.getMax()));
            return buyItem(missingItem);
        }

        return withdrawItem(missingItem);
    }

    public static int execEquipmentLoadout(EquipmentLoadout loadout) {
        if (loadout.isFulfilled()) {
            Logger.info("Equipment loadout is already fulfilled");
            return ReactionGenerator.getNormal();
        }

//        InterfaceComponent geWarningYes = GrandExchange.getGeWarningYes();
//        if (geWarningYes != null && geWarningYes.isVisible()) {
//            geWarningYes.interact("Yes");
//            return ReactionGenerator.getNormal();
//        }

        if (Bank.getLastBankHistoryCacheTime() < 1) {
            Logger.info("No bank cache, opening the bank.");
            if (Bank.open()) Bank.close();
            return ReactionGenerator.getNormal();
        }


        EquipmentLoadoutItem missingItem = loadout.getMissingItem();
        if (missingItem == null) {
            Logger.warn("Missing item was null equipment loadout");
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(missingItem.getItemId())) {
            Logger.info("Attempting to equip item");
            if (GrandExchange.isOpen() || Bank.isOpen()) {
                Widgets.closeAll();
                return ReactionGenerator.getNormal();
            }

            Inventory.interact(x -> x.hasAction("Wear", "Equip", "Wield") && missingItem.getItemId() == x.getID());
            return ReactionGenerator.getNormal();
        }

        if (Bank.contains(missingItem.getItemId())) {
            return withdrawItem(missingItem);
        }
        return buyItem(missingItem);
    }

    private static int strictDeposit(InventoryLoadout loadout) {
        if (!Bank.isOpen()) {
            Logger.info("Strict deposit bank open");
            Bank.open();
            return ReactionGenerator.getNormal();
        }

        Item strictItem = loadout.getStrictItem();
        if (strictItem == null) {
            Logger.info("strict item was null something has gone horribly wrong");
            return ReactionGenerator.getNormal();
        }

        Logger.info("Strict deposit " + strictItem.getName());

        Bank.depositAll(strictItem.getID());
        return ReactionGenerator.getNormal();
    }

    private static int depositRemainder(LoadoutItem missingItem) {
        if (!Bank.isOpen()) {
            Logger.info("deposit remainder bank open");
            Bank.open();
            return ReactionGenerator.getNormal();
        }

        int invCount = Inventory.count(missingItem.getItemId());
        Bank.deposit(missingItem.getItemId(), invCount - missingItem.getMax());
        return ReactionGenerator.getNormal();
    }

    private static int buyItem(LoadoutItem missingItem) {
        if (!GRAND_EXCHANGE.contains(Players.getLocal())) {
            Logger.info("Walking to ge");
            if (Walking.shouldWalk()) Walking.walk(GRAND_EXCHANGE.getRandomTile());
            return ReactionGenerator.getNormal();
        }

        int coinsInInv = Inventory.count(ItemID.COINS_995);
        if (Bank.contains(ItemID.COINS_995) || Bank.getLastBankHistoryCacheTime() < 0 || coinsInInv == 0) {
            Logger.info("Withdrawing coins from bank has item: " + Bank.contains(missingItem.getItemId())
                    + " invCount/buyPrice: " + coinsInInv + " " + missingItem.getBuyPrice());

            if (!Bank.isOpen()) {
                Logger.info("Opening bank - buy item");
                Bank.open();
                return ReactionGenerator.getNormal();
            }

            if (Inventory.isFull()) {
                Bank.depositAllItems();
                return ReactionGenerator.getNormal();
            }

//
            Logger.info("Withdrawing all coins");
            Bank.withdrawAll(ItemID.COINS_995);
            return ReactionGenerator.getNormal();
        }

        if (!GrandExchange.isOpen()) {
            Logger.info("Opening grand exchange");
            GrandExchange.open();
            return ReactionGenerator.getNormal();
        }

        // check if a transaction with this item is already in get
        if (GrandExchange.contains(missingItem.getItemId())) {
            // sleep until its read to collection
            if (GrandExchange.isReadyToCollect()) {
                GrandExchange.collect();
                return ReactionGenerator.getNormal();
            }

            Sleep.sleepUntil(GrandExchange::isReadyToCollect, 64000);
            return ReactionGenerator.getNormal();
        }

        // todo impl price increasing
        // if its not, make it

//        Logger.info(String.format("Making offer on item: %d, Price: %d, refill: %d", missingItem.getItemId(), missingItem.getBuyPrice(), missingItem.getRefill()));
//        StockMarket.createOffer(StockMarketable.Type.BUY)
//                .item(missingItem.getItemId())
//                .quantity(missingItem.getRefill())
//                .price(i -> missingItem.getBuyPrice())
//                .build()
//                .createAndCollectTransaction(missingItem.getRefill() < 28 ? StockMarketTransaction.CollectionAction.ITEM : StockMarketTransaction.CollectionAction.NOTE);
        GrandExchange.buyItem(missingItem.getItemId(), missingItem.getRefill(), (int) (missingItem.getBuyPrice() * 1.5));

        return ReactionGenerator.getNormal();
    }

    private static int withdrawItem(LoadoutItem item) {
        if (!Bank.isOpen()) {
            Logger.info("withdraw item bank open");
            Bank.open(); // todo test if this is open closest or not
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isFull()) {
            Bank.depositAllItems();
            return ReactionGenerator.getNormal();
        }

        Logger.info("Bank withdraw " + item.getItemId() + " " + item.getMax());
        if (!Bank.contains(item.getItemId())) {
            Logger.warn("Bank withdraw no has item force bank cache update");
        }

        if (item.getMax() < 28) Bank.setWithdrawMode(BankMode.ITEM);
        Bank.withdraw(item.getItemId(), item.getMax());
        Sleep.sleepUntil(() -> Inventory.count(item.itemId) == item.getMax(), 2400);
        return ReactionGenerator.getNormal();
    }
}
