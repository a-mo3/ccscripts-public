package org.dreambot.behaviour.survivaltutorial;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

public class FishAndCookShirmpLeaf extends Leaf {
    ScriptStage scriptStage = ScriptStage.getScriptStage();
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() >= 40;
    }

    @Override
    public int onLoop() {
        scriptStage.setActiveLeaf("Fish and cook shrimp");
        if (Dialogues.inDialogue()) {
            Dialogues.chooseFirstOptionContaining();
            return 600;
        }
        if (MyVarps.getTutVarp() == 60 || MyVarps.getTutVarp() == 30) {
            NPC expert = NPCs.closest("Survival Expert");
            if (expert != null && expert.interact("Talk-to")) {
                return 1200;
            }
        }
        if (MyVarps.getTutVarp() == 50) {
            Tabs.open(Tab.SKILLS);
            return 1200;
        }

        // raw shrimp
        if (!Inventory.contains(2514)) {
            NPC fishSpot = NPCs.closest("Fishing spot");
            if (fishSpot != null && fishSpot.interact("Net")) {
                Sleep.sleepUntil(() -> Inventory.contains(2514), 5000);
            }
        }

        GameObject fire = GameObjects.closest("Fire");
        if (fire != null) {
            Item shrimp = Inventory.get(2514);
            if (shrimp != null && shrimp.useOn(fire)) {
                Sleep.sleepUntil(() -> Inventory.contains("Cooked Shrimp"), 5000);
            }
            return 800;
        }

        if (!Inventory.contains("Logs")) {
            GameObject tree = GameObjects.closest("Tree");
            if (tree != null && tree.interact("Chop down")) {
                Sleep.sleepUntil(() -> Inventory.contains("Logs"), 5000);
            }
        }

        Item log = Inventory.get("Logs");
        Item tinderbox = Inventory.get("Tinderbox");
        if (log != null && tinderbox != null) {
            log.useOn(tinderbox);
            Sleep.sleepUntil(() -> Players.getLocal().isStandingStill(), 5000);
        }

        return 1000;
    }
}
