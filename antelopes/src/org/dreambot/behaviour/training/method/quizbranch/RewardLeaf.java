package org.dreambot.behaviour.training.method.quizbranch;


import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;

public class RewardLeaf extends Fractal {
    @Override
    public boolean isValid() {
        return PlayerSettings.getBitValue(3686) == 268435455;
    }

    private final Area ORLANDO_SMITH = new Area(1762, 4953, 1757, 4958, 0);

    @Override
    public int onLoop() {
        if (Dialogues.canContinue()) {
            Dialogues.continueDialogue();
        } else if (ORLANDO_SMITH.contains(Players.getLocal())) {
            NPC orlando = NPCs.closest("Orlando Smith");
            if (orlando != null) {
                orlando.interact("Talk-to");
                Sleep.sleepUntil(Dialogues::canContinue, 2000);
            }
        } else {
            if (Walking.shouldWalk(3)) {
              if (Walking.shouldWalk(6)) Walking.walk(ORLANDO_SMITH.getRandomTile());
                Sleep.sleepUntil(() -> Players.getLocal().isMoving(), 1300);
            }
        }
        return Calculations.random(1700, 2300);
    }
}
