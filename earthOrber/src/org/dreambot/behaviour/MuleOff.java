package org.dreambot.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.trade.Trade;
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
            ItemID.SAPPHIRE_RING,
            ItemID.RING_OF_RECOIL,
            ItemID.RING_OF_DUELING8,
            ItemID.EMERALD_RING,
            ItemID.AMULET_OF_GLORY_UNCHARGED,
            ItemID.EARTH_ORB
    };

    @Override
    public boolean isValid() {
        if (timer == null) timer = new Timer(ScriptSettings.getMuleOffTime());
        if (Skills.getRealLevel(Skill.MAGIC) < 60) {
            timer.reset();
            return false;
        }
        return timer.finished();
    }

    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.open()) Bank.close();
            return ReactionGenerator.getNormal();
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

    @Override
    public void onMessage(Message message) {
        String str = message.getMessage();
//        Logger.info(message.getType() + " on msg " + str);
    }
}
