package org.dreambot.behaviour.training.agility.wild;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class WildyCourseExit extends Fractal {
    final WildernessAgilityMode mode;

    public WildyCourseExit(Supplier<Boolean> acceptCondition, WildernessAgilityMode mode) {
        super(acceptCondition);
        this.mode = mode;
    }

    @Override
    public int onLoop() {
        // get to safe space, and hop to random world1
        if (mode == WildernessAgilityMode.SUICIDE) {
            if (Skill.HITPOINTS.getBoostedLevel() <= 4) {
                log("Kill self");
                Inventory.interact(ItemID.ARAXYTE_VENOM_SACK);
            } else {
                Inventory.interact(ItemID.DWARVEN_ROCK_CAKE_7510, "Guzzle");
            }

        }

        if (Combat.isInWild()) {
            log("Go mage bank");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.MAGE_BANK);
            return ReactionGenerator.getNormal();
        }

        if (!Bank.isOpen()) {
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        } else {
            Logger.info("Emptying looting bags");
            new EmptyLootingBagEvent().executed();
        }

        return ReactionGenerator.getNormal();
    }
}
