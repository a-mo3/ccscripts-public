package org.dreambot.behaviour.prayer;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * pick ups bones and buries them
 */
public class CheapPrayerFractal extends Fractal {
    private final Area targetArea;
    private final int boneID;

    public CheapPrayerFractal(Supplier<Boolean> acceptCondition, Area targetArea, int boneID) {
        super(acceptCondition);
        this.targetArea = targetArea;
        this.boneID = boneID;
    }

    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Bank.depositAllItems();
            return ReactionGenerator.getNormal();
        }

        if (!targetArea.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(targetArea);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(boneID)) {
            Inventory.interact(boneID, "Bury");
            return ReactionGenerator.getLong();
        }

        GroundItem bone = GroundItems.closest(i -> i.getID() == boneID && targetArea.contains(i));
        // todo hopping if there are no bones
        if (bone != null) {
            if (bone.distance() > 10) {
                if (Walking.shouldWalk(6)) Walking.walk(bone);
                return ReactionGenerator.getNormal();
            }

            bone.interact("Take");
            Sleep.sleepUntil(() -> Inventory.contains(boneID), 1400);
            return ReactionGenerator.getLong();
        }
        return ReactionGenerator.getNormal();
    }
}
