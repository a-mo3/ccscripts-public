package org.dreambot.behaviour.method.antipk;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.HashMap;
import java.util.Map;

public class AntiPkEating extends Fractal {
    Timer lock = new Timer(800);
    // food id | how much it heals
    Map<Integer, Integer> foodHealMap = new HashMap<>();

    public AntiPkEating() {
        foodHealMap.put(ItemID.JUG_OF_WINE, 12);
        foodHealMap.put(ItemID.LOBSTER, 12);
        foodHealMap.put(ItemID.BLIGHTED_MANTA_RAY, 22);
        foodHealMap.put(ItemID.SARADOMIN_BREW1, 12); // this is variable based on level but 12 in a good min
        foodHealMap.put(ItemID.SARADOMIN_BREW2, 12); // this is variable based on level but 12 in a good min
        foodHealMap.put(ItemID.SARADOMIN_BREW3, 12); // this is variable based on level but 12 in a good min
        foodHealMap.put(ItemID.SARADOMIN_BREW4, 12); // this is variable based on level but 12 in a good min
        foodHealMap.put(ItemID.BLIGHTED_KARAMBWAN, 18);
    }

    @Override
    public boolean isValid() {
        return foodHealMap.entrySet().stream().anyMatch(x -> Inventory.contains(x.getKey())) && healthMissing() > 22;
    }

    @Override
    public int onLoop() {
        // drink brew if its the last option, or you dont have a defence boost
        if (Skills.getRealLevel(Skill.DEFENCE) == Skills.getBoostedLevel(Skill.DEFENCE)) {
            Item brew = ItemVariants.SARADOMIN_BREW.getItem();
            if (brew != null) {
                Logger.info("Brewing up - def bonus");
                brew.interact("Drink");
                lock.reset();
                return ReactionGenerator.getQuick();
            }
        }

        // combo eat
        if (healthMissing() >= 30 && Inventory.contains(ItemID.BLIGHTED_KARAMBWAN)) {
            foodHealMap.entrySet().stream()
                    .filter(i -> i.getKey() != ItemID.BLIGHTED_KARAMBWAN)
                    .filter(i -> Inventory.contains(i.getKey()))
                    .filter(i -> healthMissing() >= i.getValue())
                    .findFirst()
                    .ifPresent(x -> Inventory.interact(x.getKey()) // i think eat option will always be first
                    );
            Sleep.sleep(200);
            Inventory.interact(ItemID.BLIGHTED_KARAMBWAN);
            lock.reset();
            return ReactionGenerator.getQuick();
        }

        // just eat the most appropriate food
        foodHealMap.entrySet().stream()
                .filter(i -> Inventory.contains(i.getKey()))
                .filter(i -> healthMissing() >= i.getValue())
                .findFirst()
                .ifPresent(x -> Inventory.interact(x.getKey()) // i think eat option will always be first
                );
        lock.reset();
        return ReactionGenerator.getQuick();
    }

    private int healthMissing() {
        return Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
    }
}
