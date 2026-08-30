package org.dreambot.behaviour.method.sarachnis;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;

public class SarachnisFightDecision extends TickDecision {
    public SarachnisFightDecision() {
        setSimpleName("Sarachnis fight");
    }

    @Override
    public boolean evaluate() {
        NPC sarachnis = NPCs.closest("Sarachnis");
        if (sarachnis == null) {
            log("No sarachnis found");
        }



        return false;
    }
}
