package org.dreambot.muling.impl;

import lombok.Getter;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.OfferedItem;
import org.dreambot.muling.RequiredItem;
import org.dreambot.muling.messages.client.MuleRequestMessage;

/**
 * just a mule request message with a time to live
 */

public class TimeoutMuleRequest {
    private static final int TIME_TO_LIVE = 5 * 60 * 1000;
    @Getter
    private MuleRequestMessage requestMessage;
    Timer timer;

    public TimeoutMuleRequest(MuleRequestMessage requestMessage) {
        if (requestMessage == null) return;
        this.requestMessage = requestMessage;
        timer = new Timer(TIME_TO_LIVE);
    }

    public boolean isExpired() {
        return timer.finished();
    }

    /**
     * @return if offer (their items) match whats in the request
     */
    public boolean offerMatches() {
        for (OfferedItem offeredItem : requestMessage.offeredItems) {
            Item i = Trade.getItem(false, offeredItem.getItemId());
            if (i == null || i.getAmount() != offeredItem.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return if we have offered all the items
     */
    public boolean hasFulfilled() {
        for (RequiredItem requiredItem : requestMessage.requiredItems) {
            Item i = Trade.getItem(true, requiredItem.getItemId());
            if (i == null || i.getAmount() != requiredItem.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return if we have offered all the items, but for request side
     */
    public boolean requestSideHasFulfilled() {
        for (OfferedItem offeredItem : requestMessage.offeredItems) {
            Item i = Trade.getItem(true, offeredItem.getItemId());
            if (i == null || i.getAmount() != offeredItem.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    public RequiredItem getNextRequiredItem() {
        for (RequiredItem requiredItem : requestMessage.requiredItems) {
            Item i = Trade.getItem(true, requiredItem.getItemId());
            if (i == null || i.getAmount() != requiredItem.getQuantity()) {
                return requiredItem;
            }
        }
        return null;
    }

    public OfferedItem getNextOfferedItem() {
        for (OfferedItem offeredItem : requestMessage.offeredItems) {
            Item i = Trade.getItem(true, offeredItem.getItemId());
            if (i == null || i.getAmount() != offeredItem.getQuantity()) {
                return offeredItem;
            }
        }
        return null;
    }

    /**
     * @return inventory loadout required for fulfilling the loadout
     */
    public InventoryLoadout generateInventoryLoadout() {
        InventoryLoadout loadout = new InventoryLoadout();
        for (RequiredItem req : getRequestMessage().requiredItems) {
            loadout.addItem(req.getItemId(), req.getQuantity(), OwnedItems.count(req.getItemId()));
        }
        return loadout;
    }

    public void finish() {
        MuleState.tradeComplete(true, "accepted", requestMessage.requestId);
        Logger.info("setting run time");
        timer.setRunTime(0);
    }

    public String getTimeToLive() {
        return timer.formatTime();
    }
}
