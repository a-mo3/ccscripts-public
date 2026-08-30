package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.AlchItem;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Alching extends Fractal {
    public Alching(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.NATURE_RUNE, 1, ScriptSettings.getSettingsData().natureRuneCount)
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE);
    }

    // counter for what item from the settings you are trying to byu
    int counter = 0;

    @Override
    public int onLoop() {
        AlchItem[] alchItems = ScriptSettings.getSettingsData().alchItems;
        if (!Bank.isCached()) {
            if (Bank.isOpen()) {
                Bank.close();
                return ReactionGenerator.getNormal();
            }
            if (Walking.shouldWalk()) Bank.open(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getNormal();
        }

        // withdraw all alchables and coins
        List<Item> inBankAlchables = Bank.all().stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getID() == ItemID.COINS_995 || Arrays.stream(alchItems).anyMatch(i -> i.itemID == x.getID()))
                .collect(Collectors.toList());
        if (!inBankAlchables.isEmpty() && Inventory.emptySlotCount() > 10) {
            Logger.info("Getting alchables out of banks " + inBankAlchables);
            if (!Bank.isOpen()) {
                Bank.open();
                return ReactionGenerator.getNormal();
            }

            if (Bank.getWithdrawMode() != BankMode.NOTE) {
                Bank.setWithdrawMode(BankMode.NOTE);
                return ReactionGenerator.getNormal();
            }

            inBankAlchables.forEach(x -> {
                Bank.withdrawAll(x.getID());
                Sleep.sleep(ReactionGenerator.getQuick());
            });
            return ReactionGenerator.getNormal();
        }

        // alch all alchables in inventory
        Item alchable = Inventory.get(x -> Arrays.stream(alchItems).anyMatch(i -> i.itemID == x.getUnnotedItemID()));
        if (alchable != null) {
            Logger.info("Alching " + alchable);
            if (Widgets.isOpen()) {
                Widgets.closeAll();
            }
            Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, alchable);
            return 2600;
        }


        // todo check if you have enough gp
        if (OwnedItems.count(ItemID.COINS_995) < ScriptSettings.getSettingsData().moneyLeftAfterMuling) {
            if (GrandExchange.getItems() == null || GrandExchange.getItems().length == 0) {
                Logger.info("Mule request for more gp");
                new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                        .addRequiredItem(ItemID.COINS_995, ScriptSettings.getSettingsData().moneyLeftAfterMuling - OwnedItems.count(ItemID.COINS_995) + 50_000)
                        .execute();
                return ReactionGenerator.getNormal();
            }
        }

        // buy one of the items
        if (!GrandExchange.isOpen()) {
            GrandExchange.open();
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.isReadyToCollect()) {
            GrandExchange.collect();
            return ReactionGenerator.getNormal();
        }

        if (alchItems.length == 0) {
            Logger.info("No alch items found.");
            return ReactionGenerator.getNormal();
        }

        AlchItem alchItem = alchItems[counter % alchItems.length];
        if (GrandExchange.contains(alchItem.itemID)) {
            GrandExchange.cancelAll();
            return ReactionGenerator.getNormal();
        }

        if (OwnedItems.count(ItemID.COINS_995) < (alchItem.buyPrice * alchItem.buyQuantity)) {
            Logger.info("Not enough money to buy this item " + alchItem);
            boolean canBuyAnything = Arrays.stream(ScriptSettings.getSettingsData().alchItems)
                    .filter(x -> OwnedItems.count(ItemID.COINS_995) > (alchItem.buyPrice * alchItem.buyQuantity))
                    .peek(x -> Logger.info("Can buy " + x.itemID))
                    .findFirst().isPresent();
            if (canBuyAnything) {
                counter++;
                Logger.info("Can buy something");
            } else {
                Logger.info("Cant buy anything getting more money " + alchItem.itemID);
                new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                        .addRequiredItem(ItemID.COINS_995, (alchItem.buyQuantity * alchItem.buyPrice))
                        .execute();
            }
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.buyItem(alchItem.itemID, alchItem.buyQuantity, alchItem.buyPrice)) {
            Logger.info("Made offer, will wait 10 seconds then try next item " + alchItem);
            if (GrandExchange.getItem(alchItem.itemID) != null && Sleep.sleepUntil(() -> GrandExchange.isReadyToCollect(GrandExchange.getItem(alchItem.itemID).getSlot()),
                    10_000)) {
                // brought, just returning again will collect and then begin to alch
                Logger.info("Brought item");
            } else {
                GrandExchange.cancelAll();
                counter++; // increment counter to move to the next alch item in the list
                // todo possibly time this item out, would need to be sure to un timeout all items when everything is timed out, or take a break
                // didnt buy
            }
        }

        return ReactionGenerator.getNormal();
    }
}
