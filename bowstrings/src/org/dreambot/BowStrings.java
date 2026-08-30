package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.Crafting;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.SpinSomething;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.settings.BondSettings;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.script.SettingsData;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettings;
import org.dreambot.settings.ui.Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCBowStrings", author = "camalCase", version = 0.0)
public class BowStrings extends AbstractScript implements PaintInfo, HumanMouseListener, ChatListener, ItemContainerListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();
    public static int deathCount = 0;
    boolean shouldHop = false;
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    @Override
    public void onStart(String... params) {
        super.onStart(params);
        init();
    }

    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();

    @Override
    public boolean onSolverStart(RandomSolver solver) {
        if (solver instanceof BreakSolver) isBreaking.set(true);
        return super.onSolverStart(solver);
    }

    @Override
    public void onSolverEnd(RandomSolver solver) {
        if (solver instanceof BreakSolver) isBreaking.set(false);
        super.onSolverEnd(solver);
    }

    private void init() {
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        ScriptSettings.setSettingsData(settings);
        new WebhookListener();
        
        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));
        tree.setSimpleName("cCBowStrings")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),
                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Getting membership"),
                        new MuleOff().setSimpleName("Mule Off"),
                        new Crafting(() -> true).setSimpleName("Crafting")
                );
//        new AIAntiban();
    }


    public static boolean hasLootInBag;
    private long loopSpd;
    private long lastTimestamp;

    @Override
    public int onLoop() {
        loopSpd = System.currentTimeMillis() - lastTimestamp;
        lastTimestamp = System.currentTimeMillis();
        if (ClientSettings.isAcceptAidEnabled()) {
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getNormal();
        }

        if (shouldHop && !Players.getLocal().isInCombat()) {
            if (WorldHopper.hopWorld(
                    Worlds.getRandomWorld(x -> !x.isF2P() && x.isNormal() && x.getMinimumLevel() < Combat.getCombatLevel())
            )) shouldHop = false;
            return ReactionGenerator.getNormal();
        }
//
//        if (ScriptSettings.getSettingsData().useLootingBag && hasLootInBag) {
//            if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null) {
//                new EmptyLootingBagEvent().executed();
//            }
//        }


        if (ClientSettings.isSellPriceWarningEnabled()) {
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleSellPriceWarning(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isBuyPriceWarningEnabled()) {
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleBuyPriceWarning(false);
            return ReactionGenerator.getNormal();
        }
        return tree.run();
    }

    @Override
    public void onExit() {
        Client.getInstance().removeEventListener(webhookListener);
        AnalyticsReporter.stop();
    }

    Tile safe = new Tile(2970, 3604);

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
            SwingUtilities.invokeLater(Gui::new);
        }
    }

    @Override
    public void onPaint(Graphics graphics) {
        Alerts.renderList(graphics);
Gui.paintDiscordButton(graphics);
        Gui.paintButton(graphics);
        scriptPaint.paint(graphics);
        fractalPaint.paint(graphics);

//        NPC dragon = NPCs.closest(x -> x.getIndex() == 8507);
//        if (dragon != null) {
//            graphics.setColor(Color.RED);
//            graphics.drawPolygon(dragon.getServerTile().getPolygon());
//        }
//        graphics.setColor(Color.GREEN);
//        graphics.drawPolygon(safe.getPolygon());
    }

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
                "cCBowStrings " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Time Until Mule off: " + muleOff,
                "LoopSpd: " + loopSpd,
                String.format("Earned %s (%s / hr))", df.format(grossGp), df.format(runtime.getHourlyRate(grossGp))),
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

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            shouldHop = true;
            deathCount++;
        }
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (SpinSomething.LOOM_AREA.equals(Players.getLocal().getTile()) && item.getID() == ItemID.BOW_STRING) {
            grossGp += item.getLivePrice() * item.getAmount();
        }
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (SpinSomething.LOOM_AREA.equals(Players.getLocal().getTile()) && incoming.getID() == ItemID.BOW_STRING) {
            grossGp += incoming.getLivePrice();
        }
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (SpinSomething.LOOM_AREA.equals(Players.getLocal().getTile()) && incoming.getID() == ItemID.BOW_STRING) {
            grossGp += incoming.getLivePrice();
        }
    }
}
