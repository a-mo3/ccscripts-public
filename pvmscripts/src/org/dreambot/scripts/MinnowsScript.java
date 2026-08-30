package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.method.minnows.Minnows;
import org.dreambot.behaviour.method.trawler.Trawler;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.fishingcontest.FishingBranch;
import org.dreambot.behaviour.quests.fishingcontest.FishingContest;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.MinnowsSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

public class MinnowsScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<MinnowsSettings> tree = new FractalRoot<>(new MinnowsSettings(), getScriptName());

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCMinnowsScript");

        MuleOff.LOOT = new int[]{
                ItemID.SHARK,
                ItemID.RAW_SHARK
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        tree.addChildren(

                new EmptyDeathsCoffer().setSimpleName("Empty grave"),
                new FishingBranch(() -> Skills.getRealLevel(Skill.FISHING) < 82).setSimpleName("Get 82 fishing"),

                new GetMembershipBranch().setSimpleName("Get membership")
                        .setPrependLogic(() -> {
                                    if (tree.getSettings().stopAfterFishing) {
                                        System.exit(0);
                                        return true;
                                    }
                                    return false;
                                }
                        ),
                new FishingContest().setSimpleName("Fishing contest"),
                new Trawler().setSimpleName("Trawl"),
                new MuleOff()
                        .setPrependLogic(() -> {
                            NPC kylieMinnow = NPCs.closest("Kylie minnow");
                            if (Inventory.count(ItemID.MINNOW) >= 40 && kylieMinnow != null) {
                                if (Dialogues.canEnterInput()) {
                                    Keyboard.type("2m", true);
                                    return true;
                                }
                                if (Dialogues.inDialogue()) {
                                    Dialog.solve("Yes");
                                    return true;
                                }

                                kylieMinnow.interact("Talk-to");
                                Antiban.sleepUntil(Dialogues::inDialogue, 4400);
                                return true;
                            }
                            return false;
                        }).setSimpleName("Mule"),
                new Minnows()
        );
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) tree.run();

        if (ClientSettings.isLevelUpInterfaceEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable level up message");
            ClientSettings.toggleLevelUpInterface(false);
            return ReactionGenerator.getNormal();
        }

        if (!Client.isLoggedIn()) return ReactionGenerator.getNormal();
        if (Client.getGameStateID() == 45) return ReactionGenerator.getNormal();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "In combat " + Players.getLocal().isInCombat(),
                "Tick " + Client.getGameTick(),
        };
    }

    @Override
    public String getScriptName() {
        return "cCMinnowsFarm";
    }

    @Override
    public int getMoneyMade() {
        return grossGp;
    }

    @Override
    public Timer getRuntime() {
        return runtime;
    }

    @Override
    public long getMuleOffTime() {
        return MuleOff.timer == null ? 0 : MuleOff.timer.remaining();
    }

    @Override
    public Fractal getFractal() {
        return tree;
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (item.getId() != ItemID.MINNOW) return;
        grossGp += (LivePrices.get(ItemID.SHARK) / 40) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) return;

        grossGp += (LivePrices.get(ItemID.SHARK) / 40) * quantity;
    }

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
    }

    int deathCount = 0;

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            deathCount++;
        }
    }
}
