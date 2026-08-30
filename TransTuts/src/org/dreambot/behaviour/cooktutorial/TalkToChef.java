package org.dreambot.behaviour.cooktutorial;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

public class TalkToChef extends Leaf {
    ScriptStage scriptStage = ScriptStage.getScriptStage();
    Area area = new Area(3078, 3086, 3073, 3083);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 140) {
            if (Walking.shouldWalk(4) && (Walking.getDestination() == null || !area.contains(Walking.getDestination()))) {
                Walking.walk(area.getCenter());
            }
            return 1000;
        }

        switch (MyVarps.getTutVarp()) {
            case 140:
                NPC chef = NPCs.closest("Master Chef");
                if (Dialogues.inDialogue()) {
                    Dialogues.chooseFirstOptionContaining();
                }
                if (chef != null && chef.interact("Talk-to")) {
                    return 600;
                }
                break;
            case 150: // make dough
                Item flour = Inventory.get("Pot of flour");
                Item bucket = Inventory.get("Bucket of water");

                if (flour != null && bucket != null) {
                    flour.useOn(bucket);
                }
                break;
            case 160: // bake it (420) LOL!
                Item dough = Inventory.get("bread dough");
                GameObject range = GameObjects.closest("Range");
                Logger.log(dough + " : " + range);
                if (dough != null && range != null) {
                    dough.useOn(range);
                    Sleep.sleepUntil(() -> Inventory.contains("bread"), 6000);
                }
                break;
        }
        return 1000;
    }
}
