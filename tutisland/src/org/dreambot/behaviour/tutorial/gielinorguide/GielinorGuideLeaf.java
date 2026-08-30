package org.dreambot.behaviour.tutorial.gielinorguide;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.fractals.Fractal;
import org.dreambot.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * Talk to Gielinor guide and exit the building, select experienced player
 */
public class GielinorGuideLeaf extends Fractal {
    @Override
    public boolean isValid() {
        // dont need to add upper bound here the branch takes care of it
        return MyVarps.getTutVarp() >= 2;
    }

    @Override
    public int onLoop() {
        // leave room
        if (MyVarps.getTutVarp() == 10) {
            GameObject door = GameObjects.closest("Door");
            if (door != null && door.interact("Open")) {
                return ReactionGenerator.getNormal();
            }
        }

        // handle tab
        if (MyVarps.getTutVarp() == 3 && !Tabs.isDisabled(Tab.OPTIONS) && !Dialogues.canContinue()) {
            Logger.info("Opening options tab");
            Tabs.openWithMouse(Tab.OPTIONS);
            return ReactionGenerator.getNormal();
        }

        if (TutHelper.inHumanDialogue()) {
            Dialog.solve("experienced");
            return ReactionGenerator.getNormal();
        }

        NPC guide = NPCs.closest("Gielinor Guide");
        if (guide != null && guide.interact("Talk-to")) {
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
