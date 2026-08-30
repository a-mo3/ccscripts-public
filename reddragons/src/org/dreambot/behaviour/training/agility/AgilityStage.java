package org.dreambot.behaviour.training.agility;


import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class AgilityStage extends Fractal {
    Condition sleepCondition;
    Supplier<GameObject> object;
    String action;

    public AgilityStage(Supplier<Boolean> acceptCondition, Condition sleepCondition, Supplier<GameObject> object, String action) {
        super(acceptCondition);
        this.sleepCondition = sleepCondition;
        this.object = object;
        this.action = action;
    }

    @Override
    public boolean isValid() {
        return acceptCondition.get();
    }

    @Override
    public int onLoop() {
        GameObject target = object.get();
        Log.info("Trying to interact with: " + target);
        if (Walking.getRunEnergy() > 30 && !Walking.isRunEnabled()) {
            Walking.toggleRun();
        }

        GroundItem mog = GroundItems.closest(ItemID.MARK_OF_GRACE);
        if (mog != null && mog.canReach()) {
            mog.interact("Take");
            return ReactionGenerator.getNormal();
        }

        int rand = Calculations.random(1, 100);
        if (target != null && rand < 10) {
            Mouse.click(target.getTile().getPolygon().getBounds().getLocation());
            Log.info("Misclicked: " + rand);
            Sleep.sleep(400, 760);
        }

        if (target != null) {
            target.interact();
            Log.info("interacted");
            Sleep.sleepUntil(() -> Players.getLocal().isMoving(), 1600);
            Sleep.sleepUntil(sleepCondition,
                    () -> Players.getLocal().isMoving(),
                    2400, 100);
        }

        return ReactionGenerator.getNormal();
    }
}
