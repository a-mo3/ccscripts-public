package org.dreambot.behaviour.method.venenatis.leaveveneatis;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.util.CombatUtil;

public class TickLeaveVenenatisDecision extends TickDecision {
    final Area ESCAPE_CAVE = new Area(3325, 10301, 3392, 10242);

    public TickLeaveVenenatisDecision() {
        CombatUtil.get(); // manages teleblocked state
    }

    @Override
    public boolean evaluate() {
        if (!Combat.isInWild()) {
            Logger.info("Bank all");
            new BankAllInventoryEvent().execute();
            return true;
        }
        if (ESCAPE_CAVE.contains(Players.getLocal())) Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
        if (Walking.shouldWalk()) Walking.walk(BankLocation.EDGEVILLE);
        return true;
    }
}
