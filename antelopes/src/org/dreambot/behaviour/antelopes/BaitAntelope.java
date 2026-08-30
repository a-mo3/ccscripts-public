package org.dreambot.behaviour.antelopes;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class BaitAntelope extends Fractal {
    public BaitAntelope() {
    }

    @Override
    public boolean isValid() {
        return GameObjects.closest("Spiked pit") != null;
    }

    @Override
    public int onLoop() {
        Character antelope = Players.getLocal().getCharacterInteractingWithMe();

        if (antelope == null || antelope.distance() > 5) {
            NPC antelopeNpc = NPCs.closest(x -> x.getName().equals("Moonlight antelope") && x.getInteractingCharacter() == null);
            Logger.info("Interacting with " + antelopeNpc);
            if (antelopeNpc != null && antelopeNpc.interact("Tease")) {
                return ReactionGenerator.getNormal() + 300;
            }
            return ReactionGenerator.getNormal() + 300;
        }

        if (antelope.hasAction("Dismiss")) {
            antelope.interact("Dismiss");
            Logger.info("Dismissing random");
            Sleep.sleepUntil(() -> !antelope.exists(), 2400);
            return ReactionGenerator.getNormal();
        }

        if (antelope instanceof Player) {
            Logger.warn("We are being fucked with");
            return ReactionGenerator.getNormal();
        }

        GameObject trap = GameObjects.closest("Spiked pit");
        if (trap != null && trap.interact("Jump")) {
            Sleep.sleepUntil(() -> GameObjects.closest("Collapsed trap") != null, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
