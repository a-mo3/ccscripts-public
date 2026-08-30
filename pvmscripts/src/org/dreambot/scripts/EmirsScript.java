package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.GameStateListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.method.emirs.DoFight;
import org.dreambot.behaviour.method.emirs.FindMatch;
import org.dreambot.behaviour.method.emirs.GetOnEmirs;
import org.dreambot.behaviour.method.emirs.EmirsTutorial;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.scriptdata.CalvarionSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;

import java.awt.*;
import java.text.DecimalFormat;

public class EmirsScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot<CalvarionScript> tree = new FractalRoot(new CalvarionSettings(), getScriptName());
    int deathCount = 0;

    // set whenever you get the no charges left message
    // todo find inital charge state and manage it well
    boolean needsToRecharge = false;
    boolean hasLootInBag = true;

    @Override
    public void onArgs(String... args) {
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

//        WithdrawLoadoutEvent.sellList = LootSpindel.LOOT;
//        MuleOff.LOOT = LootSpindel.LOOT;

        Logger.info("Init");
        tree.setSimpleName("cCEmirs");

        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new TutorialTree().setSimpleName("Tut"),
                // pvp mule
                new GetMembershipBranch().setSimpleName("Get members"),

                // ensure on emirs world
                new GetOnEmirs().setSimpleName("W578"),
                new EmirsTutorial().setSimpleName("PVP Tutorial"),
                new DoFight().setSimpleName("ready & fight"),
                new FindMatch().setSimpleName("Find Match"),

                // need this just for settings
                new MuleOff()
        );
    }

    @Override
    public int onLoop() {
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    int startingPoints = getPoints();
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
                String.format("Points %d %d/hr", getPoints(), runtime.getHourlyRate(getPoints() - startingPoints)),
                String.format("gp value %s %s/hr", df.format(getPoints() * 750L), df.format(runtime.getHourlyRate((getPoints() - startingPoints) * 750)))
        };
    }

    private int getPoints() {
        return PlayerSettings.getBitValue(13991);
    }

    @Override
    public String getScriptName() {
        return "cCEmirs";
    }

    @Override
    public int getMoneyMade() {
        return (getPoints() - startingPoints) * 750;
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

    @Override
    public void onMessage(Message message) {
    }

    static Timer lastWorldHop = new Timer(3000);
    boolean wasHopping = false;

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
    }

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
