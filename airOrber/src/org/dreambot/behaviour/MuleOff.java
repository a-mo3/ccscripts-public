package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Timer;
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
            ItemID.AIR_ORB,
            ItemID.SAPPHIRE_RING,
            ItemID.RING_OF_RECOIL,
            ItemID.RING_OF_DUELING8,
            ItemID.EMERALD_RING,
            ItemID.AMULET_OF_GLORY_UNCHARGED
    };

    @Override
    public boolean isValid() {
        if (timer == null) timer = new Timer(ScriptSettings.getMuleOffTime());
        if (Skills.getRealLevel(Skill.MAGIC) < 66) {
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

        if (OwnedItems.containsAnyUnworn(itemsToMule)) {
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
