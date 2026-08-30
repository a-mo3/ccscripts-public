package org.dreambot.behaviour;


import lombok.SneakyThrows;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.script.SettingsData;
import org.dreambot.settings.timing.ReactionGenerator;

import java.text.DecimalFormat;

/**
 * Mule off after x hours
 */
public class MuleOff extends Fractal {
    public static Timer timer;
    final DecimalFormat DF = new DecimalFormat("###,###,###");

    @Override
    public boolean isValid() {
        SettingsData settings = ScriptSettings.getSettingsData();
        if (settings.autoAlch) {
            if (timer == null) timer = new Timer(60L * 60 * 1000 * settings.autoAlchMuleTime);

            return timer.finished();
        }
        return OwnedItems.count(ItemID.COINS_995) - settings.moneyLeftAfterMuling >= settings.profitThreshold;
    }

    @SneakyThrows
    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.open()) Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (OwnedItems.containsAnyUnworn(LOOT)) {
            Logger.info("Selling all items");
            new SellAllEvent(LOOT)
                    .setInterruptCondition(Combat::isInWild)
                    .execute();
            return ReactionGenerator.getNormal();
        }

        if (!Trade.isOpen()
                && OwnedItems.count(ItemID.COINS_995) <= ScriptSettings.getSettingsData().moneyLeftAfterMuling) {
            Logger.info("Not enough gp. reset");
            timer.reset();
            return ReactionGenerator.getNormal();
        }

        Logger.info("Making mule request");
            new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getSettingsData().moneyLeftAfterMuling)
                .execute();
        return ReactionGenerator.getNormal();
    }

    int[] LOOT = new int[]{
            ItemID.RING_OF_DUELING8,
            ItemID.RING_OF_RECOIL,
            ItemID.EMERALD_RING,
            ItemID.SAPPHIRE_RING,
    };
}
