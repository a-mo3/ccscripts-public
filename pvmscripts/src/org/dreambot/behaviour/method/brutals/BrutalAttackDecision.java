package org.dreambot.behaviour.method.brutals;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;

import java.util.function.Supplier;

public class BrutalAttackDecision extends TickDecision {
    final Supplier<NPC> npcSupplier;
    final Tile safeSpot;

    public BrutalAttackDecision(Supplier<NPC> npcSupplier, Tile safeSpot) {
        this.npcSupplier = npcSupplier;
        this.safeSpot = safeSpot;
    }

    @Override
    public boolean evaluate() {
        if (!safeSpot.equals(Players.getLocal().getServerTile())) {
            log("Get on safespot");
            Walking.walkExact(safeSpot);
            return true;
        }

        // here we assume is npc supplier does the appropriate distance checks
        NPC dragon = npcSupplier.get();
        Character tgt = Players.getLocal().getInteractingCharacter();
        if (dragon != null && tgt != null && !dragon.equals(tgt)) {
            dragon.interact("Attack");
        }
        return true;
    }
}
