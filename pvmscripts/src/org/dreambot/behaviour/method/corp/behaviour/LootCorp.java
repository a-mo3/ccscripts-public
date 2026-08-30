package org.dreambot.behaviour.method.corp.behaviour;

import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.corp.CorpClient;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

public class LootCorp extends TickDecision {
    @Override
    public boolean evaluate() {
        log("Looting corp");
        // drop inventory cheapest
        Item cheapestInvItem = PVMUtil.getCheapest();
        if (cheapestInvItem == null) return false;
        // pick up loot
        GroundItem loot = GroundItems.closest(x -> x.canReach() && x.getItem().getId() != ItemID.RUBY_BOLTS_E
                && (x.getItem().getLivePrice() * (x.getItem().isStackable() ? x.getAmount() : 1)) > LivePrices.get(cheapestInvItem));
        if (loot != null) {
            log("Looting off ground " + loot.getName());
            loot.interact();
            // corp should be dead here otherwise this wouldnt decision fire
            CorpClient.recordCorpDeath();
        }
        return false;
    }
}
