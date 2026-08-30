package org.dreambot.behaviour.training.nmz;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * when a use owns NMZ script, this fractal can be used to create some tabs
 */
public class MakeTrollHeimTabs extends Fractal {
    public MakeTrollHeimTabs(boolean enabled) {
        super(() -> enabled
                && Client.getInstance().getScriptManager().hasSDNScript(2019)
                && Players.getLocal().getY() < 3600
                && !OwnedItems.contains(ItemID.TROLLHEIM_TELEPORT)
                && (NightmareZone.getNMZPoints() >= 775 || OwnedItems.contains(ItemID.SCROLL_OF_REDIRECTION)));

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SCROLL_OF_REDIRECTION, () -> OwnedItems.count(ItemID.SCROLL_OF_REDIRECTION))
                .addItem(ItemVariants.COMBAT_BRACLET)
                .addItem(ItemID.TELEPORT_TO_HOUSE, 50);


        setSimpleName("Make trollheim tabs");
    }

    public static final Tile CHEST_TILE = new Tile(2609, 3119);

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            log("Handle dialogue");
            Dialog.solve("All", "");
            return ReactionGenerator.getNormal();
        }
        // handle creation screen
        WidgetChild creationWidget = Widgets.get(187, 3, 9);
        if (creationWidget != null) {
            log("Create tabs");
            creationWidget.interact();
            Sleep.sleep(2500);
            return ReactionGenerator.getNormal();
        }

        // combine scrolls and tabs
        if (Inventory.containsAll(ItemID.TELEPORT_TO_HOUSE, ItemID.SCROLL_OF_REDIRECTION)) {
            log("Combine");
            if (Widgets.isOpen()) Widgets.closeAll();
            Inventory.combine(ItemID.SCROLL_OF_REDIRECTION, ItemID.TELEPORT_TO_HOUSE);
            Sleep.sleepUntil(Widgets::isOpen, 2400);
            return ReactionGenerator.getNormal();
        }

        // buy the redirection scroll
        if (CHEST_TILE.distance() > 10) {
            log("Walk to rewards chest");
            if (Walking.shouldWalk()) Walking.walk(CHEST_TILE);
            return ReactionGenerator.getNormal();
        }

        Widget parent = Widgets.getWidget(206);
        if (parent == null || !parent.isVisible()) {
            GameObject rewardChest = GameObjects.closest("Rewards chest");
            if (rewardChest == null) {
                log("Failed to find reward chest");
                return ReactionGenerator.getNormal();
            }

            log("Search reward chest");
            rewardChest.interact("Search");
            Sleep.sleepUntil(Widgets::isOpen, 4400);
            return ReactionGenerator.getNormal();
        }

        WidgetChild openBenefitTabButton = Widgets.get(x -> x.hasAction("Resources"));
        if (openBenefitTabButton != null) {
            log("Open resources tab");
            openBenefitTabButton.interact("Resources");
            return ReactionGenerator.getNormal() + 1200;
        }

        WidgetChild buyHerb = Widgets.get(x -> x.getName().contains("Scroll of redirection") && x.hasAction("Buy-50"));
        if (buyHerb == null) {
            log("Failed to find buy scrolls button");
        } else {
            log("Buy 50 of dem jaunts");
            buyHerb.interact("Buy-50");
        }

        return ReactionGenerator.getNormal();
    }
}
