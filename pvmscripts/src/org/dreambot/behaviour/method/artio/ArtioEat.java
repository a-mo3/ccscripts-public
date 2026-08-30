package org.dreambot.behaviour.method.artio;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.calvarion.LeaveCalvarion;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * eat and combo eat if available
 */
public class ArtioEat extends Fractal {
    List<Integer> acceptableFood = Arrays.asList(
            ItemID.BLIGHTED_MANTA_RAY,
            ItemID.SHARK
    );

    static Timer lock = new Timer(800);

    public ArtioEat(Supplier<Boolean> acceptCondition) {
        super(() -> acceptCondition.get() && lock.finished());
    }

    @Override
    public int onLoop() {
        Item food = Inventory.get(x -> acceptableFood.contains(x.getId()));
        if (food == null) {
            // get out of here
            Logger.info("No food");
            return LeaveCalvarion.leaveCalvarion();
        }

        int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (missingHP >= 40) {
            food.interact("Eat");
            Sleep.sleep(100);
            Inventory.interact(ItemID.COOKED_KARAMBWAN, "Eat");
            lock.reset();
            return ReactionGenerator.getQuick();
        }

        food.interact("Eat");
        lock.reset();
        return ReactionGenerator.getQuick();
    }
}
