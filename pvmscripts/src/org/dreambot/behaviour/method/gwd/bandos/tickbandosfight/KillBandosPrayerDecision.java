package org.dreambot.behaviour.method.gwd.bandos.tickbandosfight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.bandos.BandosConsts;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PrayerUtils;

public class KillBandosPrayerDecision extends TickDecision implements AnimationListener {
    public KillBandosPrayerDecision() {
        setSimpleName("Bandos Prayer choice");
        Client.getInstance().addEventListener(this);
    }

    // animation when bree fires bow
    public static final int BREE_ANI_ID = 7026;
    // growlers mage atk
    public static final int GROWLER_ANI_ID = 7037;

    public static int rangeMinionTiming = 0;
    public static int magicMinionTiming = 0;

    @Override
    public boolean evaluate() {
        int currentCycle = Client.getGameTick() % 5;

        if (rangeMinionTiming == magicMinionTiming || magicMinionTiming != currentCycle) {
            log("Flick range");
            Prayers.toggleQuickPrayer(false);
            Sleep.sleep(50);
            Prayers.toggleQuickPrayer(true);
            return false;
        }

        log("Prot magic");
        PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MAGIC);
        return false;
    }

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        String name = npc.getName();
        if (BandosConsts.MAGIC_MINION_NAME.equals(name)) {
            log("Magic guard animated " + animation);
            if (animation == BandosConsts.MAGIC_ATTACK_ANIMATION) {
                int tick = Client.getGameTick() % 5;
                log("magic shot shot " + tick);
                magicMinionTiming = tick;
            }
            return;
        }

        if (name.equals("Growler")) {
            log("Range guard animated " + animation);
            if (animation == BandosConsts.RANGE_ATTACK_ANIMATION) {
                int tick = Client.getGameTick() % 5;
                log("range shot " + tick);
                rangeMinionTiming = tick;
            }
        }
    }
}
