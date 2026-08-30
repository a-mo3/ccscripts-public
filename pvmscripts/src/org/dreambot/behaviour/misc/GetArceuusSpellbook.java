package org.dreambot.behaviour.misc;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Spellbook;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class GetArceuusSpellbook extends Fractal {
    Area ARC_ALTAR = new Area(1710, 3888, 1719, 3879);

    public GetArceuusSpellbook() {
        super(() -> Magic.getSpellbook() != Spellbook.ARCEUUS);
        setSimpleName("Get Arceuus spellbook");
    }

    @Override
    public int onLoop() {
        if (!ARC_ALTAR.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(ARC_ALTAR);
            return ReactionGenerator.getNormal();
        }

        NPC tyss = NPCs.closest("Tyss");
        if (tyss != null) {
            Logger.info("Change spellbook");
            tyss.interact("Spellbook");
        }
        return ReactionGenerator.getNormal();
    }
}
