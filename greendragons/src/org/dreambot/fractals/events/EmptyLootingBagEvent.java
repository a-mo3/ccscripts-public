package org.dreambot.fractals.events;

import org.dreambot.GreenDragonFarm;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

public class EmptyLootingBagEvent extends AbstractResponseEvent<EmptyLootingBagEvent.Response> {
    public enum Response {
        NO_BAG_IN_INV,
        BAG_EMPTY,
        LOOTBAG_NULL
    }

    final int invParent = 15;
    Filter<WidgetChild> lootBagOpenWidgetFilter = x -> {
        if (x == null) return false;
        String text = x.getText();
        return x.getParentID() == invParent && text != null && text.equals("Bank your loot");
    };
    Filter<WidgetChild> itemContainerWidgetFilter = x -> x != null && x.getParentID() == 15 && x.getID() == 13;


    @Override

    public int onLoop() {
        if (!Inventory.contains(ItemID.LOOTING_BAG_CLOSED) && !Inventory.contains(ItemID.LOOTING_BAG_OPENED)) {
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
//            Sleep.sleepUntil(() -> Widgets.get(lootBagOpenWidgetFilter) != null, 2400);
//            return ReactionGenerator.getNormal();
//        }
//
        // check all the items and see if any have actions
        WidgetChild container = Widgets.get(itemContainerWidgetFilter);
        if (container != null) {
            // if theres items to deposit
            WidgetChild depositAllButton = Widgets.get(x -> x.getParentID() == invParent && x.hasAction("Deposit loot"));
            if (depositAllButton == null || !depositAllButton.isVisible() || depositAllButton.isHidden()) {
                Inventory.interact("Looting bag", "View");
            }
            if (depositAllButton != null && depositAllButton.interact("Deposit loot")) {
                Logger.info("Deposited loot");
                GreenDragonFarm.hasLootInBag = false;
                setResponse(Response.BAG_EMPTY);
            }
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }
}
