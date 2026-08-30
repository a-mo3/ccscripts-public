package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.GameStateListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.training.fletching.FletchLogs;
import org.dreambot.behaviour.training.fletching.HeadlessArrows;
import org.dreambot.behaviour.training.fletching.StringBows;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.FletchSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;

public class FletchingScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot<FletchSettings> tree = new FractalRoot(new FletchSettings(), getScriptName());

    @Override
    public void onArgs(String... args) {
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

        MuleOff.LOOT = new int[]{
                ItemID.SHORTBOW,
                ItemID.OAK_LONGBOW,
                ItemID.OAK_SHORTBOW,
                ItemID.MAPLE_LONGBOW,
                ItemID.MAPLE_SHORTBOW,
                ItemID.WILLOW_LONGBOW,
                ItemID.WILLOW_SHORTBOW,
                ItemID.YEW_LONGBOW,
                ItemID.YEW_SHORTBOW,
                ItemID.MAGIC_LONGBOW,
                ItemID.MAGIC_SHORTBOW
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        Logger.info("Init");
        tree.setSimpleName("cCFletchingFarm");

        boolean fletchFromLogs = tree.getSettings().fletchFromLogs;
        boolean stringMagics = tree.getSettings().stringMagics;
        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),

                new LampHandler().setSimpleName("lamp handler"),
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new MuleOff().setSimpleName("Muling"),
                new HeadlessArrows(() -> Skills.getRealLevel(Skill.FLETCHING) < 20)
                        .setSimpleName("Make headless arrows"),
                new FletchLogs(ItemID.OAK_LOGS, ItemID.OAK_SHORTBOW_U, 200, 25, fletchFromLogs),
                new StringBows(() -> Skills.getRealLevel(Skill.FLETCHING) < 25, ItemID.OAK_SHORTBOW_U, 600, ItemID.OAK_SHORTBOW)
                        .setSimpleName("Oak shortbow"),
                new FletchLogs(ItemID.OAK_LOGS, ItemID.OAK_LONGBOW_U, 700, 40, fletchFromLogs)
                        .setSimpleName("Fletch oaks"),
                new StringBows(() -> Skills.getRealLevel(Skill.FLETCHING) < 40, ItemID.OAK_LONGBOW_U, 700, ItemID.OAK_LONGBOW)
                        .setSimpleName("Oak longbow"),
                new FletchLogs(ItemID.WILLOW_LOGS, ItemID.WILLOW_LONGBOW_U, 200, 55, fletchFromLogs)
                        .setSimpleName("Fletch logs"),
                new StringBows(() -> Skills.getRealLevel(Skill.FLETCHING) < 55, ItemID.WILLOW_LONGBOW_U, 200, ItemID.WILLOW_LONGBOW)
                        .setSimpleName("Willow longbow"),
                new FletchLogs(ItemID.MAPLE_LOGS, ItemID.MAPLE_LONGBOW_U, 2600, 70, fletchFromLogs),
                new StringBows(() -> Skills.getRealLevel(Skill.FLETCHING) < 70, ItemID.MAPLE_LONGBOW_U, 2600, ItemID.MAPLE_LONGBOW)
                        .setSimpleName("Maple longbow"),

                new FletchLogs(ItemID.MAGIC_LOGS, ItemID.MAGIC_LONGBOW_U, 2600, 100, fletchFromLogs,true)
                        .setSimpleName("Fletch magics"),
                new StringBows(() -> Skills.getRealLevel(Skill.FLETCHING) >= 85 && stringMagics,
                        ItemID.MAGIC_LONGBOW_U, 2600, ItemID.MAGIC_LONGBOW)
                        .setSimpleName("Magic longbow"),

                new FletchLogs(ItemID.YEW_LOGS, ItemID.YEW_LONGBOW_U, 2600, 100, fletchFromLogs)
                        .setSimpleName("Fletch yews"),
                new StringBows(() -> true, ItemID.YEW_LONGBOW_U, 2600, ItemID.YEW_LONGBOW)
                        .setSimpleName("Yew longbow")
        );
    }

    @Override
    public int onLoop() {
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

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
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
        };
    }


    @Override
    public String getScriptName() {
        return "cCFletchingFarm";
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
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        grossGp += incoming.getLivePrice() - outgoing.getLivePrice();
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

    @Override
    public void onMessage(Message message) {
    }

    static Timer lastWorldHop = new Timer(3000);
    boolean wasHopping = false;

    @Override
    public void onGameStateChange(GameState gameState) {
        if (gameState == GameState.HOPPING) {
            Logger.info("World hop");
            wasHopping = true;
            return;
        }

        if (wasHopping && gameState == GameState.LOGGED_IN) {
            Logger.info("Reset last world hop timer");
            lastWorldHop.reset();
            wasHopping = false;
        }
    }
}
