package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.spindel.ExitWithLoot;
import org.dreambot.behaviour.method.tanning.TanHide;
import org.dreambot.behaviour.method.tanning.TanningHide;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
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
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.TannerSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;

public class TannerScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<TannerSettings> tree = new FractalRoot<>(new TannerSettings(), getScriptName());

    @Override
    public void onArgs(String... args) {
        for (String arg : args) {
        }
    }


    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        MuleOff.LOOT = new int[]{
                ItemID.LEATHER,
                ItemID.RED_DRAGON_LEATHER,
                ItemID.GREEN_DRAGON_LEATHER,
                ItemID.BLUE_DRAGON_LEATHER,
                ItemID.BLACK_DRAGON_LEATHER,
                ItemID.RING_OF_WEALTH,
                ItemID.AMULET_OF_GLORY_UNCHARGED
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        Logger.info("Init");
        tree.setSimpleName("cCTanner");

        tree.addChildren(

                new Fractal(() -> tree.getSettings().selectedHide == TanningHide.SOFT_LEATHER)
                        .addChildren(
                                new MuleOff().setSimpleName("Mule off"),
                                new TanHide(tree.getSettings().selectedHide, tree.getSettings().restock, tree.getSettings().priceIncrease)
                        )
                        .setSimpleName("Ftp tanning"),

                // todo no membership if its ftp tanning
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new EmptyDeathsCoffer().setSimpleName("Empty coffer"),
                new MuleOff().setSimpleName("Mule off"),
                new TanHide(tree.getSettings().selectedHide, tree.getSettings().restock, tree.getSettings().priceIncrease)
                        .setSimpleName("Tan hide: " + tree.getSettings().selectedHide)

        );
//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();

        if (!Combat.isInWild() && !Bank.isCached()) {
            if (!SpecialWalker.leaveAvasRoom()) return ReactionGenerator.getNormal();
            if (Bank.isOpen()) Bank.close();
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }



        // dont run the tree while hopping worlds because equipment state will make you do loadouts you shouldnt
        if (Client.getGameStateID() == 45) return ReactionGenerator.getQuick();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    Timer cacheTime = new Timer(5 * 1000);
    int invValue = -1;

    @Override
    public String[] getPaintInfo() {
        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        Player local = Players.getLocal();
        String target = "";
        if (local != null) {
            Character tgt = local.getInteractingCharacter();
            if (tgt != null) target = tgt.getName();
        }

        // todo something here for melee mode
        if (cacheTime.finished()) {
            cacheTime.reset();
            invValue = ExitWithLoot.inventoryValue();
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
        };
    }

    @Override
    public String getScriptName() {
        return "cCTannerFarm";
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
    public void onScriptPaint(Graphics g) {
    }

    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        DecimalFormat format = new DecimalFormat("00");
        return String.format("%s:%s:%s",
                format.format(hours),
                format.format(minutes),
                format.format(seconds));
    }


    public void onInventoryItemAdded(Item item) {
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        TanningHide swapped = Arrays.stream(TanningHide.values()).filter(x -> existing.getId() == x.precursorId)
                .findFirst().orElse(null);
        if (swapped != null) {
            Logger.info("Made " + swapped);
            grossGp += swapped.profit();
        }
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
    }
}
