package org.dreambot.behaviour.questtutorial;


import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

public class QuestGuyLeaf extends Leaf {
    ScriptStage scriptStage = ScriptStage.getScriptStage();
    Area area = new Area(3082, 3125, 3089, 3119);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        Tabs.open(Tab.QUEST);
        scriptStage.setActiveLeaf("Quest guy");
        if (MyVarps.getTutVarp() < 220) {
            if (Walking.shouldWalk(4) && (Walking.getDestination() == null || !area.contains(Walking.getDestination()))) {
                Walking.walk(area.getCenter());
            }
            return 1000;
        }

        NPC questGuide = NPCs.closest("Quest Guide");
        if (Dialogues.inDialogue()) {
            Dialogues.chooseFirstOptionContaining();
            return 600;
        }
        if (questGuide != null && questGuide.interact("Talk-to")) {
            return 1200;
        }


        return 1000;
    }
}
