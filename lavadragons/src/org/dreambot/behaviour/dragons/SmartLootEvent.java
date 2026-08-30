package org.dreambot.behaviour.dragons;

import org.dreambot.LavaDragonFarm;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;
import java.util.function.Supplier;

public class SmartLootEvent extends AbstractResponseEvent<SmartLootEvent.Response> {
    final Supplier<List<GroundItem>> lootSupplier;
    final Integer[] dropItems;

    public SmartLootEvent(Supplier<List<GroundItem>> lootSupplier, Integer... dropItems) {
        this.lootSupplier = lootSupplier;
        this.dropItems = dropItems;
        this.ignoreGlobalCondition = true;
    }

    enum Response {
        LOOTED_ALL,
        FULL_INVENTORY,
        PKER
    }

    @Override
    public int onLoop() {
        if (AntiPk.getThreat() != null) {
            setResponse(Response.PKER);
            return sleep();
        }

        List<GroundItem> loot = lootSupplier.get();
        if (loot.isEmpty()) {
            setResponse(Response.LOOTED_ALL);
            return sleep();
        }

        // sort list to prefer most valuable items
        loot.sort((loot1, loot2) -> {
            int valueOne = loot1.getAmount() * LivePrices.getHigh(loot1.getID());
            int valueTwo = loot2.getAmount() * LivePrices.getHigh(loot2.getID());
            return valueTwo - valueOne;
        });

        if (Inventory.isFull()) {
            for (Integer droppableId : dropItems) {
                if (Inventory.contains(droppableId)) {
                    Logger.info("Dropping item " + droppableId);
                    if (Inventory.drop(droppableId)) {
                        Sleep.sleepUntil(() -> AntiPk.getThreat() != null || Inventory.emptySlotCount() > 0, 1600);
                    }
                    return sleep();
                }
            }
            Logger.info("No droppable full inventory");
            setResponse(Response.FULL_INVENTORY);
            return sleep();
        }

        Logger.info("Looting item");
        loot.get(0).interact("Take");
        if (ItemVariants.LOOTING_BAG.getItem() != null) LavaDragonFarm.hasLootInBag = true;
        return sleep();
    }

    @Override
    protected int sleep() {
        return ReactionGenerator.getQuick();
    }

}
