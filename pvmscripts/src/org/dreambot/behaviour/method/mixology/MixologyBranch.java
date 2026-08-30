package org.dreambot.behaviour.method.mixology;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.script.listener.VarListener;
import org.dreambot.behaviour.method.mixology.data.PotionModifier;
import org.dreambot.behaviour.method.mixology.data.PotionOrder;
import org.dreambot.behaviour.method.mixology.data.PotionType;
import org.dreambot.fractals.Fractal;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.dreambot.behaviour.method.mixology.MixologyData.*;

public class MixologyBranch extends Fractal implements VarListener {
    // we will track this as state because i cant find any way to check which modifier a potion is once you have it,
    // only after refining it from the stations varbit update
    public PotionOrder[] currentOrders = new PotionOrder[3];

    public void fulfillOrder(PotionType type, PotionModifier modifier) {
        for (PotionOrder order : currentOrders) {
            log("Trying to match " + type + " " + modifier);
            log("Order: " + order);
            if (!order.fulfilled() && type == order.potionType() && order.potionModifier() == modifier) {
                order.setFulfilled(true);
                log("Fulfilled order " + order);
                return;
            }
        }
    }

    public MixologyBranch(Supplier<Boolean> acceptCondition, MixologyRewardItem rewardItem) {
        super(acceptCondition);

        // todo take digweed when its there
        // todo if we arent interacting with the station and theres a potion in it, finish it
        // todo if potion state is all empty, reset it

        this.paintArraySupplier = () -> new String[]{
                "Order 1 " + currentOrders[0],
                "Order 2 " + currentOrders[1],
                "Order 3 " + currentOrders[2]
        };

        Client.getInstance().addEventListener(this);
        setOrders();

        setSimpleName("Mixology");
        addChildren(
                new MixologyTutorial().setSimpleName("Tutorial"),
                new ClaimMixologyReward(rewardItem, rewardItem == MixologyRewardItem.ALDARIUM ? 10 : 1)
                        .setSimpleName("Claim rewards"),
                // maybe a check to after we deposit
                new GetMoreReagents(() -> (lowestResinCount() < 200 && !Inventory.contains(x -> PotionType.fromItemId(x.getId()) != null))
                        ||  lowestResinCount() < 30 )
                        .setSimpleName("Get more reagents"),

                // is animating for when we are cooking up a potion
                new ProcessPotions(() -> Players.getLocal().isAnimating()
                        // when we have the base for the order that isnt complete
                        || Arrays.stream(currentOrders).allMatch(x -> x != null && (x.fulfilled() || Inventory.contains(x.potionType().itemId()))),
                        this).setSimpleName("Process potion"),
                new MixPotions(() -> true, this, rewardItem).setSimpleName("Mix potions")
        );
    }

    public void setOrders() {
        PotionType[] types = PotionType.values();
        PotionModifier[] mods = PotionModifier.values();
        currentOrders = new PotionOrder[]{
                new PotionOrder(0,
                        types[Math.max(0, PlayerSettings.getBitValue(VARBIT_POTION_ORDER_1) - 1)],
                        mods[Math.max(0, PlayerSettings.getBitValue(VARBIT_POTION_MODIFIER_1) - 1)]
                ),
                new PotionOrder(1,
                        types[Math.max(0, PlayerSettings.getBitValue(VARBIT_POTION_ORDER_2) - 1)],
                        mods[Math.max(0, PlayerSettings.getBitValue(VARBIT_POTION_MODIFIER_2) - 1)]),
                new PotionOrder(2,
                        types[Math.max(0, PlayerSettings.getBitValue(VARBIT_POTION_ORDER_3) - 1)],
                        mods[Math.max(0, PlayerSettings.getBitValue(VARBIT_POTION_MODIFIER_3) - 1)]
                )
        };
    }


    PotionType alembicPotionType;
    PotionType agitatorPotionType;
    PotionType retortPotionType;

    List<Integer> resets = Arrays.asList(
            VARBIT_POTION_ORDER_1,
            VARBIT_POTION_MODIFIER_1,
            VARBIT_POTION_ORDER_2,
            VARBIT_POTION_MODIFIER_2,
            VARBIT_POTION_ORDER_3,
            VARBIT_POTION_MODIFIER_3
    );
    public static int gpMade;

    @Override
    public void onVarBitUpdate(int varbitId, int value) {
        if (resets.contains(varbitId)) {
            setOrders();
            return;
        }

        log("Varbit updated " + varbitId);
        log("Varbit value " + value);
        if (varbitId == VARBIT_ALEMBIC_POTION) {
            log("Alembic potion update");
            if (value == 0) {
                // when updated to 0 it means a potion was completed, so we look at our current order and set what was fulfilled
                log("Fulfill order");
                fulfillOrder(alembicPotionType, PotionModifier.CRYSTALISED);
            } else {
                log("potion type updated");
                alembicPotionType = PotionType.fromIdx(value - 1);
            }
            return;
        }

        if (varbitId == VARBIT_AGITATOR_POTION) {
            log("Agitator potion update " + value);
            if (value == 0) {
                fulfillOrder(agitatorPotionType, PotionModifier.HOMOGENOUS);
            } else {
                agitatorPotionType = PotionType.fromIdx(value - 1);
            }
            return;
        }

        if (varbitId == VARBIT_RETORT_POTION) {
            log("Retort potion update " + value);
            if (value == 0) {
                fulfillOrder(retortPotionType, PotionModifier.CONCENTRATED);
            } else {
                retortPotionType = PotionType.fromIdx(value - 1);
            }
            return;
        }
    }

}
