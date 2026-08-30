package org.dreambot.behaviour.firemaking;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;
import org.dreambot.loadouts.data.ItemID;

import java.util.function.BooleanSupplier;

public class SetFire extends IronFractal {
    public SetFire(BooleanSupplier acceptCondition) {
        super(acceptCondition);
        setSimpleName("Set fire");
    }

    /**
     * @return the closest tile to our player that a fire can be lit on
     */
    Tile findAcceptableFireTile() {
        // i dont know the rules for when you can place a fire.
        // you can place a fire in a 5x5 radius of a foresters campfire.

        return Players.getLocal().getTile();
    }

    Filter<Item> logFilter = x -> x.getName().toLowerCase().contains("log");

    @Override
    protected int onLoop() {
        // todo handle level up
        if (ItemProcessing.isOpen()) {
            log("Burn all logs");
            ItemProcessing.makeAll(logFilter);
            Sleep.sleepUntil(() -> !Inventory.contains(logFilter) || Dialogues.canContinue(),
                    6400); // the animation doesn't work well here, you will animate forever when the fire despawns
            return sleep();
        }

        // you have to check for a forester campfire first
        GameObject fire = GameObjects.closest(x -> "Forester's Campfire".equals(x.getName()) && x.distance() < 10);
        if (fire == null) fire = GameObjects.closest(x -> "Fire".equals(x.getName()) && x.distance() < 10);
        if (fire != null) {
            log("Add to fire");
            if (Players.getLocal().getAnimation() == 10565 && !Dialogues.inDialogue()) {
                log("Already firemaking");
                return sleep() + 2000;
            }
            Item inventoryLog = Inventory.get(logFilter);
            if (inventoryLog != null) {
                log("Put log in fire");
                inventoryLog.useOn(fire);
                Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
            }
            // should not be possible to have no log and accept this task
            return sleep();
        }
        // no fire, need to light one
        Tile fireTile = findAcceptableFireTile();
        if (!fireTile.equals(Players.getLocal().getTile())) {
            log("Get on fire tile");
            if (Walking.shouldWalk()) Walking.walk(fireTile);
            return sleep();
        }

        log("Start a fire");
        Widgets.closeAll();
        Inventory.combine(Inventory.get(ItemID.TINDERBOX), Inventory.get(logFilter));
        Sleep.sleepUntil(
                () -> GameObjects.closest(x -> "Fire".equals(x.getName()) && x.distance() < 3) != null || Dialogues.canContinue(),
                () -> Players.getLocal().isAnimating(),
                1000, 100);
        return sleep();
    }
}
