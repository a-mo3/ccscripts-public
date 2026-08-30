package org.dreambot.behaviour.tutorial.cooktutorial;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.behaviour.tutorial.TutorialIDs;
import org.dreambot.fractals.Fractal;
import org.dreambot.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class TalkToChef extends Fractal {
    Area area = new Area(3078, 3086, 3073, 3083);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 140) {
            if (Walking.shouldWalk(4) && (Walking.getDestination() == null || !area.contains(Walking.getDestination()))) {
                if (Walking.shouldWalk(6)) Walking.walk(area.getCenter());
            }
            return ReactionGenerator.getNormal();
        }

        switch (MyVarps.getTutVarp()) {
            case 140:
                NPC chef = NPCs.closest("Master Chef");
                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }
                if (chef != null && chef.interact("Talk-to")) {
                    return ReactionGenerator.getNormal();
                }
                break;
            case 150: // make dough
                Item flour = Inventory.get(TutorialIDs.POT_OF_FLOUR_2516);
                Item bucket = Inventory.get(TutorialIDs.BUCKET_OF_WATER);
                flour.useOn(bucket);
                break;
            case 160: // bake it (420) LOL!
                Item dough = Inventory.get(TutorialIDs.BREAD_DOUGH);
                GameObject range = GameObjects.closest("Range");
                Logger.info(dough + " : " + range);
                if (dough != null && range != null) {
                    dough.useOn(range);
                    Sleep.sleepUntil(() -> Inventory.contains(TutorialIDs.BREAD), 6000);
                }
                break;
        }
        return ReactionGenerator.getNormal();
    }
}
