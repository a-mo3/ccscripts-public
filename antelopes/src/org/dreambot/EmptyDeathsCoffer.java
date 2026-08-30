package org.dreambot;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class EmptyDeathsCoffer extends Fractal implements ChatListener {
    boolean shouldExit = false;
    final boolean shouldEmpty; // this for is script setting

    public EmptyDeathsCoffer(boolean shouldEmpty) {
        Client.getInstance().getRandomManager().disableSolver(RandomEvent.DEATHS_DOOR);
        Client.getInstance().addEventListener(this);
        this.shouldEmpty = shouldEmpty;
    }

    public EmptyDeathsCoffer() {
        Client.getInstance().getRandomManager().disableSolver(RandomEvent.DEATHS_DOOR);
        Client.getInstance().addEventListener(this);
        this.shouldEmpty = true;
    }

    public int gravestoneTimer() {
        return PlayerSettings.getBitValue(10465);
    }

    @Override
    public boolean isValid() {
        if (firstTime && !Client.isDynamicRegion()) {
            Client.getInstance().getRandomManager().disableSolver(RandomEvent.DEATHS_DOOR);
            firstTime = false;
        }
        return PlayerSettings.getBitValue(10465) > 0 ||
                (Client.isDynamicRegion() && NPCs.closest("Death") != null);
    }

    Area LUMMY_COFFIN = new Area(3237, 3195, 3240, 3191);
    Area EDGEVILLE_COFFIN = new Area(3090, 3482, 3097, 3475);
    boolean firstTime = false;

    @Override
    public int onLoop() {
        if (Client.isDynamicRegion() && Widgets.get(x -> x.getText().contains("<str>")) != null) {
            log("First time doing death enabling solver");
            Client.getInstance().getRandomManager().enableSolver(RandomEvent.DEATHS_DOOR);
        }

        if (!Client.isDynamicRegion()) {
            shouldExit = false;
        }

        if (shouldExit && Client.isDynamicRegion()) {
            Widgets.closeAll();
            log("Leave death");

            GameObject portal = GameObjects.closest("Portal");
            if (portal != null && portal.interact()) {
                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 2400);
            }

            return ReactionGenerator.getNormal();
        }
        // bank all if full
        NPC death = NPCs.closest("Death");
        if (Inventory.isFull()) {
            if (death != null && Client.isDynamicRegion()) {
                GameObject portal = GameObjects.closest("Portal");
                if (portal != null && portal.interact()) {
                    Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 2400);
                }
                return ReactionGenerator.getNormal();
            }
            Logger.info("Banking all items");
            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }

        // enter deaths office
        if (!Client.isDynamicRegion() && death == null) {
            Logger.info("Entering deaths office");
            if (!closestDeathEntrance().contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(closestDeathEntrance());
                return ReactionGenerator.getQuick();
            }

            GameObject deathsDomain = GameObjects.closest("Death's Domain");
            if (deathsDomain != null) {
                deathsDomain.interact("Enter");
                Sleep.sleepUntil(Client::isDynamicRegion, 2400);
                return ReactionGenerator.getQuick();
            }
            return ReactionGenerator.getNormal();
        }

        if (PlayerSettings.getBitValue(10465) > 0) {
            if (Dialogues.inDialogue()) {
                Dialog.solve("pay your", "I collect the");
                return ReactionGenerator.getNormal();
            }

            Logger.info("Talking to death");
            if (death != null) death.interact("Talk-to");
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            return ReactionGenerator.getNormal();
        }

        WidgetChild deathItems = Widgets.get(669, 3);
        WidgetChild takeAll = Widgets.get(669, 10);
        if (deathItems == null || takeAll == null) {
            Logger.info("Opening item menu");
            if (death != null) death.interact("Collect");
            return ReactionGenerator.getNormal();
        }

        if (deathItems.getChildren().length != 0) {
            takeAll.interact();
        } else {
            shouldExit = true;
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("nothing here to take.")) {
            shouldExit = true;
        }

        if (message.getMessage().toLowerCase().contains("please use the portal")) {
            shouldExit = true;
        }

        if (message.getMessage().toLowerCase().contains("have items stored in an item retrieval service")) {
            shouldExit = false;
        }
    }

    private Area closestDeathEntrance() {
        return LUMMY_COFFIN.getCenter().distance() > EDGEVILLE_COFFIN.getCenter().distance() ? EDGEVILLE_COFFIN : LUMMY_COFFIN;
    }
}
