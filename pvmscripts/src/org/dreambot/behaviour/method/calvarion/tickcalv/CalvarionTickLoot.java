package org.dreambot.behaviour.method.calvarion.tickcalv;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.scriptdata.CalvarionSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;

public class CalvarionTickLoot extends TickDecision {
    // the tile calv spawns on, which we stand at to attack it quicker
    public static final Tile SPAWN = new Tile(1884, 11543, 1);
    final boolean useLootingBag;

    public CalvarionTickLoot(CalvarionSettings settings) {
        useLootingBag = settings.useLootingBag;
    }

    // we only want to move on spawn tile if we recently looted something
    Timer shouldStandOn = new Timer(10_000);

    @Override
    public boolean evaluate() {
        // open loot bag
        GroundItem lootingBag = GroundItems.closest("Looting bag");

        GroundItem bestLoot = GroundItems.all()
                .stream()
                .max(Comparator.comparingInt(x -> x.getItem().getLivePrice() * x.getAmount()))
                .orElse(null);
        if (lootingBag == null && (bestLoot == null || bestLoot.getItem().getLivePrice() * bestLoot.getAmount() < 2500)) {
            log("No loot worth taking " + bestLoot);

            if (!shouldStandOn.finished()) {
                LootingBag.refreshLootBagCache();
                if (!SPAWN.equals(Players.getLocal().getTile())) {
                    Walking.walkExact(SPAWN);
                }
            }
            return false;
        }

        // drop some food
        if (Inventory.isFull()) {
            log("Drop blighted manta ray");
            Inventory.drop(ItemID.BLIGHTED_MANTA_RAY);
        }

        shouldStandOn.reset();
        if (lootingBag != null && useLootingBag) {
            log("Take looting bag");
            lootingBag.interact("Take");
            return false;
        }

        if (bestLoot != null) bestLoot.interact("Take");
        return false;
    }
}
