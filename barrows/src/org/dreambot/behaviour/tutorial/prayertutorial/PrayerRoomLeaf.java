package org.dreambot.behaviour.tutorial.prayertutorial;


import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class PrayerRoomLeaf extends Fractal {
    Area church = new Area(3128, 3103, 3120, 3110);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        switch (MyVarps.getTutVarp()) {
            case 540:
            case 550:
            case 570:
            case 600:
                if (!church.contains(Players.getLocal())) {
                    if (Walking.shouldWalk()) Walking.walk(church.getCenter());
                    break;
                }

                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }

                NPC brotherBrace = NPCs.closest("Brother Brace");
                if (brotherBrace != null && brotherBrace.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 8000);
                }
                break;
            case 560:
                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }

                Tabs.openWithMouse(Tab.PRAYER);
                break;
            case 580:
                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }

                Tabs.openWithMouse(Tab.FRIENDS);
                break;
        }
        return ReactionGenerator.getNormal();
    }
}
