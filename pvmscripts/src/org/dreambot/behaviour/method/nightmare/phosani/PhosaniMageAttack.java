package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class PhosaniMageAttack extends Fractal {
    public static final List<Integer> INACTIVE_TOTEMS = Arrays.asList(9435, 9438, 9441, 9444);
    private static final List<Integer> ACTIVE_TOTEMS = Arrays.asList(9436, 9439, 9442, 9445);

    @Override
    public boolean isValid() {
        return NPCs.closest(x -> INACTIVE_TOTEMS.contains(x.getId())) != null;
    }

    @Override
    public int onLoop() {
        // mage switch
//        new EquipEvent(SettingsRepository.findInstanceOf(new PhosaniSettings()).loadout.getMageLoadout())
//                .executed();

        // attack the active pillar!
        NPC pillar = NPCs.closest(x -> INACTIVE_TOTEMS.contains(x.getId()));
        if (pillar == null) {
            Logger.error("pillar was null, magic attack phase");
            return ReactionGenerator.getQuick();
        }

        Character target = Players.getLocal().getInteractingCharacter();
        if (target == null || target.getId() != pillar.getId()) {
            pillar.interact("Charge");
            Sleep.sleepUntil(() -> {
                Character t = Players.getLocal().getInteractingCharacter();
                return t != null && t.getId() == pillar.getId();
            }, 800);
        }

        return ReactionGenerator.getQuick();
    }
}
