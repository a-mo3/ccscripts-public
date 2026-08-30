package org.dreambot.behaviour.method.mixology;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.method.mixology.data.PotionOrder;
import org.dreambot.behaviour.method.mixology.data.PotionType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * crystallising concentrating or homogenising the base potions
 */
public class ProcessPotions extends Fractal {
    final MixologyBranch branch;

    public ProcessPotions(Supplier<Boolean> acceptCondition, MixologyBranch branch) {
        super(acceptCondition);
        this.branch = branch;
        Client.getInstance().addEventListener(this);
    }

    public static final int AGITATOR_ANIMATION = 11634;
    public static final int ALEMBIC_ANIMATION = 11639;
    public static final int RETORT_ANIMATION = 11644;

    public static final int AGITATOR_GRAPHIC_OBJ_QUICK_ACTION = 2954;
    public static final int ALEMBIC_GRAPHIC_OBJ_QUICK_ACTION = 2955;

    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            if (!Inventory.contains(x -> PotionType.fromItemId(x.getId()) == null)) {
                log("All potions! wtf!");
                ObjectUtil.interact("Conveyor belt");
                return ReactionGenerator.getNormal();

            }

            new BankAllInventoryEvent(x -> PotionType.fromItemId(x.getId()) == null).execute();
        }

        PotionOrder nextToMake = Arrays.stream(branch.currentOrders)
                .filter(x -> !x.fulfilled())
                .findFirst()
                .orElse(null);
        if (nextToMake == null) {
            // deposit
            if (!Inventory.contains(x -> PotionType.fromItemId(x.getId()) != null)) {
                log("No potion but all complete, attempting to update order");
                branch.setOrders();
                for (PotionOrder currentOrder : branch.currentOrders) {
                    currentOrder.setFulfilled(false);
                }
            }
            ObjectUtil.interact("Conveyor belt");
            return ReactionGenerator.getNormal();
        }

        // todo check if needs to quickaction

        // check if animation, assume we're cooking it up if so
        if (Players.getLocal().isAnimating()) {
            int currentAni = Players.getLocal().getAnimation();

            if (PlayerSettings.getBitValue(MixologyData.VARBIT_RETORT_POTION) != 0) {
                log("Retorting");
                ObjectUtil.interact("Retort");
                return 1800;
            }

            if (PlayerSettings.getBitValue(MixologyData.VARBIT_AGITATOR_POTION) != 0) {
                if (currentAni == AGITATOR_ANIMATION) {
                    log("Using Agitator");
                    GraphicsObject qa = GraphicsObjects.closest(AGITATOR_GRAPHIC_OBJ_QUICK_ACTION);
                    if (qa != null) {
                        log("Agitator quick action");
                        ObjectUtil.interact("Agitator");
                        return 1800;
                    }
                    return ReactionGenerator.getNormal();
                } else {
                    log("Potion in agitator");
                    ObjectUtil.interact("Agitator");
                    Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 2400);
                    return 1800;
                }
            }


            if (PlayerSettings.getBitValue(MixologyData.VARBIT_ALEMBIC_POTION) != 0) {
                if (currentAni == ALEMBIC_ANIMATION) {
                    log("Using Alembic");
                    GraphicsObject qa = GraphicsObjects.closest(ALEMBIC_GRAPHIC_OBJ_QUICK_ACTION);
                    if (qa != null) {
                        log("Alembic quick action");
                        ObjectUtil.interact("Alembic");
                        return 1800;
                    }
                    return ReactionGenerator.getNormal();
                } else {
                    log("Potion in alembic");
                    ObjectUtil.interact("Alembic");
                    Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 2400);
                    return 1800;
                }
            }

            slowLog("Animating");
            return ReactionGenerator.getNormal();
        }

        log("Next to make " + nextToMake);
        GameObject station = GameObjects.closest(nextToMake.potionModifier().alchemyObjectId());
        if (station == null) {
            log("Failed to find station");
            return ReactionGenerator.getNormal();
        }

        log("Interact with station");
        station.interact();
        Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 2400);
        return ReactionGenerator.getNormal();
    }
}
