package org.dreambot.behaviour.method.callisto.tickcallisto;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.method.callisto.CallistoData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.scriptdata.CallistoSettings;

import java.util.Comparator;

public class CallistoTickLoot extends TickDecision {

    public CallistoTickLoot(CallistoSettings settings) {
    }

    // we only want to move on spawn tile if we recently looted something
    Timer shouldStandOn = new Timer(10_000);

    /*
    when callisto respawns we want the team to split up across the arena randomly so they arent on the same tile during the fight
    after looted we move onto our randomly selected tile so when he respawns we're all split up
     */
    Tile[] aroundArena = {
            new Tile(3352, 10335, 0),
            new Tile(3365, 10334, 0),
            new Tile(3366, 10331, 0),
            new Tile(3366, 10327, 0),
            new Tile(3365, 10323, 0),
            new Tile(3362, 10319, 0),
            new Tile(3358, 10318, 0),
            new Tile(3354, 10320, 0),
            new Tile(3351, 10321, 0),
            new Tile(3350, 10325, 0),
            new Tile(3351, 10328, 0),
            new Tile(3351, 10331, 0),
            new Tile(3355, 10335, 0),
            new Tile(3359, 10334, 0),
            new Tile(3363, 10335, 0)
    };

    Tile respawnAwaitTile = aroundArena[Calculations.random(aroundArena.length)];

    @Override
    public boolean evaluate() {
        NPC callisto = NPCs.closest(CallistoData.CALLISTO_NAME);
        if (callisto != null) {
            return false;
        }

        GroundItem bestLoot = GroundItems.all()
                .stream()
                .filter(x -> (x.getItem().isStackable() ? (x.getAmount() * (LivePrices.get(x.getId()) + 1)) : x.getItem().getLivePrice()) > 500)
                .max(Comparator.comparingInt(x -> x.getItem().getLivePrice() * x.getAmount()))
                .orElse(null);
        if ((bestLoot == null || bestLoot.getItem().getLivePrice() * bestLoot.getAmount() < 2500)) {
            log("No loot worth taking " + bestLoot);
            if (Walking.shouldWalk() && !respawnAwaitTile.equals(Players.getLocal().getTile())) Walking.walkExact(respawnAwaitTile);

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
        if (bestLoot != null) {
            bestLoot.interact("Take");
        } else {
            if (Walking.shouldWalk() && !respawnAwaitTile.equals(Players.getLocal().getTile())) Walking.walkExact(respawnAwaitTile);
        }
        return false;
    }
}
