package org.dreambot.behaviour.smithing;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.LoadoutExecutor;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class SmithBarsFractal extends Fractal {
    private final Area AL_KHARID_FURNACE = new Area(3272, 3188, 3279, 3184);
    private final int oreA;
    private final int oreB;
    private final InventoryLoadout loadout;
    private final String targetBar;
    boolean restock;

    public SmithBarsFractal(Supplier<Boolean> acceptCondition, int oreA, int oreACount, int oreB, int oreBCount, String targetBar) {
        super(acceptCondition);
        this.oreA = oreA;
        this.oreB = oreB;
        this.loadout = new InventoryLoadout()
                .addItem(oreA, oreACount).setRefill(750)
                .addItem(oreB, oreBCount).setRefill(750)
                .setStrict(true);
        this.targetBar = targetBar;
    }

    @Override
    public int onLoop() {
        if (restock) {
            if (!loadout.isFulfilled()) {
                LoadoutExecutor.execInvLoadout(loadout);
            } else {
                restock = false;
            }
            return ReactionGenerator.getNormal();
        }

        // think this is an or
        if (!Inventory.contains(oreA, oreB)) {
            restock = true;
            return ReactionGenerator.getNormal();
        }

        if (!AL_KHARID_FURNACE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(AL_KHARID_FURNACE);
            return ReactionGenerator.getLong();
        }

        if (ItemProcessing.isOpen()) {
            ItemProcessing.makeAll(targetBar);
            Sleep.sleepUntil(() -> !Inventory.contains(oreA, oreB), () -> Players.getLocal().isAnimating(), 1400, 100);
            return ReactionGenerator.getLong();
        }

        GameObject furnace = GameObjects.closest("Furnace");
        if (furnace != null && furnace.interact("Smelt")) {
            Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
