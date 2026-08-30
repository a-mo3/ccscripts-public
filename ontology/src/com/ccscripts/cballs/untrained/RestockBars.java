package com.ccscripts.cballs.untrained;

import com.ccscripts.PaintButton;
import com.ccscripts.cballs.framework.ItemID;
import com.ccscripts.cballs.framework.ScriptNode;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.items.Item;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RestockBars extends ScriptNode {
    @Override
    public boolean isValid() {
        return Bank.isCached() && !Bank.contains(ItemID.STEEL_BAR, ItemID.STEEL_BAR + 1) && !Inventory.contains(ItemID.STEEL_BAR, ItemID.STEEL_BAR + 1);
    }

    @Override
    public int fallBack() {
        if (Bank.contains(ItemID.CANNONBALL)) {
            if (!Bank.isOpen()) {
                log("Open bank");
                if (Walking.shouldWalk()) Bank.open();
                return 600;
            }
            // withdraw all cballs
            Bank.withdrawAll(ItemID.CANNONBALL);
            return 600;
        }

        if (!GrandExchange.isOpen()) {
            log("Open ge");
            if (Walking.shouldWalk()) GrandExchange.open();
            return 600;
        }
        // sell all cballs
        if (Inventory.contains(ItemID.CANNONBALL)) {
            log("Sell cballs");
            GrandExchange.sellItem(ItemID.CANNONBALL, Inventory.count(ItemID.CANNONBALL), LivePrices.get(ItemID.CANNONBALL) - 20);
            return 600;
        }

        // collect ge
        if (GrandExchange.isReadyToCollect()) {
            log("Collect ge");
            GrandExchange.collect();
            return 600;
        }

        if (Arrays.stream(GrandExchange.getItems())
                .filter(Objects::nonNull)
                .anyMatch(x -> x.getItem() != null && ItemID.STEEL_BAR == x.getItem().getId())) {
            log("We have steel bar offer already (col) " + GrandExchange.isReadyToCollect());
            GrandExchange.collect();
            return 600;
        }

        // buy steel bars
        int coinCount = Inventory.count(ItemID.COINS_995) + Bank.count(ItemID.COINS_995);
        int buyAmount = coinCount / LivePrices.get(ItemID.STEEL_BAR);
        log("Buying steel bars");
        GrandExchange.buyItem(
                ItemID.STEEL_BAR,
                buyAmount,
                LivePrices.get(ItemID.STEEL_BAR)
        );
        return 600;
    }

    @Override
    public String getIdentifier() {
        return "";
    }

    @Override
    public String getExpectedNextState() {
        return "penisbutt";
    }

    @Override
    public List<Rectangle> trainingHighlights() {
        return List.of();
    }

    @Override
    protected boolean shouldConfigureReproducers() {
        return false;
    }
}
