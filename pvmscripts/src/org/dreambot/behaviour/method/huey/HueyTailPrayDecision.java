package org.dreambot.behaviour.method.huey;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PrayerUtils;

public class HueyTailPrayDecision extends TickDecision {
    final HueyLoadout mode;
    final boolean safePray;
    public static final int RANGE_PROJ = 2972;
    public static final int MELEE_PROJ = 2969;
    public static final int MAGE_PROJ = 2975;

    public HueyTailPrayDecision(HueyLoadout mode, boolean safePray) {
        this.mode = mode;
        this.safePray = safePray;
    }

    @Override
    public boolean evaluate() {
        Projectile p = Projectiles.closest(RANGE_PROJ);
        if (shouldPray(p)) {
            log("Range proj " + p.getEndCycle());
            log(p.getEndCycle() + " " + Client.getGameCycle() + " " + (p.getEndCycle() - Client.getGameCycle()));
            if (!safePray) PrayerUtils.disableAll();
            Sleep.sleep(50);
            PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MISSILES);
            return false;
        }

        p = Projectiles.closest(MELEE_PROJ);
        if (shouldPray(p)) {
            log("Melee proj " + p.getEndCycle());
            log(p.getEndCycle() + " " + Client.getGameCycle() + " " + (p.getEndCycle() - Client.getGameCycle()));
            if (!safePray) PrayerUtils.disableAll();
            Sleep.sleep(50);
            PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MELEE);
            return false;
        }

        p = Projectiles.closest(MAGE_PROJ);
        if (shouldPray(p)) {
            log("Mage proj ");
            log(p.getEndCycle() + " " + Client.getGameCycle() + " " + (p.getEndCycle() - Client.getGameCycle()));
            if (!safePray) PrayerUtils.disableAll();
            Sleep.sleep(50);
            PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            return false;
        }

        PrayerUtils.disableAll();
        Sleep.sleep(50);
        if (mode.mode != Skill.MAGIC) {
            PrayerUtils.toggle(true, PVMUtil.getBestMeleePray());
        } else {
            PrayerUtils.toggle(true, PVMUtil.getBestMagePray());
        }
        return false;
    }

    boolean shouldPray(Projectile p) {
        if (p == null) return false;
        return true;
    }
}
