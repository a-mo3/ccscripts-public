package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class PhosaniMeleeAttack extends Fractal {
    @Override
    public int onLoop() {
//        new EquipEvent(SettingsRepository.findInstanceOf(new PhosaniSettings()).loadout.getEquipmentLoadout())
//                .executed();


        NPC nightmare = NPCs.closest("Phosani's Nightmare");
        if (nightmare == null) {
            Logger.error("failed to find phosanis nightmare to attack");
            return ReactionGenerator.getQuick();
        }


        Character target = Players.getLocal().getInteractingCharacter();
        if (target == null || !target.getName().contains("Nightmare")) {
            nightmare.interact("Attack");
        }

        return super.onLoop();
    }
}
