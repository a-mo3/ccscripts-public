package org.dreambot.behaviour.method.gwd.bandos.tickbandosfight;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.fractals.TickDecision;
import org.dreambot.scriptdata.BandosSettings;
import org.dreambot.scriptdata.ZilyanaSettings;

public class BandosAntiCrash extends TickDecision {
    final BandosSettings settings;

    public BandosAntiCrash(BandosSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean evaluate() {
        if (!settings.leaveWhenCrashed) {
            return false;
        }
        if (GetIntoBandosFight.BANDOS_ROOM.contains(Players.getLocal())) {
            if (Players.closest(x -> !x.equals(Players.getLocal()) && GetIntoBandosFight.BANDOS_ROOM.contains(x)) != null) {
                log("We're being crashed, leave");
                KillBandosPotionDecision.exitToGE();
                return true;
            }
        }
        return false;
    }
}
