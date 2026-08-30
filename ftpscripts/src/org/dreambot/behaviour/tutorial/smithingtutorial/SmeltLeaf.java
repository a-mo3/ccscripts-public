package org.dreambot.behaviour.tutorial.smithingtutorial;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.behaviour.tutorial.TutorialIDs;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class SmeltLeaf extends Fractal {
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        switch (MyVarps.getTutVarp()) {
            case 330:
                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }
                NPC miningGuy = NPCs.closest("Mining Instructor");
                if (miningGuy != null && miningGuy.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 5000);
                    return ReactionGenerator.getNormal();
                }
                break;
            case 340: // make dagger
                GameObject anvil = GameObjects.closest("Anvil");
                if (anvil != null && anvil.interact("Smith")) {
                    Sleep.sleepUntil(ItemProcessing::isOpen, 5000);
                }
                break;
            case 350:
                // todo if you open then close the anvil you will get stuck here
                WidgetChild dagger = Widgets.get(w -> w.isVisible() && w.hasAction("Smith")
                        && Arrays.stream(w.getChildren()).anyMatch(x -> x.getItem().getId() == TutorialIDs.BRONZE_DAGGER));
                Logger.info(dagger + "");
                if (dagger != null && dagger.interact("Smith")) {
                    Sleep.sleepUntil(() -> Inventory.contains(TutorialIDs.BRONZE_DAGGER), 5000);
                }
                break;
        }
        return ReactionGenerator.getNormal();
    }


}
