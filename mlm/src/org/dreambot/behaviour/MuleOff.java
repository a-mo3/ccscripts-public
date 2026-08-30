package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.method.MLMTopFloor;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

public class MuleOff extends Fractal {
    public static Timer timer;

    public static final int[] itemsToMule = new int[]{
            ItemID.GOLD_ORE,
            ItemID.ADAMANTITE_ORE,
            ItemID.RUNITE_ORE,
            ItemID.MITHRIL_ORE,
            ItemID.IRON_ORE,
            ItemID.COAL,
            ItemID.UNCUT_EMERALD,
            ItemID.UNCUT_JADE,
            ItemID.UNCUT_SAPPHIRE,
            ItemID.UNCUT_DIAMOND
    };

    @Override
    public boolean isValid() {
        if (timer == null) timer = new Timer(ScriptSettings.getMuleOffTime());
        return !Inventory.contains(ItemID.PAYDIRT) && timer.finished() && !MLMTopFloor.ALLOWED_TOPLEVEL_VEINS.contains(Players.getLocal());
    }

    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.open(BankLocation.GRAND_EXCHANGE)) Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (!BankLocation.GRAND_EXCHANGE.getArea(20).contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getQuick();
        }

        if (OwnedItems.containsAny(itemsToMule)) {
            new SellAllEvent(itemsToMule).execute();
            return ReactionGenerator.getNormal();
        }

        if (!Trade.isOpen() && OwnedItems.count(ItemID.COINS_995) <= ScriptSettings.getMuleRemainder()) {
            timer.reset();
            return ReactionGenerator.getNormal();
        }

        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getMuleRemainder())
                .execute();
        return ReactionGenerator.getNormal();
    }
}
