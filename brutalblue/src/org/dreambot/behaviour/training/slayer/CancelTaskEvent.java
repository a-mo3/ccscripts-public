package org.dreambot.behaviour.training.slayer;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class CancelTaskEvent extends AbstractResponseEvent<CancelTaskEvent.Response> {
    public enum Response {
        STREAK_RESET,
        CANCELLED,
        TIMEOUT,
    }

    private static final Area TURAEL = new Area(2929, 3538, 2933, 3535);
    final Timer timeout = new Timer(5 * 1000 * 60);
    int startingKC = -1;

    public CancelTaskEvent() {
        this.startingKC = killsRemaining();
    }

    @Override
    public int onLoop() {
        if (timeout.finished()) {
            setResponse(Response.TIMEOUT);
            return ReactionGenerator.getNormal();
        }

        if (killsRemaining() == 0) {
            Logger.info("0 kills remaining on task");
            setResponse(Response.CANCELLED);
            return ReactionGenerator.getNormal();
        }

        if (!TURAEL.contains(Players.getLocal())) {
            Logger.info("Walking to turael to cancel a task");
            if (Walking.shouldWalk()) Walking.walk(TURAEL);
            return ReactionGenerator.getNormal();
        }

        NPC turael = NPCs.closest("Turael");
        if (turael == null) {
            Logger.info("Could not find turael");
            return ReactionGenerator.getNormal();
        }

        if (getSlayerPoints() < 30) {
            if (startingKC != killsRemaining()) {
                Logger.info("Streak reset cancelled " + startingKC + " " + killsRemaining());
                setResponse(Response.STREAK_RESET);
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve("Yes");
                return ReactionGenerator.getNormal();
            }

            turael.interact("Assignment");
            Sleep.sleepUntil(Dialogues::inDialogue, 4400);
            return ReactionGenerator.getNormal();
        }

        Logger.info("Slayer points " + getSlayerPoints());

        if (!Widgets.isOpen()) {
            Logger.info("Open slayer menu");

            turael.interact("Rewards");
            Sleep.sleepUntil(Widgets::isOpen, 4000);
            return ReactionGenerator.getNormal();
        }

        WidgetChild cancelConfirm = Widgets.get(426, 8, 55);
        WidgetChild tasksTab = Widgets.get(426, 12, 6);
        WidgetChild cancelTask = Widgets.get(426, 26, 0);

        if (cancelConfirm != null) {
            Logger.info("Confirming task cancel");
            if (cancelConfirm.interact()) {
                setResponse(Response.CANCELLED);
                Sleep.sleep(2400);
                Widgets.closeAll();
            }
            return ReactionGenerator.getLong();
        }

        if (cancelTask != null) {
            Logger.info("Cancelling task");
            cancelTask.interact();
            return ReactionGenerator.getLong();
        }

        if (tasksTab != null) {
            Logger.info("Task tab");
            tasksTab.interact();
            return ReactionGenerator.getLong();
        }

        return ReactionGenerator.getNormal();
    }

    public static int getSlayerPoints() {
        return PlayerSettings.getBitValue(4083);
    }

    public static int killsRemaining() {
        return PlayerSettings.getConfig(394);
    }
}

