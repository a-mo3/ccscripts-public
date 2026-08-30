package org.dreambot.behaviour.survivaltutorial;


import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

public class SurvivalExpertLeaf extends Leaf {
    ScriptStage scriptStage = ScriptStage.getScriptStage();
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 40; // at 40 you get shrimp etc
    }

    @Override
    public int onLoop() {
        scriptStage.setActiveLeaf("Talking to survival expert");
        NPC survivalExpert = NPCs.closest("Survival Expert");
        if (MyVarps.getTutVarp() <= 30) {
            NPC expert = NPCs.closest("Survival Expert");
            if (expert != null && expert.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::canContinue, 4000);
                Tabs.open(Tab.INVENTORY);
            }
            return 600;
        }

        if (Dialogues.inDialogue()) {
            Dialogues.chooseFirstOptionContaining();
            return 800;
        }
        if (survivalExpert != null && survivalExpert.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 4000);
            return 1200;
        }
        return 1000;
    }
}
