package org.dreambot.behaviour.method.barrows.handlecrypt.decisions;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.barrows.BarrowsBrother;
import org.dreambot.behaviour.method.barrows.BarrowsVarbits;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.NpcID;

import java.util.Arrays;
import java.util.List;

public class GetBarrowsPoints extends TickDecision {
    public static final int BLOODWORM = 1678;
    public static final int SKELETON_1685 = 1685;
    public static final int GIANT_CRYPT_SPIDER = 1684;
    private final List<Integer> CRYPT_MOBS = Arrays.asList(
            BLOODWORM,
            SKELETON_1685,
            GIANT_CRYPT_SPIDER
    );

    @Override
    public boolean evaluate() {
        int rewardPot = PlayerSettings.getBitValue(BarrowsVarbits.BARROWS_REWARD_POTENTIAL);
        int deadBrotherPot = Arrays.stream(BarrowsBrother.values())
                .filter(x -> !x.hasKilled())
                .map(x -> x.combatLevel)
                .findFirst()
                .orElse(0);
        if (rewardPot + deadBrotherPot >= 750) {
            log("Reached pot " + rewardPot + " Remaining from brothers " + deadBrotherPot);
            return false;
        }

        log("Need pot " + rewardPot);
        NPC pointsMob = NPCs.closest(x -> CRYPT_MOBS.contains(x.getId()) && x.canReach());
        if (pointsMob != null) {
            // todo pets will fuck this uo
            Character tgt = Players.getLocal().getInteractingCharacter();
            Character attackingMe = NPCs.closest(x -> x.isInteracting(Players.getLocal()));
            if (attackingMe != null && attackingMe.canReach()) {
                log("Already under attack points mob");
                if (!attackingMe.equals(tgt)) {
                    log("Attack back");
                    attackingMe.interact();
                }
                return true;
            }

            log("Found points mob: " + pointsMob);
            if (tgt == null || !tgt.equals(pointsMob)) {
                pointsMob.interact("Attack");
            }
            return true;
        } else {
            log("No points mob");
            return false;
        }
    }
}
