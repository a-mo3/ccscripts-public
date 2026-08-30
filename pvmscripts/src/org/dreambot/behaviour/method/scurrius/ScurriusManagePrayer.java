package org.dreambot.behaviour.method.scurrius;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PrayerUtils;

public class ScurriusManagePrayer extends TickDecision {
    final boolean tickFlick;

    final ScurriusMode mode;

    public ScurriusManagePrayer(boolean tickFlick, ScurriusMode mode) {
        this.tickFlick = tickFlick;
        setSimpleName("Scurr prayer");
        this.mode = mode;
    }

    public static final int RANGE_PROJ = 2642;
    public static final int MAGIC_PROJ = 2640;

    @Override
    public boolean evaluate() {
        if (Skill.PRAYER.getBoostedLevel() ==0) return false;
        NPC scurrius = NPCs.closest("Scurrius");
        if (scurrius == null && !Players.getLocal().isInCombat()) {
            log("Not in combat disable all prayer");
            PrayerUtils.disable(Prayer.values());
            return false;
        }


        // if one of the projects is spawned we have to use mage/range pray
        if (Projectiles.closest(RANGE_PROJ) != null) {
            log("Range proj pray");
            PrayerUtils.toggle(false, Prayer.PROTECT_FROM_MISSILES);
            PrayerUtils.toggle(false, mode.boostPrayerSupplier.get());
            Sleep.sleep(50);
            Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);

            return false;
        }

        // otherwise melee pray and flick
        if (Projectiles.closest(MAGIC_PROJ) != null) {
            log("mage proj pray");
            PrayerUtils.toggle(false, Prayer.PROTECT_FROM_MAGIC);
            PrayerUtils.toggle(false, mode.boostPrayerSupplier.get());
            Sleep.sleep(50);
            PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MAGIC);
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
