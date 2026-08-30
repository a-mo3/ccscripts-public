package org.dreambot.behaviour.method.blastfurnace;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.BuyLimitData;
import org.dreambot.fractals.util.BuyLimitManager;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.scriptdata.BlastFurnaceSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;

public class BlastFurnaceRestock extends Fractal {
    final List<Integer> requiredOres;

    public BlastFurnaceRestock(List<Integer> requiredOres) {
        // im expecting this to accept when one or more of the required ores are on timer
        super(() -> Bank.isCached()
                && requiredOres.stream().anyMatch(x -> !OwnedItems.contains(x) // you dont have any
                && BuyLimitManager.get().getBrought(x) == 13_000) // and you've reached the buy limit
        );
        this.requiredOres = requiredOres;
    }

    @Override
    public int onLoop() {
        if (SettingsRepository.findInstanceOf(new BlastFurnaceSettings()).restockStrategy == BlastFurnaceRestockStrategy.BREAK) {
            // sell any bars you own so you have gp to make a buy offer
            if (OwnedItems.containsAny(MuleOff.LOOT)) {
                log("Sell all bars");
                new SellAllEvent(MuleOff.LOOT)
                        .execute();
                return ReactionGenerator.getNormal();
            }

            if (requiredOres.stream().allMatch(GrandExchange::contains)) {
                log("Time to take a break");
                // log off until
                long lowestLimit = requiredOres.stream().mapToLong(x -> {
                    BuyLimitData d = BuyLimitManager.get().getData(x);
                    if (d == null) return Long.MAX_VALUE;
                    return System.currentTimeMillis() - d.getFirstBuyTimestamp();
                }).min().orElse(Long.MAX_VALUE);

                if (lowestLimit == Long.MAX_VALUE) {
                    Logger.error("Terrible math error when taking a break");
                    return ReactionGenerator.getNormal();
                }

                Client.getInstance().getRandomManager().disableSolver(RandomEvent.LOGIN);
                log("Taking a break until we can buy more ores " + lowestLimit);
                Sleep.sleep(60 * 1000 * 60 * 4 - lowestLimit);
                // get afk logged
                Client.getInstance().getRandomManager().enableSolver(RandomEvent.LOGIN);
                return ReactionGenerator.getNormal();
            }

            // set a buy listing
            if (!GrandExchange.isOpen()) {
                log("Open GE");
                if (Walking.shouldWalk()) GrandExchange.open();
                return ReactionGenerator.getNormal();
            }
            int spend = OwnedItems.count(ItemID.COINS_995);
            for (Integer r : requiredOres) {
                if (GrandExchange.contains(r)) continue;
                log("Adding offer for " + r);
                GrandExchange.buyItem(r, Math.min(spend / LivePrices.get(r), 13_000), LivePrices.get(r));
            }
            return ReactionGenerator.getNormal();
        }

        // todo request items from mule on reverse state (make bulk buying script first)
        if (SettingsRepository.findInstanceOf(new BlastFurnaceSettings()).restockStrategy == BlastFurnaceRestockStrategy.REVERSE_MULE) {
            MuleRequestEvent e = new MuleRequestEvent("Blast furnace restock");
            for (Integer r : requiredOres) {
                if (BuyLimitManager.get().getBrought(r) == 13_000 && !OwnedItems.contains(r)) {
                    log("Need more " + r + " requesting 2k");
                }
                e.addRequiredItem(r + 1, 2000);
            }

            log("Making mule request.");
            e.execute();
        }
        return ReactionGenerator.getNormal();
    }
}
