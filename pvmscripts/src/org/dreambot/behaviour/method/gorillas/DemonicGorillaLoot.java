package org.dreambot.behaviour.method.gorillas;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

import java.util.Comparator;

public class DemonicGorillaLoot extends TickDecision {
    @Override
    public boolean evaluate() {

        GroundItem loot = GroundItems.all()
                .stream()
                .filter(x -> x.getId() != ItemID.MALICIOUS_ASHES)
                .filter(x -> x.getItem().getLivePrice() > 500_000 || x.distance() < 5)
                .filter(x -> (x.getItem().isStackable() ? (x.getAmount() * (LivePrices.get(x.getId()) + 1)) : x.getItem().getLivePrice()) > 500)
                .max(Comparator.comparingInt(x -> x.getItem().getLivePrice() * x.getAmount()))
                .orElse(null);
        if (loot != null) {
            Item cheap = PVMUtil.getCheapest();
            if (Inventory.isFull() && cheap != null && loot.getItem().getLivePrice() > cheap.getLivePrice()) {
                log("Drop item for loot");
                PVMUtil.dropCheapest();
                return true;
            }

            log("Loot " + loot.getName());
            loot.interact();
            return true;
        }

        return false;
    }
}
