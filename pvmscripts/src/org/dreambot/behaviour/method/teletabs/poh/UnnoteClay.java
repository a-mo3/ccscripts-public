package org.dreambot.behaviour.method.teletabs.poh;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class UnnoteClay extends Fractal {
    Area RIMMINGTON = new Area(2948, 3222, 2956, 3212);

    public UnnoteClay(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        if (Walking.getRunEnergy() > 10 && !Walking.isRunEnabled()) Walking.toggleRun();

        if (Client.isDynamicRegion()) {
            // todo recharge run energy if you have none
            Logger.info("Leave house");
            GameObject portal = GameObjects.closest("Portal");

            if (portal != null) {
                if (!Menu.isMenuManipulationActive() && !portal.isOnScreen()) {
                    log("Mouse only distance walk");
                    if (Walking.shouldWalk()) Walking.walk(portal.getTile());
                    return ReactionGenerator.getNormal();
                }

                portal.interact();
                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 4000);
            }
            return ReactionGenerator.getNormal();
        }

        NPC phials = NPCs.closest("Phials");
        if (phials == null) {
            Logger.info("No phials going to rimmington");
            if (Walking.shouldWalk()) Walking.walk(RIMMINGTON);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Logger.info("Unnote all");
            // todo handle no gp here
            Dialog.solve("all");
            return ReactionGenerator.getNormal();
        }

        Item notedClay = Inventory.get(ItemID.SOFT_CLAY + 1);
        if (notedClay != null) {
            notedClay.useOn(phials);
            Sleep.sleepUntil(Dialogues::inDialogue, 4400);
        }
        return ReactionGenerator.getNormal();
    }
}
