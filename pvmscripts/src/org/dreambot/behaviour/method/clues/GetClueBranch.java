package org.dreambot.behaviour.method.clues;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.behaviour.method.puropuro.EnterPuroPuro;
import org.dreambot.behaviour.method.puropuro.PuroPuroHunt;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.LostCity;
import org.dreambot.behaviour.training.hunter.HunterBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.scriptdata.MediumClueSettings;
import org.dreambot.scriptdata.PuroPuroSettings;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;

import java.util.function.Supplier;

public class GetClueBranch extends Fractal {
    public GetClueBranch(Supplier<Boolean> acceptCondition, MediumClueSettings settings) {
        super(acceptCondition);
        setSimpleName("Get a clue scroll");
        addChildren(
                new Fractal(() -> settings.clueStrategy == GetClueStrategy.PURO_PURO && GetClueStrategy.PURO_PURO.isOwned())
                        .addChildren(
                                // open implings in inventory
                                new HunterBranch(() -> Skill.HUNTER.getLevel() < 50).setSimpleName("Hunter training"),
                                new LostCity().setSimpleName("Lost city"),
                                new MuleOff().setSimpleName("Mule Off")
//                                new EnterPuroPuro(4)
//                                new PuroPuroHunt(new PuroPuroSettings(false, 50, false, false,
//                                        false, false, true, true,
//                                        false, false))
                        )
                        .setSimpleName("pp mode")
                // steal from that chest in varlamore

                // just buy the shits

        );

    }
}
