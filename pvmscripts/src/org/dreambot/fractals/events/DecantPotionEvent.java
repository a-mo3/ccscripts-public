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
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class DecantPotionEvent extends AbstractResponseEvent<DecantPotionEvent.Response> {
    static final String CONTAINS_BELOW_4_CHARGE = ".*[123].*";
    Area HERB_GUY = new Area(3152, 3484, 3159, 3478);

    // dont decant more than once an hour
    private static final Timer decantTimer = new Timer(60 * 1000 * 60);

    public enum Response {
        TIME_THRESHOLD_NOT_MET,
        SUCCESS,
    }

    String[] potionNames;

    public DecantPotionEvent(String... potionNames) {
        this.potionNames = potionNames;
    }

    @Override
    public Response executed() {
        if (!decantTimer.finished()) return Response.TIME_THRESHOLD_NOT_MET;
        return super.executed();
    }

    @Override
    public int onLoop() {
        // find the first potion from the potion names list that you have > 4 doses of in 1-3 potions
        String potion = Arrays.stream(potionNames)
                .filter(
                        // potions with 4 doses
                        x -> OwnedItems.all(i -> i.getName().toLowerCase().contains(x))
                                .stream()
                                .filter(i -> i.getName().toLowerCase().contains("divine"))
                                .filter(i -> hasBelowFourCharge(i.getName()))
                                .mapToInt(item -> cleanToInt(item.getName()) * item.getAmount())
                                .sum() >= 4
                )
                .findFirst().orElse(null);
        if (potion == null) {
            // if you have none, return SUCCESS and reset the timer
            Logger.info("No Potion found");
            setResponse(Response.SUCCESS);
            return ReactionGenerator.getNormal();
        }

        // withdraw them all, noted
        if (Bank.contains(i -> i.getName().toLowerCase().contains(potion))) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            if (Inventory.isFull()) {
                Bank.depositAllItems();
                return ReactionGenerator.getNormal();
            }

            Bank.setWithdrawMode(BankMode.NOTE);
            Bank.withdrawAll(x -> x.getName().toLowerCase().contains(potion));
            return ReactionGenerator.getNormal();
        }

        // decant
        if (Bank.isOpen()) {
            Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (!HERB_GUY.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(HERB_GUY);
            return ReactionGenerator.getNormal();
        }

        WidgetChild fourDose = Widgets.get(582, 6);
        if (fourDose != null && fourDose.isVisible()) {
            fourDose.interact();
            Sleep.sleep(2000);
            return ReactionGenerator.getNormal();
        }

        NPC bob = NPCs.closest(x -> HERB_GUY.contains(x) && x.hasAction("Decant"));
        if (bob != null && bob.interact("Decant")) {
            Antiban.sleepUntil(ItemProcessing::isOpen, 2400);
        }

        return ReactionGenerator.getNormal();
    }

    // (1) (2) (3)
    private boolean hasBelowFourCharge(String s) {
        return s.matches(CONTAINS_BELOW_4_CHARGE);
    }

    private int cleanToInt(String s) {
        String i = s.toLowerCase().replaceAll("[^\\d.]", "");
        Logger.info(s + " " + i);
        return Integer.parseInt(i);
    }
}
