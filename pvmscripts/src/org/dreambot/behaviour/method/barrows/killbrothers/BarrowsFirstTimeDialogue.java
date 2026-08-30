package org.dreambot.behaviour.method.barrows.killbrothers;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class BarrowsFirstTimeDialogue extends Fractal {
    public static boolean hasToDisableWarning = false;

    public BarrowsFirstTimeDialogue() {
        super(() -> hasToDisableWarning);
        setSimpleName("Old man");
    }

    @Override
    public int onLoop() {
        log("Disable warning");

        if (Players.getLocal().getZ() == 0 && hasToDisableWarning) {
            if (Dialogues.areOptionsAvailable()) hasToDisableWarning = false;
            if (Dialogues.inDialogue()) {
                Dialog.solve("I'll be back soon.");
                return ReactionGenerator.getQuick();
            }
            NPC oldMan = NPCs.closest("Strange Old Man");
            if (oldMan != null && oldMan.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 4400);
            }
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }
}
