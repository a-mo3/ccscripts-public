package org.dreambot.behaviour.method.vetion.tickvetion;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.scriptdata.VetionSettings;

import java.util.Comparator;

public class VetionTickLoot extends TickDecision {

    public VetionTickLoot(VetionSettings settings) {
    }

    // we only want to move on spawn tile if we recently looted something
    Timer shouldStandOn = new Timer(10_000);

    @Override
    public boolean evaluate() {
        // open loot bag
        GroundItem lootingBag = GroundItems.closest("Looting bag");

        GroundItem bestLoot = GroundItems.all()
                .stream()
                .filter(x -> (x.getItem().isStackable() ? (x.getAmount() * (LivePrices.get(x.getId()) + 1)) : x.getItem().getLivePrice()) > 500)
                .max(Comparator.comparingInt(x -> x.getItem().getLivePrice() * x.getAmount()))
                .orElse(null);
        if (lootingBag == null && (bestLoot == null || bestLoot.getItem().getLivePrice() * bestLoot.getAmount() < 2500)) {
            log("No loot worth taking " + bestLoot);

            if (!shouldStandOn.finished()) {
                LootingBag.refreshLootBagCache();
//                if (!SPAWN.equals(Players.getLocal().getTile())) {
//                    Walking.walkExact(SPAWN);
//                }
            }
            return false;
        }

        // drop some food
        if (Inventory.isFull()) {
            log("Drop blighted manta ray");
            Inventory.drop(ItemID.BLIGHTED_MANTA_RAY);
        }

        shouldStandOn.reset();
//        if (lootingBag != null && useLootingBag) {
//            log("Take looting bag");
//            lootingBag.interact("Take");
//            return false;
//        }

        if (bestLoot != null) bestLoot.interact("Take");
        return false;
    }
}
