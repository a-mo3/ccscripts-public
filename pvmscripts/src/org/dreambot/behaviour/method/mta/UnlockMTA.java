package org.dreambot.behaviour.method.mta;

import org.dreambot.api.Client;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * Talk to instructor to get access to MTA games
 */
public class UnlockMTA extends Fractal {
    public static boolean force;

    public UnlockMTA() {
        super(() -> force || (!MTAPointManager.get().hasAny() && PlayerSettings.getBitValue(10670) == 0));
        MTANodes.init();
        setSimpleName("MTA Tutorial");
    }

    Tile ENTRANCE_GUARD_LOC = new Tile(3363, 3305, 0);

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            log("Solve dialog");
            Dialog.solve("");
            return ReactionGenerator.getNormal();
        }

        if (ENTRANCE_GUARD_LOC.distance() > 5 || Players.getLocal().getZ() != 0) {
            slowLog("Go to MTA");
            if (Walking.shouldWalk()) Walking.walk(ENTRANCE_GUARD_LOC);
            return ReactionGenerator.getNormal();
        }

        NPC guardian = NPCs.closest("Entrance Guardian");
        if (guardian != null) {
            log("Talk to guard");
            guardian.interact();
            Sleep.sleepUntil(Dialogues::inDialogue, 6000);
            // varbit will update instantly when we get in dialogue but wont stay unless we make it all the way through
            for (int i = 0; i < 100; i++) {
                if (!Dialogues.inDialogue() || !Client.isLoggedIn()) {
                    break;
                }

                Dialog.solve("Thanks", "new");
                Sleep.sleepTicks(1);
            }
            force = false;
        } else {
            log("Cant find them");
        }

        return ReactionGenerator.getNormal();
    }
}
