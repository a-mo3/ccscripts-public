package org.dreambot.behaviour.method.callisto.leavecallisto;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.callisto.CallistoData;
import org.dreambot.fractals.TickDecision;

public class SuicideLeaveCallisto extends TickDecision {
    final boolean enabled;

    public SuicideLeaveCallisto(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean evaluate() {
        if (!enabled) return false;
        NPC callisto = NPCs.closest(CallistoData.CALLISTO_NAME);
        if (callisto != null) {
            log("Killing self on callisto");
            if (callisto.distance() > 5) Walking.walk(callisto);
            if (!callisto.equals(Players.getLocal().getInteractingCharacter())) callisto.interact();
        }

        return false;
    }
}
