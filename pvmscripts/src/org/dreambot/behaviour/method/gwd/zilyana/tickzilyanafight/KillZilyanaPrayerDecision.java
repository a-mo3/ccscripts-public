package org.dreambot.behaviour.method.gwd.zilyana.tickzilyanafight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PrayerUtils;

public class KillZilyanaPrayerDecision extends TickDecision implements AnimationListener {
    public KillZilyanaPrayerDecision() {
        setSimpleName("Zil Prayer choice");
        Client.getInstance().addEventListener(this);
    }

    // animation when bree fires bow
    public static final int BREE_ANI_ID = 7026;
    // growlers mage atk
    public static final int GROWLER_ANI_ID = 7037;

    public static int breeTickTiming = 0;
    public static int growlerTickTiming = 0;

    @Override
    public boolean evaluate() {
        int currentCycle = Client.getGameTick() % 5;
        // if its not bree cycle, or both are sync'd, just
        log("Prayer flick " + breeTickTiming + " " + growlerTickTiming);
        if (currentCycle != breeTickTiming || growlerTickTiming == breeTickTiming) {
            Prayers.toggleQuickPrayer(false);
            Sleep.sleep(50);
            Prayers.toggleQuickPrayer(true);
            return false;
        }

        log("Bree cycle pray ranged");
        PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MISSILES);
        return false;
    }

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        String name = npc.getName();
        if (name.equals("Bree")) {
            log("Bree animated " + animation);
            if (animation == BREE_ANI_ID) {
                int tick = Client.getGameTick() % 5;
                log("Bree shot " + tick);
                breeTickTiming = tick;
            }
            return;
        }

        if (name.equals("Growler")) {
            log("Growler animated " + animation);
            if (animation == GROWLER_ANI_ID) {
                int tick = Client.getGameTick() % 5;
                log("Growler shot " + tick);
                growlerTickTiming = tick;
            }
        }
    }
}
