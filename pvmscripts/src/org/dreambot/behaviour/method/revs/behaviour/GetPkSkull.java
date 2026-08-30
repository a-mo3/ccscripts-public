package org.dreambot.behaviour.method.revs.behaviour;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetPkSkull extends Fractal {
    Area EMBLEM_TRADER = new Area(3089, 3506, 3102, 3502);

    public GetPkSkull(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            Dialog.solve("skull", "");
            return ReactionGenerator.getQuick();
        }

        if (!EMBLEM_TRADER.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(EMBLEM_TRADER);
            return ReactionGenerator.getNormal();
        }

        NPC trader = NPCs.closest(x -> x.hasAction("Skull"));
        if (trader == null) {
            Logger.info("Couldnt find emblem trader with skull action");
            return ReactionGenerator.getNormal();
        }

        trader.interact("Skull");
        Sleep.sleepUntil(() -> Players.getLocal().isSkulled(), 2400);
        return ReactionGenerator.getNormal();
    }
}
