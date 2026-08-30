package org.dreambot.behaviour.training.thieving;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class StealFromTeaStall extends Fractal {
    public StealFromTeaStall(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    Tile STALL_TILE = new Tile(3268, 3410, 0);

    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            log("Drop all tea");
            Inventory.dropAll(ItemID.CUP_OF_TEA);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("");
            return ReactionGenerator.getNormal();
        }

        if (!STALL_TILE.equals(Players.getLocal().getTile())) {
            log("Walk to tea stall");
            if (Walking.shouldWalk()) Walking.walkExact(STALL_TILE);
            return ReactionGenerator.getNormal();
        }

        GameObject stall = GameObjects.closest(x -> x.hasAction("Steal-from"));
        if (stall != null) {
            stall.interact("Steal-from");
            Sleep.sleep(2400);
        }
        return ReactionGenerator.getNormal();
    }
}
