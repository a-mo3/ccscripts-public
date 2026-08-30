package org.dreambot.behaviour.misc.tickcombat.decisions;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PrayerUtils;

import java.util.function.BooleanSupplier;

public class TickGoToArea extends TickDecision {
    final Area area;
    final BooleanSupplier extra;

    public TickGoToArea(Area area, BooleanSupplier extra) {
        this.area = area;
        this.extra = extra;
        setSimpleName("Go to slayer task (add)");
    }

    public TickGoToArea(Area area) {
        this.area = area;
        this.extra = null;
        setSimpleName("Go to slayer task");
    }

    @Override
    public boolean evaluate() {
        if (extra != null && extra.getAsBoolean()) {
            log("Extra walk logic");
            return true;
        }

        if (area.contains(Players.getLocal())) {
            return false;
        }

        log("Go to area");
        PrayerUtils.disableAll();
        if (Walking.walk(area)) Walking.walk(area);
        return true;
    }
}
