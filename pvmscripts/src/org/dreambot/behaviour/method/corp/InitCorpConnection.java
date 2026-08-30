package org.dreambot.behaviour.method.corp;

import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.fractals.Fractal;
import org.dreambot.scriptdata.CorpSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class InitCorpConnection extends Fractal {
    final CorpSettings settings;
    final boolean forceHost;

    public InitCorpConnection(CorpSettings settings, boolean forceHost) {
        super(() -> Client.isLoggedIn() && (!CorpClient.isInstantiated() || CorpClient.getRole() == null));
        this.settings = settings;
        this.forceHost = forceHost;
        setSimpleName("Get a corp connection");
    }

    @Override
    public int onLoop() {
        log("Getting corp instance");
        if (CorpClient.isInstantiated()) {
            log("Requesting update for " + AnalyticsReporter.hashStringSHA256(Players.getLocal().getName()));
            CorpClient.requestUpdate();
            return ReactionGenerator.getNormal();
        }
        CorpClient.getInstance(settings.teamSize, settings.specialForcesCount, settings.worldPreference, forceHost);
        return ReactionGenerator.getNormal();
    }
}
