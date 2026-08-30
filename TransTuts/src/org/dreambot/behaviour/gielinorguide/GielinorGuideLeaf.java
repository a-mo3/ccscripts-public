package org.dreambot.behaviour.gielinorguide;


import org.dreambot.Main;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

/**
 * Talk to Gielinor guide and exit the building, select experienced player
 */
public class GielinorGuideLeaf extends Leaf {
    ScriptStage scriptStage = ScriptStage.getScriptStage();
    @Override
    public boolean isValid() {
        // dont need to add upper bound here the branch takes care of it
        return MyVarps.getTutVarp() >= 2;
    }

    @Override
    public int onLoop() {
        scriptStage.setActiveLeaf("Talking to Gielinor guide");
        // leave room
        if (MyVarps.getTutVarp() == 10) {
            GameObject door = GameObjects.closest("Door");
            if (door != null && door.interact("Open")) {
                return 1800;
            }
        }

        // handle tab
        if (MyVarps.getTutVarp() == 3) {
            Main.solveDialogue("experienced");
            Tabs.open(Tab.OPTIONS);
            return 1200;
        }

        if (Dialogues.canContinue() || Dialogues.areOptionsAvailable()) {
            Main.solveDialogue("experienced");
            return 1200;
        }

        NPC guide = NPCs.closest("Gielinor Guide");
        if (guide != null && guide.interact("Talk-to")) {
            return 1200;
        }
        return 1000;
    }
}
