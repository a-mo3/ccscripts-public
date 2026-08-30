package org.dreambot.behaviour.mining;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

@Accessors(chain = true)
public class MiningFractal extends Fractal {
    public static final int RUNE_PICKAXE = 1275;
    public static final int MITHRIL_PICKAXE = 1273;
    public static final int BRONZE_PICKAXE = 1265;

    public static final Supplier<Integer> appropriatePickaxe = () -> {
        int mineLvl = Skills.getRealLevel(Skill.MINING);
        if (mineLvl >= 41) return RUNE_PICKAXE;
        if (mineLvl >= 21) return MITHRIL_PICKAXE;
        return BRONZE_PICKAXE;
    };

    private final Area targetArea;
    private final Rock rockType;

    @Setter
    private boolean shouldBank;

    public MiningFractal(Supplier<Boolean> acceptCondition, Area targetArea, Rock rockType) {
        super(acceptCondition);
        this.targetArea = targetArea;
        this.rockType = rockType;
    }

    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Bank.depositAll(x -> appropriatePickaxe.get() != x.getID());
            return ReactionGenerator.getNormal();
        }

        if (!targetArea.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(targetArea.getRandomTile());
            return ReactionGenerator.getNormal();
        }

        GameObject rock = rockType.getClosest();
        if (rock != null && targetArea.contains(rock) && !Players.getLocal().isAnimating()) {
            rock.interact("Mine");
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 1300);
        }
        return ReactionGenerator.getNormal();
    }
}
