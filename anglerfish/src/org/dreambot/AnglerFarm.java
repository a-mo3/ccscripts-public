package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.Anglers;
import org.dreambot.behaviour.ItemMuleOff;
import org.dreambot.behaviour.LampHandler;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.fishing.FishingFractal;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractEvent;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCAnglerFarm", author = "camalCase", version = 0.02)
public class AnglerFarm extends AbstractScript implements PaintInfo, HumanMouseListener, ChatListener, ItemContainerListener {
    static Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();
    public static int deathCount = 0;
    public static final Area BARB_VILLAGE_FISHING = new Area(3101, 3422, 3111, 3435);

    DecimalFormat df = new DecimalFormat("###,###,###");
    int netMade = 0;

    public static int getHourlyDeathCount() {
        return runtime.getHourlyRate(deathCount);
    }

    boolean shouldHop = false;
    public static final Area MID_DRAGON_AREA = new Area(3197, 3843, 3214, 3830);

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

    public static final int MIN_HP = 15;
    public static final Area SHRIMP_AREA = new Area(3240, 3159, 3246, 3141);
    boolean hasSetLocation;

    private void init() {
        AbstractResponseEvent.globalBreakCondition = Combat::isInWild; // quest / training loadouts can get you stuck if they activate in wild
        AbstractEvent.globalInterruptCondition = Combat::isInWild;
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        ScriptSettings.setSettingsData(settings);
        new WebhookListener();
        
        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));
        tree.setSimpleName("cCAnglerFarm")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("Lamp handler"),
                        new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) < 20,
                                SHRIMP_AREA, () -> NPCs.closest(n -> n.hasAction("Net") && SHRIMP_AREA.contains(n)))
                                .setShouldBank(false)
                                .setInteraction("Net")
                                .setSimpleName("Shrimp until lvl 20")
                                .setInventoryLoadout(
                                        new InventoryLoadout()
                                                .strictIgnore(ItemID.RAW_SHRIMPS, ItemID.RAW_ANCHOVIES)
                                                .addItem(FishingFractal.SMALL_FISHING_NET, 1)
                                                .setStrict(true)
                                ),
                        new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) < 82,
                                BARB_VILLAGE_FISHING, () -> NPCs.closest(n -> n.hasAction("Lure") && BARB_VILLAGE_FISHING.contains(n)))
                                .setShouldBank(false)
                                .setInteraction("Lure")
                                .setSimpleName("Salmon/Trout until 82")
                                .setInventoryLoadout(new InventoryLoadout()
                                        .setStrict(true)
                                        .strictIgnore(ItemID.RAW_SALMON, ItemID.RAW_TROUT)
                                        .addItem(FishingFractal.FLY_FISHING_ROD, 1)
                                        .addItem(FishingFractal.FEATHER, 1, 5000)
                                        .setMuleRequestAmount(30_000)
                                ),
                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get membership")
                                .setAppendLogic(() -> {
                                            if (ScriptSettings.getSettingsData().stopAfterFishing) {
                                                System.exit(0);
                                                return true;
                                            }
                                            return false;
                                        }
                                ),
                        ScriptSettings.getSettingsData().muleOffItems ? new ItemMuleOff().setSimpleName("Item Mule off") : new MuleOff().setSimpleName("Mule off"),
                        new Anglers(() -> true).setSimpleName("Anglers")
                );
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Web", "Slash"));
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

        Item stamina = ItemVariants.STAMINA_POTION.getItem();
        if (Combat.isInWild() && stamina != null && !Walking.isStaminaActive() && Walking.getRunEnergy() < 20) {
            Inventory.interact(stamina, "Drink");
            return ReactionGenerator.getQuick();
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getNormal();
        }
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
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "cCAnglerFarm " + runtime.formatTime(),
                "Net made: " + df.format(netMade),
                "Fishing level: " + Skills.getRealLevel(Skill.FISHING),
                "Time until mule off " + (ScriptSettings.getSettingsData().muleOffItems ? formatTime(ItemMuleOff.muleTimer.remaining())
                        : formatTime(MuleOff.timer.remaining()))
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
        if (!Anglers.ANGLER_AREA.contains(Players.getLocal())) ;
        if (item.getID() != ItemID.ANGLERFISH) ;
        netMade += LivePrices.get(ItemID.ANGLERFISH) - LivePrices.get(ItemID.SANDWORMS);
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Combat.isInWild()) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity >= 0) return;

        netMade += incoming.getLivePrice() * quantity;
    }
}
