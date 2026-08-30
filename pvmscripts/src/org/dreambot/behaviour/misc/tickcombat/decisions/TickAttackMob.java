package org.dreambot.behaviour.misc.tickcombat.decisions;

import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;

import java.util.function.BooleanSupplier;

public class TickAttackMob extends TickDecision {
    final Filter<NPC> mobFilter;
    final BooleanSupplier extra;

    public TickAttackMob(Filter<NPC> mobSupplier, BooleanSupplier extraFightLogic) {
        this.extra = extraFightLogic;
        this.mobFilter = mobSupplier;
        setSimpleName("Attack");
    }

    @Override
    public boolean evaluate() {
        if (extra != null) {
            if (extra.getAsBoolean()) return true;
        }

        Character currentTarget = Players.getLocal().getInteractingCharacter();
        if (currentTarget != null) {
            log("Current target non null " + currentTarget);
            return false;
        }

        if (mobFilter == null) {
            log("Mob filter null");
            return false;
        }

        // get something attacking you that matches the filter
        NPC attackingMe = (NPC) Players.getLocal().getCharactersInteractingWithMe().stream()
                .filter(x -> x instanceof NPC)
                .filter(x -> mobFilter.match((NPC) x))
                .findFirst().orElse(null);

        if (attackingMe != null) {
            log("No target but a matching mob is attacking us, hit back");
            attackingMe.interact();
            return false;
        }

        NPC npc = NPCs.closest(mobFilter);
        if (npc == null) {
            log("Failed to fetch target");
            return false;
        }
        log("Interact with npc " + npc);
        npc.interact();


        return false;
    }
}
