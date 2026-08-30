package org.dreambot.behaviour.quests.animalmagnetism.behaviour.undeadchickens;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class TalkToWife extends Fractal {
    final Area WIFE_ROOM = new Area(3625, 3527, 3630, 3524);
    List<Integer> states = Arrays.asList(
            20,
            40,
            60
    );

    @Override
    public boolean isValid() {
        return states.contains(PaidQuest.ANIMAL_MAGNETISM.getConfigValue());
    }

    @Override
    public int onLoop() {
        if (!WIFE_ROOM.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(WIFE_ROOM.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("I'm here about a quest.");
            /*
            Dialog.solve(
                    "I'm here about a quest."
            );

             */
            return ReactionGenerator.getNormal();
        }

        NPC alice = NPCs.closest("Alice");
        if (alice != null && alice.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        return ReactionGenerator.getNormal();
    }

}
