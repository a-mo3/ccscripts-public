package org.dreambot.behaviour.tutorial.questtutorial;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class QuestGuyLeaf extends Fractal {
    Area area = new Area(3082, 3125, 3089, 3119);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        Tabs.openWithMouse(Tab.QUEST);
        if (MyVarps.getTutVarp() < 220) {
            if (Walking.shouldWalk(4) && (Walking.getDestination() == null || !area.contains(Walking.getDestination()))) {
              if (Walking.shouldWalk(6)) Walking.walk(area.getCenter());
            }
            return ReactionGenerator.getNormal();
        }

        NPC questGuide = NPCs.closest("Quest Guide");
        if (TutHelper.inHumanDialogue()) {
            Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        if (questGuide != null) {
            questGuide.interact("Talk-to");
        }
        return ReactionGenerator.getNormal();
    }
}
