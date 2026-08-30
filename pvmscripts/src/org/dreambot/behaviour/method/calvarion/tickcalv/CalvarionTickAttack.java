package org.dreambot.behaviour.method.calvarion.tickcalv;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.AntiCrashWildyBosses;
import org.dreambot.fractals.TickDecision;

public class CalvarionTickAttack extends TickDecision {
    public CalvarionTickAttack() {
        setSimpleName("Calvarion attack");
    }

    @Override
    public boolean evaluate() {
        NPC hound = NPCs.closest(x -> x.getName().contains("hound"));
        Character target = Players.getLocal().getInteractingCharacter();
        if (target != null) {
            // check if hound or calvarion.
            String tgtName = target.getName();
            if (tgtName == null) {
                log("Non null target with a null name?");
                return true;
            }

            if ("Calvar'ion".equals(tgtName)) {
                log("Targeting Calvarion");
                if (hound != null) {
                    log("Attack a hound " + hound);
                    hound.interact("Attack");
                    return true;
                }
                return true;
            }

            if (tgtName.toLowerCase().contains("hound")) {
                log("Targeting hound");
                return true;
            }
        }

        // attack
        NPC calv = NPCs.closest("Calvar'ion");

        if (hound != null) {
            log("Attack a hound " + hound);
            hound.interact("Attack");
            return true;
        }

        if (calv != null) {
            // check if someone else is hitting calv
            Character fightingCalv = calv.getCharacterInteractingWithMe();
            if (fightingCalv != null && !fightingCalv.equals(Players.getLocal())) {
                log("Someone else is attacking calvarion");
                // todo we could try to crash someone for like X ticks
                AntiCrashWildyBosses.hasToLeave = true;
                return true;
            }

            if (Combat.getSpecialPercentage() >= 50) {
                Combat.toggleSpecialAttack(true);
            }

            calv.interact("Attack");
            return true;
        }

        return false;
    }
}
