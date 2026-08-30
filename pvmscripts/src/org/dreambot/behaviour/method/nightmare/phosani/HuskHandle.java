package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class HuskHandle extends Fractal {
    public HuskHandle(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        // todo ensure melee gear is switched to


        // this was a list of husk and parasite on lost
        NPC npc = NPCs.closest("Husk");
        Character target = Players.getLocal().getInteractingCharacter();
        if (npc != null && (target == null || "Husk".contains(target.getName()))) {
            npc.interact("Attack");
        }
        return ReactionGenerator.getNormal();
    }
}
