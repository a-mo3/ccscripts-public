package org.dreambot.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
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
            ItemID.SHORTBOW,
            ItemID.OAK_LONGBOW,
            ItemID.OAK_SHORTBOW,
            ItemID.MAPLE_LONGBOW,
            ItemID.MAPLE_SHORTBOW,
            ItemID.WILLOW_LONGBOW,
            ItemID.WILLOW_SHORTBOW,
            ItemID.YEW_LONGBOW,
            ItemID.YEW_SHORTBOW,
            ItemID.MAGIC_LONGBOW,
            ItemID.MAGIC_SHORTBOW
    };

    @Override
    public boolean isValid() {
        if (timer == null) timer = new Timer((long) ScriptSettings.hoursUntilMuleOff() * 1000 * 60 * 60);
        return timer.finished();
    }

    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.open()) Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (OwnedItems.containsAnyUnworn(itemsToMule)) {
            Logger.info("Selling all items");
            new SellAllEvent(itemsToMule)
                    .setInterruptCondition(Combat::isInWild)
                    .execute();
            return ReactionGenerator.getNormal();
        }

        int muleOffGP = OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getSettingsData().gpRemainingAfterMuling;
        Logger.info("Muling off " + muleOffGP);
        if (!Trade.isOpen() && muleOffGP < 10_000) {
            Logger.info("Resetting timer not enough gp to bother muling off");
            timer.reset();
            return ReactionGenerator.getNormal();
        }

        Logger.info("Making mule request offering " + muleOffGP);
        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.COINS_995, muleOffGP)
                .execute();
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        String str = message.getMessage();
//        Logger.info(message.getType() + " on msg " + str);
    }
}
