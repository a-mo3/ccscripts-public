package org.dreambot.behaviour.method.sarachnis;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PVMUtil;

import java.util.Arrays;

public class SarachnisPrayerDecision extends TickDecision {
    public SarachnisPrayerDecision() {
        setSimpleName("Sarachnis prayer");
    }

    @Override
    public boolean evaluate() {
        NPC sarachnis = NPCs.closest("Sarachnis");
        if (sarachnis == null) {
            log("No sarachnis found - disable prayers");
            for (Prayer value : Prayer.values()) {
                Prayers.toggle(false, value);
            }
            return false;
        }
        if (Skill.PRAYER.getBoostedLevel() == 0) {
            log("No prayer level");
            return false;
        }

        boolean inMeleeDist = Arrays.stream(PVMUtil.attackableTiles(sarachnis, 3)).anyMatch(x -> x.equals(Players.getLocal().getTile()));
        if (inMeleeDist) {
            // use quick prayer here because if we're melee we need the boost, if praying ranged we arent dps'ing
            log("Flick qp");
            Prayers.toggleQuickPrayer(false);
            Sleep.sleep(50);
            Prayers.toggleQuickPrayer(true);
        } else {
            log("Flick range");
            Prayers.toggle(false, PVMUtil.getBestMeleePray());
            Prayers.toggle(false, Prayer.PROTECT_FROM_MISSILES);
            Sleep.sleep(50);
            Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
        }

        return false;
    }
}
