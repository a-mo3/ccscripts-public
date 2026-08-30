package org.dreambot.behaviour.training.agility;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.widgets.Menu;
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

        if (target != null) {
            if (!target.isOnScreen() || (target.distance() > 8 && !Menu.isMenuManipulationActive())) {
                Logger.info("Walking towards agility obstacle");
                Walking.walk(target);
            }
            target.interact();
            Log.info("interacted");
//            Sleep.sleepUntil(() -> Players.getLocal().isMoving(), 1600);
//            Sleep.sleepUntil(sleepCondition,
//                    () -> Players.getLocal().isMoving(),
//                    1200, 100);
        }

        return ReactionGenerator.getNormal();
    }
}
