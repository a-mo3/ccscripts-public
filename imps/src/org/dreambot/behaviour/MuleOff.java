package org.dreambot.behaviour;


import lombok.SneakyThrows;
import org.dreambot.ImpFarm;
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
        if (!ImpFarm.isUnrestricted()) return false;
        return !Combat.isInWild() && timer.finished();
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
            timer.reset();
            return ReactionGenerator.getNormal();
        }

        Logger.info("Making mule request");
        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getSettingsData().moneyLeftAfterMuling)
                .execute();
        return ReactionGenerator.getNormal();
    }

    public static final int[] LOOT = new int[]{
            ItemID.BLACK_BEAD,
            ItemID.RED_BEAD,
            ItemID.YELLOW_BEAD,
            ItemID.WHITE_BEAD,
            ItemID.FIENDISH_ASHES,
            ItemID.LOBSTER,
            ItemID.IRON_KITESHIELD,
            ItemID.EARTH_TALISMAN,
            ItemID.MIND_TALISMAN,
            ItemID.BLUE_WIZARD_HAT,
            ItemID.CHEFS_HAT,
            ItemID.BREAD_DOUGH

    };
}
