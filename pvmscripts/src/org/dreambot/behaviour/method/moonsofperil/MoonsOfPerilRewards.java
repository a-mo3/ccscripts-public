package org.dreambot.behaviour.method.moonsofperil;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.scripts.MoonsOfPerilScript;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class MoonsOfPerilRewards extends Fractal {
    public static final Area REWARDS = new Area(1509, 9585, 1518, 9573);

    public MoonsOfPerilRewards(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Rewards");
    }

    @Override
    public int onLoop() {
        Prayers.toggleQuickPrayer(false);

        if (!REWARDS.contains(Players.getLocal())) {
            log("Go to rewards chamber");
            if (Walking.shouldWalk()) Walking.walk(REWARDS);
            return ReactionGenerator.getNormal();
        }

        ObjectUtil.interact("Lunar Chest");
        Sleep.sleepUntil(Widgets::isOpen, 16_400);

        WidgetChild rewards = Widgets.get(868, 5);
        if (rewards == null) {
            log("Failed to record the rewards");
            Sleep.sleep(5000);
            WidgetChild bank = Widgets.get(868, 20);
            Bank.resetCache();
            if (bank != null) bank.interact();
            return ReactionGenerator.getNormal();
        }

        for (WidgetChild reward : rewards.getChildren()) {
            if (reward == null) continue;
            if (reward.getItem() == null) {
                log("No reward");
                continue;
            }

            log("Reward " + reward.getItemId() + "*" + reward.getItemStack());
            MoonsOfPerilScript.grossGp += LivePrices.get(reward.getItemId()) * reward.getItemStack();
        }

        // invalidate cache because it never gets tracked into cache state and causes never muling
        Bank.resetCache();
        // click bank widget
        WidgetChild bank = Widgets.get(868, 20);
        if (bank != null) bank.interact();
        return ReactionGenerator.getNormal();
    }
}
