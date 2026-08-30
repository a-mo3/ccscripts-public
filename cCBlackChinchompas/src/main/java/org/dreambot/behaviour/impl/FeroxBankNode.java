package org.dreambot.behaviour.impl;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.config.Config;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import static org.dreambot.behaviour.BehaviourUtils.FEROX_BANK;
import static org.dreambot.behaviour.BehaviourUtils.stdWalk;

public class FeroxBankNode extends Fractal {
    private final Config config = Config.getConfig();
    @Override
    public boolean isValid() {
        return Inventory.count("Black chinchompa") >= ScriptSettings.getChinMax();
    }

    @Override
    public int onLoop() {
        if (!FEROX_BANK.contains(Players.getLocal())) {
            stdWalk(FEROX_BANK);
            return ReactionGenerator.getNormal();
        }
        Filter<Item> brews = x -> x.getName().toLowerCase().contains("saradomin brew");
        if (Bank.open()) {
            if (Inventory.count(brews) < 5 && Bank.count(brews) <= 5) {
                Bank.withdraw(brews, 5);
                return ReactionGenerator.getNormal();
            }

            if (Inventory.count("box trap") < 10 && Bank.contains("box trap")) {
                Bank.withdraw("box trap", 10 - Inventory.count("box trap"));
                return ReactionGenerator.getNormal();
            }
            if (Inventory.contains("black chinchompa")) {
                Bank.depositAll("black chinchompa");
                return ReactionGenerator.getNormal();
            }
        }
        return ReactionGenerator.getNormal();
    }
}
