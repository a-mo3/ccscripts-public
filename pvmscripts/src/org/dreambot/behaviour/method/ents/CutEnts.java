package org.dreambot.behaviour.method.ents;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CutEnts extends Fractal {
    Supplier<NPC> trunkSupplier;

    public CutEnts(Supplier<NPC> trunkSupplier) {
        super(() -> trunkSupplier.get() != null);
        this.trunkSupplier = trunkSupplier;
    }

    @Override
    public int onLoop() {
        NPC trunk = trunkSupplier.get();
        if (trunk == null) {
            Logger.info("Trunk is null");
            return ReactionGenerator.getQuick();
        }

        if (!trunk.equals(Players.getLocal().getInteractingCharacter())) {
            trunk.interact("Chop");
            Sleep.sleepUntil(() -> trunk.equals(Players.getLocal().getInteractingCharacter()), 2400);
        }
        return ReactionGenerator.getQuick();
    }
}
