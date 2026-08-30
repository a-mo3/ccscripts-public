package org.dreambot.behaviour.woodcutting;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

@Accessors(chain = true)
public class WoodcuttingFractal extends Fractal {
    private final Area targetArea;
    private final Supplier<GameObject> targetSupplier;
    @Setter
    private boolean shouldBank;

    public WoodcuttingFractal(Supplier<Boolean> acceptCondition, Area targetArea, Supplier<GameObject> targetSupplier) {
        super(acceptCondition);
        this.targetArea = targetArea;
        this.targetSupplier = targetSupplier;
    }


    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            if (shouldBank) {
                if (!Bank.isOpen()) {
                    if (Walking.shouldWalk()) Bank.open();
                    return ReactionGenerator.getNormal();
                }

                Bank.depositAll(x -> !x.getName().contains("axe"));
                return ReactionGenerator.getNormal();
            } else {
                Inventory.dropAll(x -> !x.getName().contains("axe"));
            }
            return ReactionGenerator.getNormal();
        }

        if (!targetArea.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(targetArea);
            return ReactionGenerator.getNormal();
        }

        GameObject target = targetSupplier.get();
//        Logger.info("Tree " + target);
        // todo this can probably break if the closest tree is one outside the area, shouldnt rly happen doe
        if (target != null && targetArea.contains(target)) {
            target.interact("Chop down");
            Sleep.sleepUntil(() -> Inventory.isFull() || !Client.isLoggedIn(),
                    () -> Players.getLocal().isAnimating(), 2400, 100);
        }
        return ReactionGenerator.getNormal();
    }
}
