package org.dreambot.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

public class MuleOff extends Fractal implements ChatListener {
    public static Timer timer;

    public MuleOff() {
        Client.getInstance().addEventListener(this);
    }

    public static final int[] itemsToMule = new int[]{
            ItemID.RED_CHINCHOMPA_10034,
            ItemID.AMULET_OF_GLORY_UNCHARGED,
            ItemID.CHINCHOMPA

    };

    @Override
    public boolean isValid() {
        if (timer == null) timer = new Timer(ScriptSettings.getMuleOffTime());
        return timer.finished();
    }

    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.open(BankLocation.GRAND_EXCHANGE)) Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (!Trade.isOpen() && shouldReset()) {
            timer.reset();
            return ReactionGenerator.getNormal();
        }

        if (ScriptSettings.getSettingsData().tradeOffChins) {
            if (!BankLocation.GRAND_EXCHANGE.getArea(25).contains(Players.getLocal())) {
                if (Walking.shouldWalk(6)) Walking.walk(BankLocation.GRAND_EXCHANGE);
                return ReactionGenerator.getQuick();
            }
            new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                    .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getSettingsData().gpRemainingAfterMuling)
                    .addOfferedItem(ItemID.AMULET_OF_GLORY_UNCHARGED, OwnedItems.count(ItemID.AMULET_OF_GLORY_UNCHARGED))
                    .addOfferedItem(ItemID.RED_CHINCHOMPA_10034, OwnedItems.count(ItemID.RED_CHINCHOMPA_10034))
                    .execute();
            return ReactionGenerator.getNormal();
        }

        if (OwnedItems.containsAnyUnworn(itemsToMule)) {
            new SellAllEvent(itemsToMule).execute();
            return ReactionGenerator.getNormal();
        }

        if (!BankLocation.GRAND_EXCHANGE.getArea(25).contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getQuick();
        }
        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getMuleRemainder())
                .execute();
        return ReactionGenerator.getNormal();
    }

    private boolean shouldReset() {
        if (ScriptSettings.getSettingsData().tradeOffChins) {
            return !OwnedItems.contains(ItemID.RED_CHINCHOMPA_10034);
        } else {
            return OwnedItems.count(ItemID.COINS_995) <= ScriptSettings.getMuleRemainder()
                    && !OwnedItems.contains(ItemID.RED_CHINCHOMPA_10034)
//                    && GrandExchange.contains(ItemID.RED_CHINCHOMPA_10034)
                    ;
        }
    }

    @Override
    public void onMessage(Message message) {
        String str = message.getMessage();
//        Logger.info(message.getType() + " on msg " + str);
    }
}
