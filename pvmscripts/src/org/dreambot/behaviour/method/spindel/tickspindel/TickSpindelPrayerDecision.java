package org.dreambot.behaviour.method.spindel.tickspindel;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.SpindelPhase;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PrayerUtils;

import java.util.Arrays;

public class TickSpindelPrayerDecision extends TickDecision {
    @Override
    public boolean evaluate() {
        SpindelPhase currentPhase = SpindelState.getCurrentPhase();
        log("Current phase is " + currentPhase);
        Prayer desiredPray = (currentPhase == SpindelPhase.RANGE_BENIGN || currentPhase == SpindelPhase.RANGE_SPECIAL) ? Prayer.PROTECT_FROM_MISSILES : Prayer.PROTECT_FROM_MAGIC;
        log("Desired pray is " + desiredPray);

        // distance check for melee or ranged prayer
        NPC spindel = NPCs.closest("Spindel");
        if (spindel == null) {
            log("Cant pray, cant find spindel");
            return false;
        }

        if (Arrays.stream(PVMUtil.attackableTiles(spindel, 3)).anyMatch(x -> x.equals(Players.getLocal().getTile()))) {
            log("Near spindel, melee prayer");
            desiredPray = Prayer.PROTECT_FROM_MELEE;
        }

        // flick desired prayer
        PrayerUtils.toggle(false, desiredPray);
        Sleep.sleep(50);
        PrayerUtils.toggle(true, desiredPray);
        return false;
    }
}
