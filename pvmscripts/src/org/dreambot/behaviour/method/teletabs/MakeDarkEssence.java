package org.dreambot.behaviour.method.teletabs;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class MakeDarkEssence extends Fractal {
    final Area DARK_ALTAR = new Area(1710, 3886, 1718, 3880);

    public MakeDarkEssence(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Make Dark Essence");
    }

    @Override
    public int onLoop() {
        if (!DARK_ALTAR.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(DARK_ALTAR);
            return ReactionGenerator.getNormal();
        }
        GameObject altar = GameObjects.closest("Dark Altar");
        if (altar != null && altar.interact("Venerate")) {
            Sleep.sleepUntil(() -> !Inventory.contains(ItemID.DENSE_ESSENCE_BLOCK), 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
