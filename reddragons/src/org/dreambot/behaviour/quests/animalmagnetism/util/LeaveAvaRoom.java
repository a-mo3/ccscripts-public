package org.dreambot.behaviour.quests.animalmagnetism.util;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class LeaveAvaRoom extends Fractal {
    public LeaveAvaRoom() {
        super(() -> SpecialWalker.INSIDE_AVAS_ROOM.contains(Players.getLocal()));
    }

    @Override
    public int onLoop() {
        SpecialWalker.leaveAvasRoom();
        return ReactionGenerator.getNormal();
    }
}
