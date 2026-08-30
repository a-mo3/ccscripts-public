package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.behaviour.tutorial.WalkAbout;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.fractals.util.StopScriptFractal;
import org.dreambot.scriptdata.TutorialScriptSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

public class TutorialScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<TutorialScriptSettings> tree = new FractalRoot<>(new TutorialScriptSettings(), getScriptName());

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCTutorial");
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        tree.addChildren(
                new WalkAbout(tree.getSettings().walkAboutTime).setSimpleName("Walk about"),
                new StopScriptFractal()
        );
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) tree.run();

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
                "Tut state: " + MyVarps.getTutVarp()
        };
    }

    @Override
    public String getScriptName() {
        return "cCTutorialIsland";
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
        if (!Combat.isInWild()) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Combat.isInWild()) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
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
