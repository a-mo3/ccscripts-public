package org.dreambot.fractals.events;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadoutItem;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.LoadoutExecutor;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class RedeemBondEvent extends AbstractEvent {
    ItemVariant BOND = new ItemVariant(ItemID.OLD_SCHOOL_BOND, ItemID.OLD_SCHOOL_BOND_UNTRADEABLE);
    boolean usedBond = false;

    Filter<World> membersWorldFilter = x -> x.isNormal() && x.getMinimumLevel() <= Skills.getTotalLevel() && x.isMembers();
    Filter<World> freeWorldFilter = x -> x.isNormal() && x.getMinimumLevel() <= Skills.getTotalLevel() && !x.isMembers();

    final int BOND_MENU_PARENT = 861;

    @Override
    public int onLoop() {
        if (Worlds.getCurrent().isMembers()) {
            setComplete(true);
            return ReactionGenerator.getNormal();
        }

        if (Client.getMembershipLeft() > 0) {
            WorldHopper.hopWorld(Worlds.getRandomWorld(membersWorldFilter));
            return ReactionGenerator.getNormal();
        }

        Logger.info("Bond event");
        if (Client.getMembershipLeft() > 0 || PlayerSettings.getConfig(1780) > 0) {
            Logger.info("redeem bond event, you have membership");
            setComplete(true);
            return ReactionGenerator.getNormal();
        }

        // get chat message widget
        if (Widgets.get(x -> x.getText().contains("log out before attempting")) != null) {
            WorldHopper.hopWorld(Worlds.getRandomWorld(freeWorldFilter));
            return ReactionGenerator.getNormal();
        }

        if (usedBond) {
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.contains(BOND.getIds()) || GrandExchange.isReadyToCollect()) {
            if (!GrandExchange.isOpen()) {
                if (Walking.shouldWalk()) GrandExchange.open();
                return ReactionGenerator.getNormal();
            }

            if (GrandExchange.isReadyToCollect()) {
                GrandExchange.collect();
                Sleep.sleepUntil(() -> BOND.getItem() != null, 2400);
                return ReactionGenerator.getNormal();
            }

            Sleep.sleepUntil(GrandExchange::isReadyToCollect, 2400);
            return ReactionGenerator.getNormal();
        }

        if (!OwnedItems.contains(BOND)) {
            if (OwnedItems.count(ItemID.COINS_995) < LivePrices.get(ItemID.OLD_SCHOOL_BOND)) {
                Logger.info("Cant afford bond so failing event");
                setFailed(true);
            }

            LoadoutExecutor.buyItem(new InventoryLoadoutItem(BOND).setBuyPrice(9_000_000));
            Sleep.sleepUntil(GrandExchange::isReadyToCollect, 1800);
            return ReactionGenerator.getNormal();
        }


        Widget parent = Widgets.getWidget(BOND_MENU_PARENT);
        if (parent != null && parent.isVisible()) {
            Logger.info("parent is open");
            // this is now a pop up menu
            WidgetChild confirmButton = Widgets.get(x -> x.getParentID() == 289 && x.hasAction("Accept"));
            Logger.info("confirm button " + confirmButton);
            if (confirmButton != null && confirmButton.interact("Accept")) {
                Logger.info("Accept");
                Sleep.sleepUntil(() -> Widgets.get(x -> x.getText().contains("log out before attempting")) != null, 45000);
                WorldHopper.hopWorld(Worlds.getRandomWorld(freeWorldFilter));
                usedBond = true;
                return ReactionGenerator.getNormal() + 3000;
            }

            WidgetChild oneBond = Widgets.get(x -> x.getParentID() == BOND_MENU_PARENT && x.hasAction("14 days membership"));
            if (oneBond != null && oneBond.isVisible()) {
                Logger.info("selecting 14 day membership");
                oneBond.interact("14 days membership");
            }
            return ReactionGenerator.getNormal() + 3000;
        }

        if (BOND.getItem() == null) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }
            Bank.withdraw(x -> Arrays.stream(BOND.getIds()).anyMatch(i -> i == x.getID()), 1);
            return ReactionGenerator.getNormal();
        }

        Item bond = BOND.getItem();
        // linter told me this is null safe.
        if (GrandExchange.isOpen() || Bank.isOpen()) {
            GrandExchange.close();
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }
        if (Inventory.interact(bond, "Redeem")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 5000);
        }
        return ReactionGenerator.getNormal();
    }

}
