package org.dreambot.behaviour.smithingtutorial;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.widget.helpers.Smithing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

public class SmeltLeaf extends Leaf {
    ScriptStage scriptStage = ScriptStage.getScriptStage();
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        scriptStage.setActiveLeaf("Smelt");
        switch (MyVarps.getTutVarp()) {
            case 330:
                if (Dialogues.inDialogue()) {
                    Dialogues.chooseFirstOptionContaining();
                    return 600;
                }
                NPC miningGuy = NPCs.closest("Mining Instructor");
                if (miningGuy != null && miningGuy.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 5000);
                    return 600;
                }
                break;
            case 340: // make dagger
                GameObject anvil = GameObjects.closest("Anvil");
                if (anvil != null && anvil.interact("Smith")) {
                    Sleep.sleepUntil(Smithing::isOpen, 5000);
                }
                break;
            case 350:
                // todo if you open then close the anvil you will get stuck here
                if (Smithing.isOpen() && Smithing.make("Bronze dagger", 1)) {
                    return 20_000; // idk lol
                }
                break;
        }
        return 1000;
    }
}
