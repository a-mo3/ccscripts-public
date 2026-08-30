package org.dreambot.behaviour.training.agility.wild;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.clan.chat.ClanChat;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.training.hunter.BlackChinAntiPkNode;
import org.dreambot.fractals.TickDecision;

import java.util.HashSet;

/**
 * attack anyone skulled that is not in our clan
 */
public class TickClanRag extends TickDecision {
    final WildernessAgilityMode mode;
    final Timer refresh = new Timer(15 * 1000);
    public static final HashSet<String> clanMembers = new HashSet<>();
    final BHAgressionMode agressionMode;

    public TickClanRag(WildernessAgilityMode mode, BHAgressionMode agressionMode) {
        this.mode = mode;
        this.agressionMode = agressionMode;
    }


    public static int counter;

    @Override
    public boolean evaluate() {
        if (mode != WildernessAgilityMode.BH_RAG_WORLD) return false;
        if (counter > 0) counter--;

        if (refresh.finished()) {
            refresh.reset();
            ClanChat.getMembers().forEach(x -> clanMembers.add(x.getName()));
        }

        Player ragTarget = Players.closest(agressionMode.getFilter());
        if (ragTarget != null && !Players.getLocal().isAnimating() && counter <= 0) {
            // if we are able to attack (are not on cooldown)
            // random chance attacking the rag target, so that every account doesnt sync up trying to attack them
            log("Rag target " + ragTarget);
            int roll = Calculations.random(5);
            log("Roll " + roll);
            if (roll == 1) {
                log("Attacking");
                ragTarget.interact("Attack");
                Sleep.sleepUntil(() -> Players.getLocal().getAnimation() == 7552, 1200);
                counter = 4;
                return true;
            }
        }
        return false;
    }
}
