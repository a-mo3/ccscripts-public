package org.dreambot.behaviour.training.slayer.behaviour;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetTaskLeaf extends Fractal {
    private static final Area TURAEL_HOUSE = new Area(2930, 3538, 2933, 3535);
    public static final Area NIEVE = new Area(2430, 3426, 2434, 3421);

    //NPC messages
    private static final Pattern NPC_ASSIGN_MESSAGE = Pattern.compile(".*(?:Your new task is to kill|You are to bring balance to)\\s*(?<amount>\\d+) (?<name>.+?)(?: (?:in|on|south of) (?:the )?(?<location>.+))?\\.");
    private static final Pattern NPC_ASSIGN_BOSS_MESSAGE = Pattern.compile("^(?:Excellent\\. )?You're now assigned to (?:kill|bring balance to) (?:the )?(.*) (\\d+) times.*Your reward point tally is (.*)\\.$");
    private static final Pattern NPC_ASSIGN_FIRST_MESSAGE = Pattern.compile("^We'll start you off (?:hunting|bringing balance to) (.*), you'll need to kill (\\d*) of them\\.$");
    private static final Pattern NPC_CURRENT_MESSAGE = Pattern.compile("^You're (?:still(?: meant to be)?|currently assigned to) (?:hunting|bringing balance to|kill|bring balance to|slaying) (?<name>.+?)(?: (?:in|on|south of) (?:the )?(?<location>.+))?(?:, with|; (?:you have|only)) (?<amount>\\d+)(?: more)? to go\\..*");


    @Override
    public boolean isValid() {
        return SlayerBranch.task == null || SlayerBranch.task.equals("new");
    }

    InventoryLoadout getGemLoadout = new InventoryLoadout()
            .addItem(ItemID.COINS_995, 1000)
            .addItem(ItemID.ENCHANTED_GEM, 1)
            .setEnabledCondition(() -> OwnedItems.contains(ItemID.ENCHANTED_GEM))
//            .addItem(ItemVariant.GAMES_NECKLACE.minCharges(2))
            .addItem(ItemID.GAMES_NECKLACE8, 1, 3);
    Area GRAND_EXCHANGE = new Area(3149, 3509, 3180, 3467);

    @Override
    public int onLoop() {
        if (SlayerBranch.task == null || !Bank.isCached()) {
            if (!Bank.isCached()) {
                Logger.info("Getting bank cache");
                if (Bank.isOpen()) Bank.close();
                if (Walking.shouldWalk(8)) Bank.open();
                return ReactionGenerator.getNormal();
            }

            if (!OwnedItems.contains(ItemID.ENCHANTED_GEM)) {
                Logger.info("Buying gem");
                if (!getGemLoadout.isFulfilled() && !GRAND_EXCHANGE.contains(Players.getLocal())) {
                    if (Walking.shouldWalk(8)) Walking.walk(GRAND_EXCHANGE.getRandomTile());
                    return ReactionGenerator.getNormal();
                }

                if (!getGemLoadout.isFulfilled() && GRAND_EXCHANGE.contains(Players.getLocal())) {
                    Logger.info("Fulfilling get gem loadout");
                    new WithdrawLoadoutEvent(getGemLoadout, null)
                            .setBuyRemainder(true)
                            .executed();
                    return ReactionGenerator.getQuick();
                }

                if (!getSlayerMasterArea().contains(Players.getLocal())) {
                    Logger.info("Walking to slayer master");
                    if (Walking.shouldWalk(8)) Walking.walk(getSlayerMasterArea());
                    return ReactionGenerator.getQuick();
                }

                if (!Shop.isOpen()) {
                    Logger.info("Open shop");
                    NPC turael = getSlayerMaster();
                    if (turael != null && turael.interact("Trade")) {
                        Sleep.sleepUntil(Shop::isOpen, 2400);
                    }
                    return ReactionGenerator.getQuick();
                }

                Logger.info("Buy gem");
                Shop.purchaseOne(ItemID.ENCHANTED_GEM);
                return ReactionGenerator.getQuick();
            }

            if (Shop.isOpen()) {
                Shop.close();
                return ReactionGenerator.getQuick();
            }
            if (!Inventory.contains(ItemID.ENCHANTED_GEM)) {
                if (!Bank.isOpen()) {
                    Bank.open();
                    return ReactionGenerator.getQuick();
                }

                Bank.withdraw(ItemID.ENCHANTED_GEM);
                return ReactionGenerator.getNormal();
            }

            Logger.info("Check gem");
            Widgets.closeAll();
            Inventory.interact(ItemID.ENCHANTED_GEM, "Check"); // RangeSlayerBranch listener will update ur task
            Sleep.sleepTicks(2);
            return ReactionGenerator.getQuick();
        }

        // check dialogue for assignment
        if (Dialogues.inDialogue() && Dialogues.getNPCDialogue() != null) {
            String npcText = Dialogues.getNPCDialogue();
            if (npcText == null) {
                Logger.info("NPC text null");
                return ReactionGenerator.getNormal();
            }
            final Matcher mAssign = NPC_ASSIGN_MESSAGE.matcher(npcText); // amount, name, (location)
            final Matcher mAssignFirst = NPC_ASSIGN_FIRST_MESSAGE.matcher(npcText); // name, number
            final Matcher mAssignBoss = NPC_ASSIGN_BOSS_MESSAGE.matcher(npcText); // name, number, points
            final Matcher mCurrent = NPC_CURRENT_MESSAGE.matcher(npcText); // name, (location), amount
            Log.info(npcText);
            if (mAssign.find()) {
                String name = mAssign.group("name");
                int amount = Integer.parseInt(mAssign.group("amount"));
                String location = mAssign.group("location");
                Log.info("mAssign: " + name + " " + "amount " + amount);
            } else if (mCurrent.find()) {
                String name = mCurrent.group("name");
                int amount = Integer.parseInt(mCurrent.group("amount"));
                String location = mCurrent.group("location");
                Log.info("mAssign: " + name + " " + "amount " + amount);
            }
        }

        if (!getSlayerMasterArea().contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(getSlayerMasterArea());
            return ReactionGenerator.getQuick();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("What's a slayer?", "can you teach me?", "Okay, great!");
            Sleep.sleepTicks(2);
            if (PlayerSettings.getConfig(394) > 0) {
                Widgets.closeAll();
            Inventory.interact(ItemID.ENCHANTED_GEM, "Check"); // RangeSlayerBranch listener will update ur task
            }
            return ReactionGenerator.getQuick();
        }

        NPC turael = getSlayerMaster();
        if (!Dialogues.inDialogue() && turael != null && turael.interact("Assignment")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 5000);
        }
        return ReactionGenerator.getQuick();
    }

    private NPC getSlayerMaster() {
        return Combat.getCombatLevel() >= 85 ? NPCs.closest(x -> x.getName().contains("ieve")) : NPCs.closest("Turael");
    }

    private Area getSlayerMasterArea() {
        return Combat.getCombatLevel() >= 85 ? NIEVE : TURAEL_HOUSE;
    }
}
