package org.dreambot.behaviour.method.mta;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class BuyMTAReward extends Fractal {
    final Area MTA_STORE = new Area(3359, 3322, 3367, 3314, 1);
    MTAReward desiredReward;

    public BuyMTAReward(Supplier<Boolean> acceptCondition, MTAReward reward) {
        super(acceptCondition);

        this.desiredReward = reward;
        MTANodes.init();
        setSimpleName("Buy reward");
    }

    @Override
    public int onLoop() {
        if (Bank.isOpen()) {
            log("Bank close");
            Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (!MTA_STORE.contains(Players.getLocal())) {
            slowLog("Go to MTA store");
            if (Walking.shouldWalk()) Walking.walk(MTA_STORE);
            return ReactionGenerator.getNormal();
        }

        if (!Widgets.isOpen()) {
            log("Open store");
            NPC rewardGuy = NPCs.closest("Rewards Guardian");
            if (rewardGuy != null) {
                rewardGuy.interact("Trade-with");
                Sleep.sleepUntil(Widgets::isOpen, 4000);
            } else {
                log("Cant find reward guy");
            }
            return ReactionGenerator.getNormal();
        }

        if (!desiredReward.isSelected()) {
            log("Selecting reward " + desiredReward);
            WidgetChild wc = Widgets.get(x -> x.hasAction("Select " + desiredReward.itemName));
            if (wc != null) {
                log("interact");
                wc.interact();
            } else {
                log("Failed to find reward " + desiredReward.itemName);
            }
            return ReactionGenerator.getNormal();
        }

        WidgetChild confirm = Widgets.get(x -> x.hasAction("Confirm"));
        if (confirm != null) {
            log("confirm sale");
            confirm.interact();
            Sleep.sleepTicks(2);
            Widgets.closeAll();
        } else {
            log("Failed to find confirm ");
        }


        return ReactionGenerator.getNormal();
    }
}
