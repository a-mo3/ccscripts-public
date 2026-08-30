package org.dreambot.behaviour.tutorial.survivaltutorial;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class SurvivalExpertLeaf extends Fractal {
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 40; // at 40 you get shrimp etc
    }

    @Override
    public int onLoop() {
        NPC survivalExpert = NPCs.closest("Survival Expert");
        if (TutHelper.inHumanDialogue()) {
            Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        if (MyVarps.getTutVarp() == 30) {
            Tabs.openWithMouse(Tab.INVENTORY);
            NPC expert = NPCs.closest("Survival Expert");
            if (expert != null && expert.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 4000);
            }
            return ReactionGenerator.getNormal();
        }

        if (survivalExpert == null) {
            Logger.info("cant find survial expert");
            return ReactionGenerator.getNormal();
        }

        if (survivalExpert.distance() > 8) {
            Logger.info("Walk to survival expert");
            if (Walking.shouldWalk()) Walking.walk(survivalExpert);
            return ReactionGenerator.getNormal();
        }

        if (survivalExpert.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 8000);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
