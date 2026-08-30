package org.dreambot.fractals.events;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.settings.timing.ReactionGenerator;

public class EmptyLootingBagEvent extends AbstractResponseEvent<EmptyLootingBagEvent.Response> {
    public enum Response {
        NO_BAG_IN_INV,
        BAG_EMPTY,
        LOOTBAG_NULL
    }

    final int invParent = 15;
    Filter<WidgetChild> itemContainerWidgetFilter = x -> x != null && x.getWidgetId() == 15 && x.getChildId() == 13;


    @Override

    public int onLoop() {
        if (!Inventory.contains(ItemID.LOOTING_BAG_CLOSED) && !Inventory.contains(ItemID.LOOTING_BAG_OPENED)) {
            Logger.info("No looting bag");
            LootingBag.lootingBagCache.clear();
            setResponse(Response.NO_BAG_IN_INV);
            return ReactionGenerator.getNormal();
        }

        if (!Bank.isOpen()) {
            if (Walking.shouldWalk()) BankUtil.openClosest();
            return ReactionGenerator.getNormal();
        }

//        // open loot bag
//        WidgetChild bankYourLoot = Widgets.get(lootBagOpenWidgetFilter);
//        if (bankYourLoot == null) {
//            Item lootbag = ItemVariants.LOOTING_BAG.getItem();
//            if (lootbag == null) {
//                setResponse(Response.LOOTBAG_NULL);
//                return ReactionGenerator.getNormal();
//            }
//
//            lootbag.interact("View");
//            Antiban.sleepUntil(() -> Widgets.get(lootBagOpenWidgetFilter) != null, 2400);
//            return ReactionGenerator.getNormal();
//        }
//
        // check all the items and see if any have actions
        WidgetChild container = Widgets.get(itemContainerWidgetFilter);
        if (container != null) {
            // if theres items to deposit
            WidgetChild depositAllButton = Widgets.get(x -> x.getParentID() == invParent && x.hasAction("Deposit loot"));
            if (depositAllButton == null || !depositAllButton.isVisible() || depositAllButton.isHidden()) {
                Item bag = ItemVariants.LOOTING_BAG.getItem();
                Logger.info("View Action present: " + bag.hasAction("View"));
                WidgetChild view = Widgets.get(x -> x.getParentID() == invParent && x.hasAction("View"));
                if (view == null) {
                    Logger.info("No view action");
                    LootingBag.lootingBagCache.clear();
                    setResponse(Response.BAG_EMPTY);
                    return ReactionGenerator.getNormal();
                }
                Inventory.interact("Looting bag", "View");
            }

            if (depositAllButton != null && depositAllButton.interact("Deposit loot")) {
                Logger.info("Deposited loot");
                LootingBag.lootingBagCache.clear();
                if (Widgets.get(x -> x.getText().equalsIgnoreCase("The bag is empty.")) != null)
                    setResponse(Response.BAG_EMPTY);
            }
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }
}
