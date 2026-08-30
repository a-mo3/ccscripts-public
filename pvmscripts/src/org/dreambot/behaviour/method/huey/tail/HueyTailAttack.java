package org.dreambot.behaviour.method.huey.tail;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;

public class HueyTailAttack extends TickDecision {

    public static final int HUEYCOATL_BODY = 14017;

    @Override
    public boolean evaluate() {
        return hitBody();
    }

    private boolean hitBody() {
        log("Fight body");
        Character tgt = Players.getLocal().getInteractingCharacter();
        NPC closestBody = NPCs.closest(x -> x.getId() == HUEYCOATL_BODY, Players.getLocal().getServerTile());
        if (closestBody == null) {
            log("Failed to find body");
            return true;
        }
        if (tgt == null || !tgt.equals(closestBody)) {
            log("Attack body");
            closestBody.interact("Attack");
        }
        return true;
    }
}
