package org.dreambot.fractals.getmoney;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

public class PickPotatoes extends Fractal {
    Area POTATOES = new Area(
            new Tile(3259, 3320, 0),
            new Tile(3265, 3320, 0),
            new Tile(3265, 3299, 0),
            new Tile(3242, 3299, 0),
            new Tile(3242, 3304, 0));

    @Override
    public int onLoop() {
        if (LivePrices.get(ItemID.POTATO) * OwnedItems.count(ItemID.POTATO) + OwnedItems.count(ItemID.COINS_995) > GetMoneyBranch.gpTarget) {
            log("Looks like we have enough taters time to sell " + GetMoneyBranch.gpTarget);
            new SellAllEvent(ItemID.POTATO).execute();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isFull()) {
            log("Full of taters deposit all in bank");
            new BankAllInventoryEvent().execute();
        }

        if (!POTATOES.contains(Players.getLocal())) {
            slowLog("Walk to potato farm");
            if (Walking.shouldWalk()) Walking.walk(POTATOES);
            return ReactionGenerator.getNormal();
        }

        GameObject tater = GameObjects.closest(x -> POTATOES.contains(x) && x.hasAction("Pick"));
        if (tater != null) {
            tater.interact("Pick");
        }
        return ReactionGenerator.getNormal();
    }
}
