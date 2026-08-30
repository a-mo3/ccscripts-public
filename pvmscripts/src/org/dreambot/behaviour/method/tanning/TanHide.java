package org.dreambot.behaviour.method.tanning;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.awt.*;

public class TanHide extends Fractal {
    Area TANNING_STORE = new Area(3270, 3194, 3277, 3189);
    final TanningHide hide;

    public TanHide(TanningHide hide, int restock, float priceIncrease) {
        this.prependLogic = () -> {
            if (Inventory.isFull() && OwnedItems.count(ItemID.COINS_995) < 580) {
                Logger.info("Requesting more coins");
                new BankAllInventoryEvent().execute();

                new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                        .addRequiredItem(ItemID.COINS_995, 50_000);
                return true;
            }
            return false;
        };

        this.hide = hide;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(hide.precursorId, 27)
                .setPriceIncrease(priceIncrease)
                .setRefill(restock)
                .addItem(ItemID.COINS_995, 28 * 20)
                .setStrict(true);

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .setEnabledCondition(Client::isMembers)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .setEnabledCondition(Client::isMembers)
        ;
    }

    public static final int TANNING_WIDGET = 324;

    @Override
    public int onLoop() {
        if (!Equipment.isSlotEmpty(EquipmentSlot.CHEST)) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Bank.depositAllEquipment();
            return ReactionGenerator.getNormal();
        }

        // go to tanning store
        if (!TANNING_STORE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(TANNING_STORE);
            return ReactionGenerator.getQuick();
        }

        Widget w = Widgets.getWidget(TANNING_WIDGET);
        if (w == null || !w.isVisible()) {
            Logger.info("Trading ellis " + w);
            NPC ellis = NPCs.closest("Ellis");
            if (ellis != null && ellis.interact("Trade")) {
                Sleep.sleepUntil(() -> Widgets.getWidget(TANNING_WIDGET) != null, 1400);
            }
            return ReactionGenerator.getQuick();
        }

        // find appropriate label
        WidgetChild label = Widgets.get(x -> x.getWidgetId() == TANNING_WIDGET
                && x.getText().toLowerCase().contains(hide.label));
        Logger.log("Labels " + label);

        if (label == null) {
            Logger.info("Label was null for " + hide);
            return ReactionGenerator.getNormal();
        }

        // find the tan all widget on that contains the label
        WidgetChild tanAll = Widgets.get(x -> x.getTooltip().contains("All") && x.getRectangle().contains(
                new Point((int) label.getRectangle().getCenterX(), (int) label.getRectangle().getCenterY())
        ));

        if (tanAll == null) {
            Logger.info("Unable to find tan all widget " + label);
            return ReactionGenerator.getNormal();
        }

        Logger.info("Tanning " + tanAll);
        if (tanAll.interact("Tan All")) {
            Sleep.sleepUntil(() -> !Inventory.contains(x -> x.getName().toLowerCase().contains("dragonhide")), 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
