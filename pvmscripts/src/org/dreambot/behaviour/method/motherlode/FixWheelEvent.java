package org.dreambot.behaviour.method.motherlode;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class FixWheelEvent extends AbstractResponseEvent<FixWheelEvent.Response> {
    enum Response {
        FIXED_WHEEL,
        TIMEOUT
    }

    Area WHEEL_AREA = new Area(3741, 5673, 3743, 5659);

    Timer timeout = new Timer(4 * 60 * 1000);
    Tile crateTile = new Tile(3752, 5674, 0);

    @Override
    public int onLoop() {
        if (timeout.finished()) {
            Inventory.dropAll(ItemID.HAMMER);
            setResponse(Response.TIMEOUT);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
        }

        GameObject brokenStrut = GameObjects.closest("Broken strut");
        if (brokenStrut != null) {
            if (!WHEEL_AREA.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(WHEEL_AREA);
                return ReactionGenerator.getQuick();
            }

            if (!Inventory.contains(ItemID.HAMMER)) {
                if (Inventory.isFull() && MLMMining.MLM_INNER.contains(Players.getLocal())) {
                    Inventory.drop(ItemID.PAYDIRT);
                    return ReactionGenerator.getNormal();
                }

                GameObject crate = GameObjects.closest(x -> x.getName().contains("Crate") && x.getTile().equals(crateTile));
                if (crate != null && crate.interact("Search")) {
                    Sleep.sleepUntil(() -> Inventory.contains(ItemID.HAMMER), 5000);
                }
                return ReactionGenerator.getNormal();
            }

            if (brokenStrut.interact("Hammer")) {
                Sleep.sleepUntil(() -> GameObjects.closest(x -> x.equals(brokenStrut)) == null, 20_000);
            }
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.HAMMER)) {
            Inventory.dropAll(ItemID.HAMMER);
            return ReactionGenerator.getNormal();
        }

        GroundItem droppedDIrt = GroundItems.closest(ItemID.PAYDIRT);
        if (droppedDIrt != null) {
            droppedDIrt.interact("Take");
            Sleep.sleepUntil(() -> !droppedDIrt.exists(), 1400);
            return ReactionGenerator.getNormal();
        }

        setResponse(Response.FIXED_WHEEL);
        return ReactionGenerator.getNormal();

    }

}
