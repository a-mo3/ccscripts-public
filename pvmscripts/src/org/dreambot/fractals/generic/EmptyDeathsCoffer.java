package org.dreambot.fractals.generic;

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
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class EmptyDeathsCoffer extends Fractal implements ChatListener {
    boolean shouldExit = false;
    final boolean shouldEmpty; // this for is script setting
    public static boolean forceEmpty;

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
        return PlayerSettings.getBitValue(10465) > 0
                || forceEmpty
                || (Client.isDynamicRegion() && NPCs.closest("Death") != null);
    }

    Area LUMMY_COFFIN = new Area(3237, 3195, 3240, 3191);
    Area EDGEVILLE_COFFIN = new Area(3090, 3482, 3097, 3475);
    boolean firstTime = false;

    @Override
    public int onLoop() {
        PrayerUtils.disableAll();
        WidgetChild cantPay = Widgets.get(x -> x.getText().contains("You don't have enough money for the fees."));
        if (cantPay != null) {
            log("Need more money to pay death coffer fee");
            forceEmpty = true;
            new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                    .addRequiredItem(ItemID.COINS_995, 300_000)
                    .execute();
            return ReactionGenerator.getNormal();
        }

        String dialog = Dialogues.getNPCDialogue();
        if (Dialogues.inDialogue()) {
            if (dialog != null && dialog.contains("I haven't finished talking to you yet")) {
                log("First time death");
                Client.getInstance().getRandomManager().enableSolver(RandomEvent.DEATHS_DOOR);
                firstTime = true;
                return ReactionGenerator.getNormal();
            }
        }

        if (Client.isDynamicRegion() && Widgets.get(x -> x.getText().contains("<str>")) != null) {
            log("First time doing death enabling solver");
            Client.getInstance().getRandomManager().enableSolver(RandomEvent.DEATHS_DOOR);
            firstTime = true;
            return ReactionGenerator.getNormal();
        }

        if (!Client.isDynamicRegion()) {
            shouldExit = false;
        }

//        String[] options = Dialogues.getOptions();
//        if (options != null && Dialogues.inDialogue() && Arrays.asList(options).contains("Pay Death's fee.")) {
//            log("Pay death fee");
//            Dialog.solve("Pay Death's fee.");
//            return ReactionGenerator.getNormal();
//        }

        if (shouldExit && Client.isDynamicRegion()) {
            Widgets.closeAll();
            log("Leave death");

            GameObject portal = GameObjects.closest("Portal");
            if (portal != null && portal.interact()) {
                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 2400);
            }

            return ReactionGenerator.getNormal();
        }
        // bank all if full
        NPC death = NPCs.closest("Death");
        if (Inventory.isFull()) {
            log("Full inventory");
            forceEmpty = true; // set this state so the script can remember to come back for the rest
            if (death != null && Client.isDynamicRegion()) {
                GameObject portal = GameObjects.closest("Portal");
                if (portal != null && portal.interact()) {
                    Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 2400);
                }
                return ReactionGenerator.getNormal();
            }

            Logger.info("Banking all items");
            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }

        // enter deaths office
        if (!Client.isDynamicRegion() && death == null) {
            log("Entering deaths office");
            if (!closestDeathEntrance().contains(Players.getLocal())) {
                log("Walking to closest death domain");
                if (Walking.shouldWalk()) Walking.walk(closestDeathEntrance());
                return ReactionGenerator.getQuick();
            }

            GameObject deathsDomain = GameObjects.closest("Death's Domain");
            if (deathsDomain != null) {
                log("Enter death domain");
                deathsDomain.interact("Enter");
                Antiban.sleepUntil(Client::isDynamicRegion, 2400);
                return ReactionGenerator.getQuick();
            }
            return ReactionGenerator.getNormal();
        }

        if (PlayerSettings.getBitValue(10465) > 0) {
            log("Dealing with grave");
            if (Dialogues.inDialogue()) {
                log("Paying / collect dialgoue");
                Dialog.solve("pay your", "I collect the");
                Sleep.sleep(1500);
                return ReactionGenerator.getNormal();
            }

            log("Talking to death");
            if (death != null) death.interact("Talk-to");
            Antiban.sleepUntil(Dialogues::inDialogue, 2400);
            return ReactionGenerator.getNormal();
        }

        WidgetChild deathItems = Widgets.get(669, 3);
        WidgetChild takeAll = Widgets.get(669, 10);
        if (!Widgets.isOpen() || deathItems == null || takeAll == null) {
            log("Opening item menu");
            if (Dialogues.inDialogue()) {
                log("In dialogue");
                log(Arrays.toString(Dialogues.getOptions()));
                Dialog.solve("fee", "anything for me");
                Sleep.sleep(1500);
                return ReactionGenerator.getNormal();
            }

            if (death != null) death.interact("Collect");
            return ReactionGenerator.getNormal();
        }

        if (deathItems.getChildren().length != 0) {
            if (Dialogues.inDialogue()) {
                log("In dialogue");
                log(Arrays.toString(Dialogues.getOptions()));
                Dialog.solve("fee", "anything for me");
                Sleep.sleep(1500);
                return ReactionGenerator.getNormal();
            }
            log("Take all death items");
            takeAll.interact();
            forceEmpty = true;
        } else {
            log("No items in death");
            shouldExit = true;
            forceEmpty = false;
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;

        if (message.getMessage().toLowerCase().contains("when you have concluded death's introduction")) {
            log("First time death - no exit msg");
            Client.getInstance().getRandomManager().enableSolver(RandomEvent.DEATHS_DOOR);
            firstTime = true;
            return;
        }

        if (message.getMessage().toLowerCase().contains("nothing here to take.")) {
            shouldExit = true;
            forceEmpty = false;
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
