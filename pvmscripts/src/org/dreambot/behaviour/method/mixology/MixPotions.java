package org.dreambot.behaviour.method.mixology;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.mixology.data.PotionOrder;
import org.dreambot.behaviour.method.mixology.data.PotionReagent;
import org.dreambot.behaviour.method.mixology.data.PotionType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * make the 3 ordered potions
 */
public class MixPotions extends Fractal {

    public static final Tile MIX_TILE = new Tile(1394, 9324, 0);

    // ORDER holds the ordinal in PotionType (when -1)
    private static final int VARBIT_POTION_ORDER_1 = 11315;
    // MODIFIER holds the ordinal in PotionModifier (when -1)
    private static final int VARBIT_POTION_MODIFIER_1 = 11316;
    private static final int VARBIT_POTION_ORDER_2 = 11317;
    private static final int VARBIT_POTION_MODIFIER_2 = 11318;
    private static final int VARBIT_POTION_ORDER_3 = 11319;
    private static final int VARBIT_POTION_MODIFIER_3 = 11320;

    // value -1 is the reagent (from right to left)
    // mox 1 aga 2 lye 3
    public static final int CURRENT_REAGENT_1 = 11324;
    public static final int CURRENT_REAGENT_2 = 11325;
    public static final int CURRENT_REAGENT_3 = 11326;

    final MixologyBranch branch;
    MixologyRewardItem rewardItem;

    public MixPotions(Supplier<Boolean> acceptCondition, MixologyBranch branch, MixologyRewardItem rewardItem) {
        super(acceptCondition);
        this.branch = branch;
        this.rewardItem = rewardItem;
    }

    @Override
    public int onLoop() {
        // we know we have an acceptable amount of reagents here
        if (Inventory.isFull()) {
            if (!Inventory.contains(x -> PotionType.fromItemId(x.getId()) == null)) {
                log("All potions! wtf!");
                ObjectUtil.interact("Conveyor belt");
                return ReactionGenerator.getNormal();

            }

            new BankAllInventoryEvent(x -> PotionType.fromItemId(x.getId()) == null).execute();
        }

        // get onto mixing spot
        if (!MIX_TILE.equals(Players.getLocal().getTile())) {
            log("Get onto mix tile");
            if (Inventory.count(ItemID.COINS_995) < 5000 && Players.getLocal().getY() < 5000) {
                log("Getting coins for boat - " + new WithdrawLoadoutEvent(new InventoryLoadout()
                        .addItem(ItemID.COINS_995, 10_000), null).executed());
            }
            if (Walking.shouldWalk()) Walking.walkExact(MIX_TILE);
            return ReactionGenerator.getNormal();
        }

        // its more likely you get lye or mox points, so after you have a 10x ratio of those start skipping potions that
        // wont reward you lye points
        int aga = PlayerSettings.getConfig(4415);
        int mox = PlayerSettings.getConfig(4416);
        int lye = PlayerSettings.getConfig(4414);
        if (rewardItem == MixologyRewardItem.ALDARIUM && (aga / 10) > lye && (mox / 10) > lye && mox > 1000 && aga > 1000) {
            log("Point ratio askew focusing on lye");
            for (int i = 0; i < 2; i++) {
                PotionOrder currentOrder = branch.currentOrders[i];
                if (Arrays.stream(currentOrder.potionType().components()).noneMatch(x -> x == PotionReagent.LYE)) {
                    log("Skipping order " + currentOrder);
                    currentOrder.setFulfilled(true);
                }
            }
        }

        // mix first potion we need and dont have
        // here we have to consider getting more than 1 of the same base potion eg LLL, LLL, MMA, more than just contains
        // todo replace with dep injected from branch
        List<PotionType> requiredPotions = Arrays.stream(branch.currentOrders)
                .filter(x -> !x.fulfilled())
                .map(PotionOrder::potionType)
                .collect(Collectors.toList());
        PotionType next = nextPotion(Inventory.all().stream()
                        .filter(Objects::nonNull)
                        .map(Item::getId)
                        .collect(Collectors.toList()),
                requiredPotions);

        if (next != null) {
            if (!isReadyToCollect(next)) {
                log("Create potion " + next);
                for (PotionReagent reagent : next.components()) {
                    GameObject lever = reagent.getLever();
                    if (lever != null) lever.interact();
                    Sleep.sleep(600, 1000);
                }
                return ReactionGenerator.getNormal();
            }
            // collect
            GameObject collect = GameObjects.closest(x -> x.getName().contains("Mixing vessel"));
            if (collect == null) {
                log("Failed to find collection vial");
                return ReactionGenerator.getNormal();
            }
            collect.interact();
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }

    boolean isReadyToCollect(PotionType next) {
        for (int i = 0; i < 3; i++) {
            int varp = PlayerSettings.getBitValue(CURRENT_REAGENT_1 + i);
            if (varp <= 0 || PotionReagent.values()[varp - 1] != next.components()[2 - i]) {
                log("Not ready to collect " + i + " " + varp + " Expecting " + next.components()[2 - i]);
                return false;
            }
        }
        return true;
    }

    PotionType nextPotion(List<Integer> inv, List<PotionType> orders) {
        for (PotionType order : orders) {
            if (inv.contains(order.itemId())) {
                inv.remove(new Integer(order.itemId()));
                continue;
            }
            return order;
        }
        return null;
    }
}
