package org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.undeadchickens;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class TalkToOldCrone extends Fractal {
    final Area OLD_CRONE_HOME = new Area(3460, 3560, 3466, 3556);
    List<Integer> states = Arrays.asList(
            70,
            73
    );

    @Override
    public boolean isValid() {
        return states.contains(PaidQuest.ANIMAL_MAGNETISM.getConfigValue());
    }

    @Override
    public int onLoop() {
        if (!OLD_CRONE_HOME.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(OLD_CRONE_HOME.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("farmers east");
            //Dialogues.solve();
            return ReactionGenerator.getNormal();
        }

        NPC oldCrone = NPCs.closest("Old crone");
        if (oldCrone != null && oldCrone.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
