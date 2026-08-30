package org.dreambot.behaviour.method.venenatis;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.comms.impl.callisto.CallistoClient;
import org.dreambot.comms.impl.venenatis.VenenatisClient;
import org.dreambot.comms.impl.venenatis.VenenatisComms;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class InitVenenatisConnection extends Fractal {
    public InitVenenatisConnection() {
        super(() -> Client.isLoggedIn() && (!Combat.isInWild() && VenenatisClient.getWorld() != Worlds.getCurrentWorld())); // todo or no team
    }

    @Override
    public int onLoop() {
        // todo reconnect
        VenenatisClient c = VenenatisClient.getInstance();
        if (c == null) {
            log("Failed to fetch client");
            return ReactionGenerator.getNormal();
        }

        if (c.getVenenatisTeamState() == null) {
            log("No team yet assigned.");
            // todo request team (should have got one on open)
            c.requestTeam();
            return ReactionGenerator.getNormal();
        }

        if (c.getVenenatisTeamState().getMembers() == null || c.getVenenatisTeamState().getMembers().isEmpty()) {
            log("Request team empty member list");
            return ReactionGenerator.getNormal();
        }

        // only case left is not on the right world
        log("Get onto team world");
        WorldHopper.hopWorld(VenenatisClient.getWorld());

        return ReactionGenerator.getNormal();
    }
}
