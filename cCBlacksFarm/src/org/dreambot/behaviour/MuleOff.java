package org.dreambot.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
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
            ItemID.BLACK_CHINCHOMPA,
            ItemID.AMULET_OF_GLORY_UNCHARGED,
            ItemID.RED_CHINCHOMPA_10034,
            ItemID.CHINCHOMPA
    };
    Area FALCONRY = new Area(2351, 3632, 2407, 3553);

    @Override
    public boolean isValid() {
        if (timer == null) timer = new Timer(ScriptSettings.getMuleOffTime());
        return (!FALCONRY.contains(Players.getLocal()) && timer.finished() && Skills.getRealLevel(Skill.HUNTER) >= 73)
                || OwnedItems.count(ItemID.COINS_995) > ScriptSettings.getSettingsData().maxTrainingGP;
    }

    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.open()) Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (OwnedItems.containsAnyUnworn(itemsToMule) && !ScriptSettings.getSettingsData().tradeOffChins) {
            new SellAllEvent(itemsToMule).execute();
            return ReactionGenerator.getNormal();
        }

        if (!Trade.isOpen() && shouldReset()) {
            Logger.info(String.format("Resetting Mule Timer Owned coins %d / %d",
                    OwnedItems.count(ItemID.COINS_995),
                    ScriptSettings.getMuleRemainder()));
            timer.reset();
            return ReactionGenerator.getNormal();
        }

        if (ScriptSettings.getSettingsData().tradeOffChins) {
            if (OwnedItems.containsAnyUnworn(ItemID.AMULET_OF_GLORY_UNCHARGED)) {
                Logger.info("Selling the uncharged glories");
                new SellAllEvent(ItemID.AMULET_OF_GLORY_UNCHARGED).execute();
                return ReactionGenerator.getNormal();
            }

            new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                    .addOfferedItem(ItemID.BLACK_CHINCHOMPA, OwnedItems.count(ItemID.BLACK_CHINCHOMPA))
                    .addOfferedItem(ItemID.AMULET_OF_GLORY_UNCHARGED, OwnedItems.count(ItemID.AMULET_OF_GLORY_UNCHARGED))
                    .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getSettingsData().gpRemainingAfterMuling)
                    .addOfferedItem(ItemID.RED_CHINCHOMPA_10034, OwnedItems.count(ItemID.RED_CHINCHOMPA_10034))
                    .execute();
            return ReactionGenerator.getNormal();
        }

        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getMuleRemainder())
                .execute();
        return ReactionGenerator.getNormal();
    }


    private boolean shouldReset() {
        if (OwnedItems.count(ItemID.COINS_995) > ScriptSettings.getSettingsData().maxTrainingGP) return false;
        if (ScriptSettings.getSettingsData().tradeOffChins) {
            return !OwnedItems.containsAnyUnworn(ItemID.RED_CHINCHOMPA_10034, ItemID.BLACK_CHINCHOMPA);
        } else {
            return OwnedItems.count(ItemID.COINS_995) <= ScriptSettings.getMuleRemainder();
        }
    }


    @Override
    public void onMessage(Message message) {
        String str = message.getMessage();
//        Logger.info(message.getType() + " on msg " + str);
    }
}
