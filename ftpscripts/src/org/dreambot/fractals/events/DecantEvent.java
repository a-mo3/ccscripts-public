package org.dreambot.fractals.events;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DecantEvent extends AbstractResponseEvent<DecantEvent.Response> {
    enum Response {
        DECANTED,
        TIMEOUT
    }

    public static final int STAMINA_POTION3 = 12627;
    public static final int STAMINA_POTION2 = 12629;
    public static final int STAMINA_POTION1 = 12631;
    public static final int PRAYER_POTION3 = 139;
    public static final int PRAYER_POTION2 = 141;
    public static final int PRAYER_POTION1 = 143;
    public static final int[] potions = new int[]{
            STAMINA_POTION3,
            STAMINA_POTION2,
            STAMINA_POTION1,
            PRAYER_POTION1,
            PRAYER_POTION2,
            PRAYER_POTION3
    };

    Timer t = new Timer(3 * 1000 * 60);

    private int getNotedId(int id) {
        return new Item(id, 0).getNotedItemID();
    }

    public static boolean shouldDecant() {
        int ownedCountPrayer = OwnedItems.count(PRAYER_POTION1, true)
                + (OwnedItems.count(PRAYER_POTION2, true) * 2)
                + (OwnedItems.count(PRAYER_POTION3, true) * 3);

        int ownedStaminaCount = OwnedItems.count(STAMINA_POTION1, true)
                + (OwnedItems.count(STAMINA_POTION2, true) * 2)
                + (OwnedItems.count(STAMINA_POTION3, true) * 3);

        return ownedStaminaCount > 4 || ownedCountPrayer > 4;
    }

    public DecantEvent() {
        setSleepHigh(ReactionGenerator.getReactionSettings().getNormalHigh());
        setSleepHigh(ReactionGenerator.getReactionSettings().getNormalLow());
    }

    Area HERB_GUY = new Area(3152, 3484, 3159, 3478);

    @Override
    public int onLoop() {

        if (Inventory.isFull()) {
            Logger.info("Bank all.");
            new BankAllInventoryEvent().execute();
        }

        if (t.finished()) {
            setResponse(Response.TIMEOUT);
            return ReactionGenerator.getNormal();
        }

        if (!shouldDecant()) {
            setResponse(Response.DECANTED);
            return ReactionGenerator.getNormal();
        }

        if (Bank.contains(potions)) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) BankUtil.openClosest();
                return ReactionGenerator.getNormal();
            }

            List<Integer> pot = Arrays.stream(potions)
                    .boxed()
                    .collect(Collectors.toList());
            Bank.setWithdrawMode(BankMode.NOTE);
            Item item = Bank.get(x -> pot.contains(x.getID()));
            Bank.withdraw(x -> pot.contains(x.getID()), item.getAmount());
            return ReactionGenerator.getNormal();
        }

        if (!HERB_GUY.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(HERB_GUY);
            return sleep();
        }

        WidgetChild fourDose = Widgets.get(582, 6);
        if (fourDose != null && fourDose.isVisible()) {
            fourDose.interact();
            Sleep.sleep(2000);
            return ReactionGenerator.getNormal();
        }

        NPC bob = NPCs.closest(x -> HERB_GUY.contains(x) && x.hasAction("Decant"));
        if (bob != null && bob.interact("Decant")) {
            Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
        }
        return sleep();
    }


}
