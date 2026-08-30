package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.InventoryLoadoutItem;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CannonBalls extends Fractal {
    final Area EDGEVILLE_FURNACE = new Area(3089, 3504, 3111, 3494);

    public CannonBalls(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(new InventoryLoadoutItem(() -> OwnedItems.contains(ItemID.DOUBLE_AMMO_MOULD) ? ItemID.DOUBLE_AMMO_MOULD : ItemID.AMMO_MOULD, 1, 1))
                .addItem(ItemID.STEEL_BAR, 1, 26).setRefill(ScriptSettings.getSettingsData().steelBarRestockQuantity)
                .strictIgnore(ItemID.CANNONBALL)
                .setStrict(true)
        ;
    }

    @Override
    public int onLoop() {
        if (!EDGEVILLE_FURNACE.contains(Players.getLocal())) {
            Logger.info("Walking to furnace");
            if (Walking.shouldWalk()) Walking.walk(EDGEVILLE_FURNACE.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (ItemProcessing.isOpen()) {
            Logger.info("Making cannonballs");
            ItemProcessing.makeAll(ItemID.CANNONBALL);
            Sleep.sleepUntil(() -> !Inventory.contains(ItemID.STEEL_BAR), () -> Players.getLocal().isAnimating(), 2400, 100);
            return ReactionGenerator.getNormal();
        }

        if (Bank.isOpen()) Bank.close();

        Item bar = Inventory.get(ItemID.STEEL_BAR);
        GameObject furnace = GameObjects.closest("Furnace");
        Logger.info(String.format("Bar: %s, Furnace: %s", bar, furnace));
        if (bar != null && furnace != null) {
            bar.useOn(furnace);
            Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
