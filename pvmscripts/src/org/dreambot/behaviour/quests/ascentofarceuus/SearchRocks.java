package org.dreambot.behaviour.quests.ascentofarceuus;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

public class SearchRocks extends Fractal {
    /*
    This quest step you have to search a rock for some orb, its unknown which of the rocks it will be in
    The action is inspect
     */

    Area DARK_ALTAR= new Area(1703, 3898, 1726, 3865);
    Stack<GameObject> rockStack = new Stack<>();

    public SearchRocks(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        // go to dark altar
        if (!DARK_ALTAR.contains(Players.getLocal())) {
            slowLog("Go to altar");
            if (Walking.shouldWalk()) Walking.walk(DARK_ALTAR);
            return ReactionGenerator.getNormal();
        }

        // if you have no rocks in the stack add all the inspectable rocks
        if (rockStack.isEmpty()) {
            List<GameObject> rocks = GameObjects.all(x -> DARK_ALTAR.contains(x) && x.hasAction("Inspect"));
            rocks.forEach(x -> rockStack.push(x));
            log("Finding inspectable rocks " + rocks.size());
            return ReactionGenerator.getNormal();
        }

        // search all the items, you will either find it and move on in the parent fractal or search again once stacks empty
        GameObject search = rockStack.pop();
        if (search != null && search.exists()) {
            log("Inspecting rock " + search);
            search.interact("Inspect");
            Sleep.sleep(10_000);
        }
        return ReactionGenerator.getNormal();
    }
}
