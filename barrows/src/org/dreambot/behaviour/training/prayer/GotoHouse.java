package org.dreambot.behaviour.training.prayer;

import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.LinkedList;
import java.util.List;

public class GotoHouse extends Fractal implements ChatListener {
    final Area RIMMINGTON_PORTAL = new Area(2951, 3228, 2956, 3220);
    public static final List<String> blacklistedOwners = new LinkedList<>();

    EquipmentLoadout ring = new EquipmentLoadout()
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .setEnabledCondition(() -> !Client.isDynamicRegion())
            .setRefill(10);

    InventoryLoadout inv = new InventoryLoadout()
            .addItem(ItemID.TELEPORT_TO_HOUSE, 5, 100)
            .setEnabledCondition(() -> !Inventory.contains(ItemID.TELEPORT_TO_HOUSE)).setRefill(500)
            .addItem(ItemID.DRAGON_BONES, 27).setRefill(278);

    public GotoHouse() {
        Client.getInstance().addEventListener(this);
        blacklistedOwners.add("V 3");
        blacklistedOwners.add("Pyramid");
        blacklistedOwners.add("XVLKLEOPATRA");
    }

    @Override
    public int onLoop() {
        if (Worlds.getCurrentWorld() != 330) {
            WorldHopper.hopWorld(330);
            return ReactionGenerator.getLong();
        }

        if (!ring.isFulfilled() || !inv.isFulfilled()) {
            new WithdrawLoadoutEvent(inv, ring)
                    .setBuyRemainder(true)
                    .executed();
            return ReactionGenerator.getNormal();
        }

        if (!RIMMINGTON_PORTAL.contains(Players.getLocal())) {
            if (Bank.isOpen()) {
                Bank.close();
                return ReactionGenerator.getNormal();
            }

            if (Inventory.interact(ItemID.TELEPORT_TO_HOUSE, "Outside")) {
                Sleep.sleepUntil(() -> RIMMINGTON_PORTAL.contains(Players.getLocal()), 8400);
            }
            return ReactionGenerator.getNormal();
        }

        Widget houseAds = Widgets.getWidget(52);
        if (houseAds == null || !houseAds.isVisible()) {
            GameObject house = GameObjects.closest("House Advertisement");
            if (house != null && house.interact("View")) {
                Sleep.sleep(800, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        // check if sorted high to low
        WidgetChild sortArrow = Widgets.get(52, 5, 8);
        Logger.info("sort " + sortArrow);
        // texture id was sprite id
        if (sortArrow != null && sortArrow.getTextureId() != 1051) {
            Mouse.click(sortArrow.getRectangle().getLocation());
            return ReactionGenerator.getNormal();
        }

        if (needsToRefresh) {
            needsToRefresh = false;
            log("Needs to refresh");
            WidgetChild refreshButton = Widgets.get(x -> x.getText().contains("Refresh"));
            if (refreshButton != null) {
                log("Found button");
                refreshButton.interact();
            }
            return ReactionGenerator.getNormal();
        }

        // get topmost (lowest Y) enter button
        WidgetChild enterHouse = getBestEnter();
        Logger.info("Best house " + getBestEnter());
        if (enterHouse != null && enterHouse.interact("Enter House")) {
            Sleep.sleepUntil(Client::isDynamicRegion, 3500);
        }

        return ReactionGenerator.getNormal();
    }

    final int ADVERT_PARENT = 52;
    final int NAME_PARENT = 9; // 52, 9, x is the player name for x button

    private WidgetChild getBestEnter() {
        List<WidgetChild> enterButtons = Widgets.getAll(x -> x.hasAction("Enter House"));
        WidgetChild lowestY = null;
        for (WidgetChild button : enterButtons) {
            int houseIndex = button.getIndex();
//            Log.info("House index - " + houseIndex);
            WidgetChild nameWidget = Widgets.get(ADVERT_PARENT, NAME_PARENT, houseIndex);
            if (nameWidget == null) {
                log("couldnt find name widget so button was skipped");
                continue;
            }

            String houseOwner = nameWidget.getText();
            log("Checking house " + houseIndex + " Owned " + houseOwner);
            if (blacklistedOwners.contains(houseOwner)) {
                log("this owner was blacklisted");
                continue;
            }

            if (lowestY == null) lowestY = button;
            if (button.getY() < lowestY.getY()) {
                lowestY = button;
            }
        }
        return lowestY;
    }

    boolean needsToRefresh;

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;

        if (message.getMessage().contains("or has privacy mode enabled")) needsToRefresh = true;
    }
}
