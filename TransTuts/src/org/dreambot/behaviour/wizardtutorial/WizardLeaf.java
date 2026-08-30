package org.dreambot.behaviour.wizardtutorial;


import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

public class WizardLeaf extends Leaf {
    Area wizardHut = new Area(3140, 3091, 3143, 3084);
    ScriptStage scriptStage = ScriptStage.getScriptStage();

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        scriptStage.setActiveLeaf("Wizard");
        switch (MyVarps.getTutVarp()) {
            case 610:
                if (!wizardHut.contains(Players.getLocal()) && Walking.shouldWalk(2)) {
                    Walking.walk(wizardHut.getCenter());
                }
                break;
            case 620:
            case 640:
            case 670:
                NPC wizard = NPCs.closest("Magic Instructor");
                if (wizard != null && wizard.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 8000);
                    Dialogues.chooseFirstOptionContaining("Yes.", "No, I'm not planning to do that.");
                }
                break;
            case 630:
                Tabs.open(Tab.MAGIC);
                break;
            case 650:
                NPC chicken = NPCs.closest("Chicken");
                if (chicken != null) {
                    Magic.castSpellOn(Normal.WIND_STRIKE, chicken);
                    return 6000; // lol
                }
                break;
        }
        return 1200;
    }
}
