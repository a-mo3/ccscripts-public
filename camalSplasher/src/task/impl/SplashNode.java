package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import task.AbstractTask;

public class SplashNode extends AbstractTask {
    @Override
    public boolean accept() {
        return config.isInitialised();
    }

    @Override
    public int execute() {
        if (!Magic.canCast(config.getBestSpell())) {
            config.setInitialised(false);
            return 1;
        }
        if (Magic.setAutocastSpell(config.getBestSpell())) {
            if (Combat.toggleAutoRetaliate(true)) {
                config.setStatus("Splashing!");

                if (SEAGULLS.contains(Players.getLocal())) {
                    // splashing code
                    NPC seagull = NPCs.closest(s -> s != null && s.getName().equals("Seagull") && !s.isInCombat() && !s.isHealthBarVisible());
                    if (seagull != null) {
                        if (!Players.getLocal().isInCombat()) {
                            seagull.interact("Attack");
                        } else {
                            if (Calculations.random(1, 20) == 3) {
                                Logger.log("clicking...");
                                Character gullImFightin = Players.getLocal().getCharacterInteractingWithMe();
                                if (gullImFightin != null && gullImFightin.interact("Attack")) {
                                    Sleep.sleepUntil(() -> !Players.getLocal().isInCombat(), Players.getLocal()::isAnimating, 6000, 100);
                                }
                            }
                        }
                    }
                } else {
                    // running code
                    config.setStatus("going to seagulls");

                    if (!Walking.isRunEnabled()) {
                        if (Walking.getRunEnergy() > Calculations.random(18, 32)) {
                            Walking.toggleRun();
                        }
                    }
                    if (Walking.shouldWalk()) {
                        Walking.walk(SEAGULLS.getRandomTile());
                    }
                }
            }
        }
        return 1000;
    }
}
