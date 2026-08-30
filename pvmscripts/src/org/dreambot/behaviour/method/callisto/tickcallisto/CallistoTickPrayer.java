package org.dreambot.behaviour.method.callisto.tickcallisto;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.behaviour.method.callisto.CallistoData;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;

public class CallistoTickPrayer extends TickDecision {
    final boolean tickFlick;

    public CallistoTickPrayer(boolean tickFlick) {
        this.tickFlick = tickFlick;
        setSimpleName("Prayer");
    }

    @Override
    public boolean evaluate() {
        NPC callisto = NPCs.closest(CallistoData.CALLISTO_NAME);
        if (callisto == null) {
            log("No callisto, disable prayers");
            Prayers.toggleQuickPrayer(false);
            Arrays.stream(Prayer.values()).forEach(x -> {
                Prayers.toggle(false, x);
            });
            return false;
        }

        Projectile p = Projectiles.closest(133);
        if (p != null) {
            Entity e = p.getTargetEntity();
            if (Players.getLocal().equals(e) || p.getTargetTile().distance() < 2) {
                log("Mage attack pray");
                Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
                return false;
            }
        }

        if (callisto.getServerTile().translate(2, 2).distance(Players.getLocal().getServerTile()) < 5) {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
            return false;
        }

        if (tickFlick && Menu.isMenuManipulationActive()) {
            log("Flick prayers");
            Prayers.toggleQuickPrayer(false);
            Sleep.sleep(50);
            Prayers.toggleQuickPrayer(true);
        } else {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        }
        return false;
    }
}