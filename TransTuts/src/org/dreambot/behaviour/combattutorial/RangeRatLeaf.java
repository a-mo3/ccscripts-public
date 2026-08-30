package org.dreambot.behaviour.combattutorial;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;

public class RangeRatLeaf extends Leaf {
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        switch (MyVarps.getTutVarp()) {
            case 470: // talk to combat instructor but you need to leave the pit first cant just interact
                NPC combatTrainer = NPCs.closest("Combat Instructor");
                if (combatTrainer == null) {
                    Logger.log("null combat instructor");
                    return 800;
                }
                if (!combatTrainer.canReach()) {
                    Walking.walk(combatTrainer.getTile());
                    break;
                }
                if (combatTrainer.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 5000);
                    Dialogues.chooseFirstOptionContaining();
                }
                break;
            case 480: // kill rat.
            case 490:
                // YOLO!
                Inventory.interact("Shortbow", "Wield");
                Inventory.interact("Bronze arrow", "Wield");
                // maybe check if u have bow here
                if (!Players.getLocal().isInCombat()) {
                    NPC rat = NPCs.closest("Giant rat");
                    if (rat != null && !rat.isInCombat()) {
                        rat.interact("Attack");
                    }
                }
                break;
        }
        return 1200;
    }
}
