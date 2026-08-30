package org.dreambot.behaviour.prayertutorial;


import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

public class PrayerRoomLeaf extends Leaf {
    ScriptStage scriptStage = ScriptStage.getScriptStage();
    Area church = new Area(3128, 3103, 3120, 3110);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        scriptStage.setActiveLeaf("Prayer");
        switch (MyVarps.getTutVarp()) {
            case 540:
                if (Walking.shouldWalk(3)) {
                    Walking.walk(church.getCenter());
                }
                break;
            case 550:
            case 570:
            case 600:
                NPC brotherBrace = NPCs.closest("Brother Brace");
                if (brotherBrace != null && brotherBrace.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 8000);
                    Dialogues.chooseFirstOptionContaining();
                }
                break;
            case 560:
                Tabs.open(Tab.PRAYER);
                break;
            case 580:
                Tabs.open(Tab.FRIENDS);
                break;
        }
        return 1200;
    }
}
