package org.dreambot.behaviour.method.gwd.zammy.melee;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.zammy.ZammyCounters;
import org.dreambot.fractals.TickDecision;

/**
 * if zammy will attack before us we stand under him to delay it
 *
 */
public class TickZammyAttack extends TickDecision {
    public TickZammyAttack() {
        log("Attack");
    }

    @Override
    public boolean evaluate() {
        NPC zam = NPCs.closest(ZammyCounters.ZAMMY_NAME);
        if (zam == null) return false;

        if (ZammyCounters.zamCounter < ZammyCounters.ourCounter) {
            log("Zam will attack before us, stand under");
            Walking.walkExact(zam.getTile());
            return true;
        }

        // Attack Zammy
        Character target = Players.getLocal().getInteractingCharacter();
        if (!zam.equals(target)) {
            zam.interact("Attack");
        }
        return true;
    }
}
