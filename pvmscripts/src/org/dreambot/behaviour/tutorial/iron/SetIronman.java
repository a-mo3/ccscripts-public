package org.dreambot.behaviour.tutorial.iron;

import org.dreambot.antiban.Antiban;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.AccountManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.awt.*;
import java.util.function.Supplier;

public class SetIronman extends Fractal {
    final IronmanState ironmanType;

    public SetIronman(Supplier<Boolean> acceptCondition, IronmanState type) {
        super(acceptCondition);
        this.ironmanType = type;
    }

    final Tile IRONMAN_TUTOR_LOC = new Tile(3131, 3086);

    @Override
    public int onLoop() {
        // set bank pin
        if (AccountManager.getAccountBankPin().isEmpty()) {
            log("Setting a bank pin");
            AccountManager.setPin("6969"); // random numbers gives people problems with external account mangers
            // so they can get a fixed pin or set it in EF before running tut
        }

        WidgetChild proceed = Widgets.get(x -> x.hasAction("Proceed"));
        if (proceed != null && proceed.isVisible()) {
            log("proceed");
            proceed.interact();
            return ReactionGenerator.getNormal() + 6000;
        }

        WidgetChild button = Widgets.get(x -> x.hasAction(ironmanType.type));
        if (button != null && button.isVisible()) {
            if (ironmanType == IronmanState.HCIM) {
                log("Scroll check hcim");
                WidgetChild p = Widgets.get(890, 3);
                if (p != null) {
                    int sign = scrollDirection(button, p);
                    if (sign != 0) {
                        Mouse.move(new Point((int) p.getRectangle().getCenterX(), (int) p.getRectangle().getCenterY()));
                        log("Scroll for hcim");
                        Mouse.scroll(sign == 1, 600, () -> false);
                    }
                }
            }
            log("Become ironman");
            button.interact();
            Sleep.sleep(3000);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.canContinue() || Dialogues.areOptionsAvailable() || Dialogues.isProcessing()) {
            log("Handle dialogue");
            Dialog.solve("Ironman", "Ironmen");
            return ReactionGenerator.getNormal();
        }

        NPC tutor = NPCs.closest("Ironman tutor");
        if (tutor != null) {
            log("Talking to tutor");
            tutor.interact();
            Antiban.sleepUntil(Dialogues::inDialogue, 2400);
            return ReactionGenerator.getNormal();
        } else {
            if (Walking.shouldWalk()) Walking.walk(IRONMAN_TUTOR_LOC);
        }

        return ReactionGenerator.getNormal();
    }

    /**
     * checks if the target widget is within scroll pane bounds, and returns a sign for which direct to scroll
     *
     * @param target     button you are trying to get within scroll bounds
     * @param scrollPane the pane that holds the button
     * @return 1 = scroll up, 0 = within bounds,
     */
    static int scrollDirection(WidgetChild target, WidgetChild scrollPane) {
        if (scrollPane == null) return 0;
        // we only scroll up and down in this game, so lets only consider Y
        int targetTop = target.getY();
        int targetBottom = targetTop + target.getHeight();

        int paneTop = scrollPane.getY();
        int paneBottom = scrollPane.getY() + scrollPane.getHeight();

        Logger.info(String.format("Scroll direction Top t: %d Top p: %d Bottom t: %d Bottom p: %d", targetTop, paneTop, targetBottom, paneBottom));
        // Y is 0 at the top of the screen
        if (targetTop <= paneTop) return 1;
        if (targetBottom >= paneBottom) return -1;
        return 0;
    }
}
