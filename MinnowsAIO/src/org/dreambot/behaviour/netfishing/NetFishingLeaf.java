package org.dreambot.behaviour.netfishing;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.framework.ReactionGenerator;

import java.util.ArrayList;
import java.util.List;

public class NetFishingLeaf extends Leaf {
    public static final Area LUMMY_SHRIMP = new Area(3237, 3159, 3247, 3144);

    public static final int SMALL_FISHING_NET = 303;
    public static final int RAW_ANCHOVIES = 321;
    public static final int RAW_SHRIMPS = 317;

    List<Integer> allowedItems = new ArrayList<>() {{
        add(RAW_SHRIMPS);
        add(RAW_ANCHOVIES);
        add(SMALL_FISHING_NET);
    }};

    @Override
    public boolean isValid() {
        return Skills.getRealLevel(Skill.FISHING) < 10;
    }

    @Override
    public int onLoop() {
        if (Inventory.contains(x -> !allowedItems.contains(x.getID()))) {
            Logger.log("deposit disallowed items - " + BankLocation.getNearest());
            if (Walking.shouldWalk() && Bank.open()) {
                Bank.depositAll(x -> !allowedItems.contains(x.getID()));
            }
            return ReactionGenerator.getNormal();
        } else Logger.log("has only allowed items");


        if (!Inventory.contains(SMALL_FISHING_NET)) {
            // todo add event here
            if (Walking.shouldWalk() && Bank.open()) {
                Bank.withdraw(SMALL_FISHING_NET, 1);
            }
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isFull()) {
            Inventory.dropAll(x -> !x.getName().equalsIgnoreCase("small fishing net"));
            return ReactionGenerator.getNormal();
        }

        if (!LUMMY_SHRIMP.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(LUMMY_SHRIMP.getCenter());
            return ReactionGenerator.getNormal();
        }

        NPC fishingSpot = NPCs.closest(x -> x.getName().equals("Fishing spot") && LUMMY_SHRIMP.contains(x));
        if (fishingSpot != null && fishingSpot.interact("Net")) {
            Sleep.sleepUntil(() -> Inventory.isFull() || Dialogues.inDialogue(), Players.getLocal()::isAnimating, 2000, 100);
        }

        return ReactionGenerator.getNormal();
    }
}
