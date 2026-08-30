package org.dreambot.behaviour.method.venenatis.tickvenenatis;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.behaviour.method.venenatis.VenenatisData;
import org.dreambot.comms.impl.venenatis.VenenatisComms;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;

public class VenenatisTickPrayer extends TickDecision {
    final boolean tickFlick;

    public VenenatisTickPrayer(boolean tickFlick) {
        this.tickFlick = tickFlick;
        setSimpleName("Prayer");
    }

    @Override
    public boolean evaluate() {
        NPC venenatis = NPCs.closest(VenenatisData.VENENATIS_NAME);
        if (venenatis == null) {
            log("No venenatis, disable prayers");
            Prayers.toggleQuickPrayer(false);
            Arrays.stream(Prayer.values()).forEach(x -> Prayers.toggle(false, x));
            return false;
        }


        if (venenatis.getServerTile().translate(2, 2).distance(Players.getLocal().getServerTile()) > 4) {
            Prayers.toggle(false, TickVenenatisBranch.getVenenatisAttackStyle() == Skill.RANGED ? Prayer.PROTECT_FROM_MISSILES : Prayer.PROTECT_FROM_MAGIC);
            Sleep.sleep(50);
            Prayers.toggle(true, TickVenenatisBranch.getVenenatisAttackStyle() == Skill.RANGED ? Prayer.PROTECT_FROM_MISSILES : Prayer.PROTECT_FROM_MAGIC);
            return false;
        }

        if (tickFlick && Menu.isMenuManipulationActive()) {
            Prayers.toggleQuickPrayer(false);
            Sleep.sleep(50);
            Prayers.toggleQuickPrayer(true);
        } else {
            Prayers.toggleQuickPrayer(true);
        }
        return false;
    }
}