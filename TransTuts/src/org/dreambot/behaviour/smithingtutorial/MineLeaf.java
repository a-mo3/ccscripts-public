package org.dreambot.behaviour.smithingtutorial;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

public class MineLeaf extends Leaf {
    ScriptStage scriptStage = ScriptStage.getScriptStage();
    Area area = new Area(3077, 9510, 3086, 9496);


    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 330;
    }

    @Override
    public int onLoop() {
        scriptStage.setActiveLeaf("Mine");
        switch (MyVarps.getTutVarp()) {
            case 250:
            case 260: // go talk to mine instuctor for pickaxe
            case 270:
                if (!area.contains(Players.getLocal())) {
                    if (Walking.shouldWalk(4) && (Walking.getDestination() == null || !area.contains(Walking.getDestination()))) {
                        Walking.walk(area.getCenter());
                    }
                    return 1000;
                }

                if (Dialogues.inDialogue()) {
                    Dialogues.chooseFirstOptionContaining();
                    return 600;
                }
                NPC miningGuy = NPCs.closest("Mining Instructor");
                if (miningGuy != null && miningGuy.interact("Talk-to")) {
                    return 600;
                }
                break;
            case 300: // mine tin
                GameObject tinRock = GameObjects.closest(r -> r.getName().equals("Rocks") && r.getID() == 10080);
                if (tinRock != null && tinRock.interact("Mine")) {
                    Sleep.sleepUntil(() -> Inventory.contains("tin ore"), 8000);
                    return 1200;
                }
                break;
            case 310: // mine copper
                GameObject copperRock = GameObjects.closest(r -> r.getName().equals("Rocks") && r.getID() == 10079);
                if (copperRock != null && copperRock.interact("Mine")) {
                    Sleep.sleepUntil(() -> Inventory.contains("copper ore"), 8000);
                    return 1200;
                }
                break;
            case 320: // smelt bar
                GameObject furnace = GameObjects.closest("Furnace");
                if (furnace != null && furnace.interact("Use")) {
                    Sleep.sleepUntil(() -> !Inventory.contains("copper ore"), 5000);
                }
                break;
        }
        return 1000;
    }
}
