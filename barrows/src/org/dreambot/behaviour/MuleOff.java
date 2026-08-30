package org.dreambot.behaviour;


import lombok.SneakyThrows;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.DecantEvent;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.ScriptSettings;
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
        if (timer == null)
            timer = new Timer((long) ScriptSettings.getSettingsData().hoursUntilMuleOff * 1000 * 60 * 60);
        return BankLocation.GRAND_EXCHANGE.getArea(50).contains(Players.getLocal()) && timer.finished();
    }

    @SneakyThrows
    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (BankUtil.openClosest()) Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (DecantEvent.shouldDecant() && ScriptSettings.getSettingsData().decantpotions) {
            Logger.info("DECANT: " + new DecantEvent().executed());
        }

        if (OwnedItems.containsAnyUnworn(LOOT)) {
            Logger.info("Selling all items");
            new SellAllEvent(LOOT)
                    .setInterruptCondition(Combat::isInWild)
                    .execute();
            return ReactionGenerator.getNormal();
        }

        int muleOffGP = OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getSettingsData().moneyLeftAfterMuling;
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

    public static final int[] LOOT = new int[]{
            ItemID.AHRIMS_HOOD,
            ItemID.AHRIMS_STAFF,
            ItemID.AHRIMS_ROBETOP,
            ItemID.AHRIMS_ROBESKIRT,

            ItemID.DHAROKS_HELM,
            ItemID.DHAROKS_GREATAXE,
            ItemID.DHAROKS_PLATEBODY,
            ItemID.DHAROKS_PLATELEGS,

            ItemID.GUTHANS_HELM,
            ItemID.GUTHANS_WARSPEAR,
            ItemID.GUTHANS_PLATEBODY,
            ItemID.GUTHANS_CHAINSKIRT,

            ItemID.KARILS_COIF,
            ItemID.KARILS_CROSSBOW,
            ItemID.KARILS_LEATHERTOP,
            ItemID.KARILS_LEATHERSKIRT,

            ItemID.BOLT_RACK,

            ItemID.TORAGS_HELM,
            ItemID.TORAGS_HAMMERS,
            ItemID.TORAGS_PLATEBODY,
            ItemID.TORAGS_PLATELEGS,

            ItemID.VERACS_HELM,
            ItemID.VERACS_FLAIL,
            ItemID.VERACS_BRASSARD,
            ItemID.VERACS_PLATESKIRT,

            ItemID.DEATH_RUNE,
            ItemID.CHAOS_RUNE,
            ItemID.BLOOD_RUNE,
            ItemID.AMULET_OF_GLORY_UNCHARGED,
            ItemID.RING_OF_WEALTH_UNCHARGED,
            ItemID.MIND_RUNE,
            ItemID.SAPPHIRE_RING,
            ItemID.RING_OF_RECOIL,
            ItemID.EMERALD_RING,
            ItemID.TRIDENT_OF_THE_SEAS_FULL
    };
}
