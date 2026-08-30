package org.dreambot.behaviour.training.theknightssword;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetBlurite extends Fractal {
    public GetBlurite(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    private final Tile MINING_TILE = new Tile(3067, 9583);

    @Override
    public int onLoop() {
        if (Skills.getBoostedLevel(Skill.HITPOINTS) <= 7) {
            Inventory.interact(ItemID.LOBSTER, "Eat");
        }

        if (Players.getLocal().isInCombat() && Walking.getRunEnergy() > 10 && !Walking.isRunEnabled()) {
            Walking.toggleRun();
        }

        if (!Players.getLocal().getTile().equals(MINING_TILE)) {
            if (Walking.shouldWalk(8)) Walking.walkExact(MINING_TILE);
            return ReactionGenerator.getQuick();
        }

        GameObject bluriteOre = GameObjects.closest(x -> x.getID() == 11378 && x.distance(MINING_TILE) < 3);
        if (bluriteOre != null && bluriteOre.interact("Mine")) {
            Sleep.sleepUntil(() -> Inventory.contains(ItemID.BLURITE_ORE), 1000);
        }
        return ReactionGenerator.getNormal();
    }
}