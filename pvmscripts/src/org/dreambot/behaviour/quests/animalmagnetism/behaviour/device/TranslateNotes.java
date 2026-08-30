package org.dreambot.behaviour.quests.animalmagnetism.behaviour.device;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class TranslateNotes extends Fractal {
    @Override
    public boolean isValid() {
        int state = PaidQuest.ANIMAL_MAGNETISM.getConfigValue();
        return state == 190 || state == 200 || state == 210;
    }

    int[] buttonChildIds = new int[]{
            26,
            31,
            34,
            40,
            43,
            46
    };

    @Override
    public int onLoop() {
        if (!Inventory.contains(ItemID.RESEARCH_NOTES_10492)) {
            if (Dialogues.inDialogue()) {
                Dialog.solve();
                return ReactionGenerator.getNormal();
            }

            WidgetChild puzzle = Widgets.get(480, 0);
            if (puzzle != null) {
                Logger.info("Trying to close ava puzzle");
                Widgets.closeAll();
                WidgetChild puzzleClose = Widgets.get(480, x -> x.hasAction("Close"));
                if (puzzleClose != null) puzzleClose.interact();
            }


            NPC ava = NPCs.closest("Ava");
            if (Widgets.isOpen()) Widgets.closeAll();
            if (ava != null && ava.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        WidgetChild puzzle = Widgets.get(480, 0);
        if (puzzle == null || !puzzle.isVisible()) {
            Inventory.interact(ItemID.RESEARCH_NOTES_10492, "Translate");
            return ReactionGenerator.getNormal();
        }

        for (int id : buttonChildIds) {
            WidgetChild button = Widgets.get(480, id);
//            Log.info(button);
            if (button != null && button.isVisible()) {
                button.interact();
                Sleep.sleep(600, 1900);
            }
        }
        return ReactionGenerator.getNormal();
    }
}
