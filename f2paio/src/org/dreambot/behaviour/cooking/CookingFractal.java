package org.dreambot.behaviour.cooking;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CookingFractal extends Fractal {
    private static final Area AL_KHARID_STOVE = new Area(3271, 3183, 3275, 3179);
    final int rawFood;

    public CookingFractal(Supplier<Boolean> acceptCondition, int rawFood) {
        super(acceptCondition);
        this.rawFood = rawFood;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(rawFood, 1, 28).setRefill(800);
    }

    @Override
    public int onLoop() {
        // fractal loadout should handle restocking
        if (!AL_KHARID_STOVE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(AL_KHARID_STOVE);
            return ReactionGenerator.getNormal();
        }

        GameObject stove = GameObjects.closest("Stove");
        Item raw = Inventory.get(rawFood);
        if (stove != null && raw != null && raw.useOn(stove)) {
            Sleep.sleepUntil(() -> !Inventory.contains(rawFood),
                    () -> Players.getLocal().isAnimating(), 2400, 100);
        }
        return ReactionGenerator.getNormal();
    }
}
