package org.dreambot.behaviour.method.gwd.zammy.range;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PVMUtil;

public class DropItemOnTile extends TickDecision implements SpawnListener {
    // tile that loot needs to be on
    public static final Tile ITEM_TILE = new Tile(2928, 5321, 2);
    final Timer itemTimer = new Timer();

    public DropItemOnTile() {
        Client.getInstance().addEventListener(this);
        setSimpleName("Drop item");
    }

    @Override
    public boolean evaluate() {
        // todo pick up and replace if its about to despawn
        GroundItem i = GroundItems.closest(x -> ITEM_TILE.equals(x.getTile()));
        if (i != null) {
            // no need to place anything
            return false;
        }

        if (!ITEM_TILE.equals(Players.getLocal().getTile())) {
            log("Walk onto red click tile");
            Walking.walkExact(ITEM_TILE);
        } else {
            log("Drop a cheap item there");
            PVMUtil.dropCheapest();
            return false;
        }
        return true;
    }

    @Override
    public void onGroundItemSpawn(GroundItem object) {
        if (object == null) return;
        if (!ITEM_TILE.equals(object.getTile())) return;
        itemTimer.reset();
    }
}
