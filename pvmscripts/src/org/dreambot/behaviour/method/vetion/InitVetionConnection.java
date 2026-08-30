package org.dreambot.behaviour.method.vetion;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.comms.impl.vetion.VetionClient;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class InitVetionConnection extends Fractal {
    public InitVetionConnection() {
        super(() -> Client.isLoggedIn() && (!Combat.isInWild() && VetionClient.getWorld() != Worlds.getCurrentWorld())); // todo or no team
    }

    @Override
    public int onLoop() {
        // todo reconnect
        VetionClient c = VetionClient.getInstance();
        if (c == null) {
            log("Failed to fetch client");
            return ReactionGenerator.getNormal();
        }

        if (c.getVetionTeamState() == null) {
            log("No team yet assigned.");
            // todo request team (should have got one on open)\
            return ReactionGenerator.getNormal();
        }

        if (c.getVetionTeamState().getMembers() == null || c.getVetionTeamState().getMembers().isEmpty()) {
            log("Request team empty member list");
            return ReactionGenerator.getNormal();
        }

        // only case left is not on the right world
        log("Get onto team world");
        WorldHopper.hopWorld(VetionClient.getWorld());

        return ReactionGenerator.getNormal();
    }
}
