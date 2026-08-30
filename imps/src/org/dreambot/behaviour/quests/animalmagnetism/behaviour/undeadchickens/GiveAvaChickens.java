package org.dreambot.behaviour.quests.animalmagnetism.behaviour.undeadchickens;


import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class GiveAvaChickens extends Fractal {
    @Override
    public boolean isValid() {
        int state = PaidQuest.ANIMAL_MAGNETISM.getConfigValue();
        return state == 100 || state == 110;
    }

    @Override
    public int onLoop() {
        if (!SpecialWalker.INSIDE_AVAS_ROOM.contains(Players.getLocal())) {
            SpecialWalker.enterAvasRoom();
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
            //Dialogues.solve();
            return ReactionGenerator.getNormal();
        }

        NPC ava = NPCs.closest("AVa");
        if (ava != null && ava.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
