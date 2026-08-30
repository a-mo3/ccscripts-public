package org.dreambot.behaviour.method.gemstone;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;

public class FindGemStoneCrab extends TickDecision {
    Area area = new Area(1272, 3174, 1283, 3165);

    @Override
    public boolean evaluate() {
        NPC crab = NPCs.closest("Gemstone Crab");
        if (crab != null) return false;
        // get to one of the caves, northmost probably fastesf
        GameObject cave = GameObjects.closest(57631); // all have the same id
        if (cave == null || cave.distance() > 10) {
            log("No cave going to north crab");
            if (Walking.shouldWalk()) Walking.walk(area.getCenter());
            return true;
        }

        // go through cave if crab isnt there (this being accepted = no cave)
        if (!Dialogues.inDialogue()) {
            cave.interact();
        } else {
            Sleep.sleep(3000);
            cave.interact();
        }
        return true;
    }
}
