package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.settings.Varcs;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.ui.Gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class FtpTutorial extends PseudoScript implements ItemContainerListener {
    Fractal tree = new Fractal();
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    final static int PLAY_TIME_VARCINT = 526;
    private long lastTimestamp;
    private final Timer playTimer = new Timer(60 * 60 * 1000 * 20);
    private final Timer checkPlayTime = new Timer(30 * 60 * 1000);
    private boolean checkedPlayTime;

    @Override
    public void onStart(String... args) {
        init();
    }

    @Override
    public void init() {
        SettingsLoader.changePath("cCTutorialFarm");
        Client.getInstance().addEventListener(this);


        tree.setSimpleName("cCTutorialFarm");
        tree.addChildren(
                new TutorialTree().setSimpleName("Tutorial island")
        );
//        new AIAntiban();
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
                "cCTutorialFarm: " + runtime.formatTime(),
        };
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

    public static boolean isUnrestricted() {
        return Skills.getTotalLevel() >= 100 && Quests.getQuestPoints() >= 10 && Varcs.getInt(PLAY_TIME_VARCINT) >= 1200;
    }

    // todo gp/hr testing
    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (Gui.wasDiscordButtonClicked(e.getPoint())) {
            try {
                Desktop.getDesktop().browse(new URI(""));
            } catch (IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (Gui.wasButtonClicked(e.getPoint())) {
        }
    }
}
