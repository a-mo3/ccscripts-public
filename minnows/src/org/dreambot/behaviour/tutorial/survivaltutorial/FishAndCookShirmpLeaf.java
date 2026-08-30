package org.dreambot.behaviour.tutorial.survivaltutorial;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.behaviour.tutorial.TutorialIDs;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class FishAndCookShirmpLeaf extends Fractal {

    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() >= 40;
    }

    @Override
    public int onLoop() {
        if (TutHelper.inHumanDialogue()) {
            Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        if (MyVarps.getTutVarp() == 60 || MyVarps.getTutVarp() == 30) {
            NPC expert = NPCs.closest("Survival Expert");
            if (expert != null && expert.interact("Talk-to")) {
                return ReactionGenerator.getNormal();
            }
        }

        if (MyVarps.getTutVarp() == 50) {
            Tabs.openWithMouse(Tab.SKILLS);
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.contains(TutorialIDs.RAW_SHRIMPS_2514)) {
            NPC fishSpot = NPCs.closest("Fishing spot");
            Logger.info("Attempting to fish");
            if (fishSpot != null && fishSpot.interact("Net")) {
                Sleep.sleepUntil(() -> Inventory.contains(TutorialIDs.RAW_SHRIMPS_2514), 5000);
            }
            return ReactionGenerator.getNormal();
        }

        GameObject fire = GameObjects.closest("Fire");
        if (fire != null) {
            // todo cook fish
            Item shrimp = Inventory.get(TutorialIDs.RAW_SHRIMPS_2514);
            Logger.info("Cooking shrimp");
            if (shrimp != null && shrimp.useOn(fire)) Sleep.sleepUntil(() -> Inventory.contains(TutorialIDs.SHRIMPS), 5000);
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.contains(TutorialIDs.LOGS_2511)) {
            GameObject tree = GameObjects.closest("Tree");
            if (tree != null && tree.interact("Chop down")) {
                Sleep.sleepUntil(() -> Inventory.contains(TutorialIDs.LOGS_2511), 5000);
            }
        }

        Item log = Inventory.get(TutorialIDs.LOGS_2511);
        Item tinderbox = Inventory.get(TutorialIDs.TINDERBOX);
        if (log != null && tinderbox != null) {
            log.useOn(tinderbox);
            Sleep.sleepUntil(() -> Players.getLocal().isStandingStill(), 5000);
        }

        return ReactionGenerator.getNormal();
    }
}
