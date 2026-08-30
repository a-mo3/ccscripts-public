package org.dreambot.behaviour.method.callisto;

import okhttp3.Call;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.comms.impl.callisto.CallistoClient;
import org.dreambot.comms.impl.vetion.VetionClient;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class InitCallistoConnection extends Fractal {
    public InitCallistoConnection() {
        super(() -> Client.isLoggedIn() && (!Combat.isInWild() && CallistoClient.getWorld() != Worlds.getCurrentWorld())); // todo or no team
    }

    @Override
    public int onLoop() {
        // todo reconnect
        CallistoClient c = CallistoClient.getInstance();
        if (c == null) {
            log("Failed to fetch client");
            return ReactionGenerator.getNormal();
        }

        if (!c.isOpen()) {
            log("Non open connected close and reopen");
            CallistoClient.closeConnection();
            return ReactionGenerator.getNormal();
        }

        if (c.getCallistoTeamState() == null) {
            log("No team yet assigned.");
            // todo request team (should have got one on open)
            c.requestTeam();
            return ReactionGenerator.getNormal();
        }

        if (c.getCallistoTeamState().getMembers() == null || c.getCallistoTeamState().getMembers().isEmpty()) {
            log("Request team empty member list");
            return ReactionGenerator.getNormal();
        }

        // only case left is not on the right world
        log("Get onto team world");
        WorldHopper.hopWorld(CallistoClient.getWorld());

        return ReactionGenerator.getNormal();
    }
}
