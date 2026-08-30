package org.dreambot.behaviour.method.venenatis.leaveveneatis;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.method.callisto.CallistoData;
import org.dreambot.behaviour.method.venenatis.VenenatisData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.scriptdata.CallistoSettings;

import java.util.Comparator;

public class VenenatisTickLoot extends TickDecision {

    public VenenatisTickLoot() {
        setSimpleName("Loot ");
    }

    // we only want to move on spawn tile if we recently looted something
    Timer shouldStandOn = new Timer(10_000);

    /*
    when callisto respawns we want the team to split up across the arena randomly so they arent on the same tile during the fight
    after looted we move onto our randomly selected tile so when he respawns we're all split up
     */
    Tile[] aroundArena = {
            new Tile(3419, 10211, 2),
            new Tile(3417, 10207, 2),
            new Tile(3417, 10203, 2),
            new Tile(3417, 10198, 2),
            new Tile(3419, 10195, 2),
            new Tile(3423, 10194, 2),
            new Tile(3427, 10196, 2),
            new Tile(3430, 10200, 2),
            new Tile(3429, 10204, 2),
            new Tile(3428, 10208, 2),
            new Tile(3425, 10210, 2)
    };

    Tile respawnAwaitTile = aroundArena[Calculations.random(aroundArena.length)];

    @Override
    public boolean evaluate() {
        NPC vene = NPCs.closest(VenenatisData.VENENATIS_NAME);
        if (vene != null) {
            return false;
        }

        GroundItem bestLoot = GroundItems.all()
                .stream()
                .filter(x -> (x.getItem().isStackable() ? (x.getAmount() * (LivePrices.get(x.getId()) + 1)) : x.getItem().getLivePrice()) > 500)
                .max(Comparator.comparingInt(x -> x.getItem().getLivePrice() * x.getAmount()))
                .orElse(null);
        if ((bestLoot == null || bestLoot.getItem().getLivePrice() * bestLoot.getAmount() < 2500)) {
            log("No loot worth taking " + bestLoot);
            if (Walking.shouldWalk() && !respawnAwaitTile.equals(Players.getLocal().getTile()))
                Walking.walkExact(respawnAwaitTile);

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
            if (Walking.shouldWalk() && !respawnAwaitTile.equals(Players.getLocal().getTile()))
                Walking.walkExact(respawnAwaitTile);
        }
        return false;
    }
}
