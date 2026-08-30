package org.dreambot.behaviour.method.gwd.zilyana.tickzilyanafight;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.behaviour.method.gwd.zilyana.GetZilyanaKC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.scriptdata.ZilyanaSettings;

public class ZilyanaAntiCrash extends TickDecision {
    final ZilyanaSettings settings;

    public ZilyanaAntiCrash(ZilyanaSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean evaluate() {
        if (!settings.leaveWhenCrashed) {
            return false;
        }
        if (GetIntoZilyanaFight.ZILYANA_BOSS_ROOM.contains(Players.getLocal()) && GetZilyanaKC.getSaradominKC() < 40) {
            if (Players.closest(x -> !x.equals(Players.getLocal()) && GetIntoZilyanaFight.ZILYANA_BOSS_ROOM.contains(x)) != null) {
                log("We're being crashed, leave");
                KillZilyanaPotionDecision.exitToGE();
                return true;
            }
        }
        return false;
    }
}
