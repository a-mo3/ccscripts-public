package org.dreambot.behaviour.quests.animalmagnetism.behaviour.undeadchickens;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class TalkToHusband extends Fractal {
    final Area HUSBAND_AREA = new Area(3625, 3531, 3609, 3523);
    List<Integer> states = Arrays.asList(
            10,
            30,
            50,
            76,
            80,
            90
    );

    @Override
    public boolean isValid() {
        return states.contains(PaidQuest.ANIMAL_MAGNETISM.getConfigValue())
                || (PaidQuest.ANIMAL_MAGNETISM.getConfigValue() == 100 && Inventory.count(ItemID.UNDEAD_CHICKEN) < 2);
    }

    @Override
    public int onLoop() {
        if (Client.isInCutscene()) {
            if (Dialogues.inDialogue()) Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        if (!HUSBAND_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(HUSBAND_AREA.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("Give the Crone-made amulet to Malcolm.",
                    "Okay, you need it more than I do, I suppose.",
                    "Could I buy those chickens now, then?",
                    "Could I buy 2 chickens?");
            /*
            Dialogues.solve(
                "Give the Crone-made amulet to Malcolm.",
                    "Okay, you need it more than I do, I suppose.",
                    "Could I buy those chickens now, then?",
                    "Could I buy 2 chickens?"
            );

             */
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.GHOSTSPEAK_AMULET)) {
            Inventory.interact(ItemID.GHOSTSPEAK_AMULET, "Wear");
            return ReactionGenerator.getNormal();
        }

        NPC husband = NPCs.closest("Malcolm");
        if (husband != null && husband.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
