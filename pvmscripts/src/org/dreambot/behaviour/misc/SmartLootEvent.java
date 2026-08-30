package org.dreambot.behaviour.misc;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class SmartLootEvent extends AbstractResponseEvent<SmartLootEvent.Response> {
    Timer logTimer = new Timer(20 * 1000);
    final Supplier<List<GroundItem>> lootSupplier;
    final Integer[] dropItems;

    public SmartLootEvent(Supplier<List<GroundItem>> lootSupplier, Integer... dropItems) {
        this.lootSupplier = lootSupplier;
        this.dropItems = dropItems;
        this.ignoreGlobalConditions = true;
    }

    public SmartLootEvent(Supplier<List<GroundItem>> lootSupplier, List<Integer> d) {
        this.lootSupplier = lootSupplier;
        this.dropItems = d.toArray(new Integer[]{});
        this.ignoreGlobalConditions = true;
    }

    enum Response {
        LOOTED_ALL,
        FULL_INVENTORY,
        PKER
    }

    @Override
    public int onLoop() {
        // todo do this
//        if (AntiPk.getThreat() != null) {
//            setResponse(Response.PKER);
//            return sleep();
//        }
//
        List<GroundItem> loot = lootSupplier.get();
        loot.sort(Comparator.comparingDouble(Entity::distance));
        if (loot.isEmpty()) {
            setResponse(Response.LOOTED_ALL);
            return sleep();
        }

        // sort list to prefer most valuable items
        loot.sort((loot1, loot2) -> {
            int valueOne = loot1.getAmount() * LivePrices.getHigh(loot1.getId());
            int valueTwo = loot2.getAmount() * LivePrices.getHigh(loot2.getId());
            return valueTwo - valueOne;
        });

        if (Inventory.isFull()) {
            for (Integer droppableId : dropItems) {
                if (Inventory.contains(droppableId)) {
                    Logger.info("Dropping item " + droppableId);
                    if (Inventory.get(droppableId).hasAction("Eat") && Combat.getHealthPercent() != 100) {
                        Inventory.interact(droppableId, "Eat");
                    }
                    if (Inventory.drop(droppableId)) {
                        Antiban.sleepUntil(() -> Inventory.emptySlotCount() > 0, 1600);
                    }
                    return sleep();
                }
            }
            Logger.info("No droppable full inventory");
            setResponse(Response.FULL_INVENTORY);
            return sleep();
        }

        if (logTimer.finished()) {
            Logger.info("Looting item");
            logTimer.reset();
        }
        loot.sort(Comparator.comparingDouble(Entity::distance));
        loot.get(0).interact("Take");
        Antiban.sleepUntil(() -> !loot.get(0).exists(), 1500);
//        if (ItemVariants.LOOTING_BAG.getItem() != null) BrutalBlues.hasLootInBag = true;
        return sleep();
    }

    @Override
    protected int sleep() {
        return ReactionGenerator.getQuick();
    }

}
