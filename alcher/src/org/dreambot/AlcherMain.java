package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.Alching;
import org.dreambot.behaviour.MixedCombat;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.impcatcher.ImpCatcher;
import org.dreambot.behaviour.magic.EnchantDueling;
import org.dreambot.behaviour.magic.EnchantRecoils;
import org.dreambot.behaviour.magic.F2PMultipleSpots;
import org.dreambot.behaviour.quests.*;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.runemysteries.RuneMysteries;
import org.dreambot.behaviour.training.TotalLevel;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.BankAllEquipmentEvent;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.BondSettings;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.script.AlchItem;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCAlcherFarm", author = "camalCase", version = 0)
public class AlcherMain extends AbstractScript implements PaintInfo, HumanMouseListener, ItemContainerListener {
    static Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();

    boolean shouldHop = false;

    @Override
    public void onStart() {
        init();
    }

    List<String> validColors = Arrays.asList("green", "blue", "black", "red");

    @Override
    public void onStart(String... params) {
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
        Walking.setRunThreshold(20);
        tree.setSimpleName("cCAlcher")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new Fractal(() -> !ScriptSettings.getSettingsData().noMember)
                                .setSimpleName("Members mode")
                                .addChildren(
                                        new LampHandler().setSimpleName("lamp handler"),
                                        new GetMembership().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                                        new ImpCatcher().setSimpleName("Impcatcher"),
                                        new EnchantRecoils().setSimpleName("Enchant Recoils until 27"),
                                        new EnchantDueling().setSimpleName("Enchant Duelings until 55"),
                                        new MuleOff().setSimpleName("MuleOff"),
                                        new Alching(() -> true).setSimpleName("Alching")
                                ),
                        new F2PMultipleSpots(() -> Skills.getRealLevel(Skill.MAGIC) < 55 && ScriptSettings.getSettingsData().noMember)
                                .setSimpleName("F2P training"),
                        new Fractal(() -> ScriptSettings.getSettingsData().tradeUnrestrict && !isUnrestrictedWithoutPlaytime()).addChildren(
                                new MixedCombat(() -> Skills.getRealLevel(Skill.STRENGTH) < 15
                                        || Skills.getRealLevel(Skill.HITPOINTS) < 25)
                                        .setSimpleName("Melee training for quests"),
                                new ShuffleFractal(() -> Quests.getQuestPoints() < 10).setSimpleName("Questin")
                                        .addChildren(
                                                new CooksAssistant().setSimpleName("Cooks assistant"), // 1
                                                new RomeoAndJulietBranch().setSimpleName("Romeo and juliet"), // 5
                                                new org.dreambot.behaviour.quests.impcatcher.ImpCatcher().setSimpleName("Imp catcher"), // 1
                                                new DoricsQuest().setSimpleName("Dorics quest"), // 1
                                                new RuneMysteries().setSimpleName("Rune mysteries"), // 1
                                                new GoblinDiplomacy().setSimpleName("Goblin diplomacy"), // 5
                                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),// 4
                                                new VampyreSlayer().setSimpleName("Vampyre Slayer"), // 3
                                                new SheepShearer().setSimpleName("Sheep shearer"), // 1
                                                new RestlessGhost().setSimpleName("Restless Ghost") // 1
                                        ),
                                new TotalLevel(() -> Skills.getTotalLevel() < 100).setSimpleName("Get 100 total")
                        ),
                        new MuleOff().setSimpleName("MuleOff"),
                        new Alching(() -> true).setSimpleName("Alching")
                );
//        new AIAntiban();

        Arrays.stream(ScriptSettings.getSettingsData().alchItems).forEach(x -> {
            Item alchItem = new Item(x.itemID, 0);
            if (alchItem.getHighAlchValue() <= (x.buyPrice + LivePrices.get(ItemID.NATURE_RUNE))) {
                Logger.warn(String.format("Alchitem %s ID: %d Buyprice: %d is not profitable ",
                        alchItem.getName(), x.itemID, x.buyPrice, x.itemID, x.buyPrice));
            }
        });
    }


    boolean hasLookedForAlchitems = false;
    Timer alchItemsCheckTimer = new Timer(60 * 60 * 1000);
    boolean bankedAll = false;

    @Override
    public int onLoop() {
        if (ScriptSettings.getSettingsData().stopAt55) {
            if (Skills.getRealLevel(Skill.MAGIC) >= 55) return -1;
        }

        if (!bankedAll && ScriptSettings.getSettingsData().bankAllOnStart) {
            new BankAllInventoryEvent().execute();
            new BankAllEquipmentEvent().execute();
            bankedAll = true;
        }

        if ((!hasLookedForAlchitems || alchItemsCheckTimer.finished()) && ScriptSettings.getSettingsData().autoAlch && Client.isLoggedIn()) {
            Logger.info("SEARCHING FOR ALCH ITEMS");
            // run through all the items in the game and replace alch list with whatever the math allows for
            ArrayList<AlchItem> autoAlchItems = new ArrayList<>();
            int natPrice = LivePrices.get(ItemID.NATURE_RUNE);
            for (int i = 0; i < 20_000; i++) {
                Item item = new Item(i, 0);
                // lole!
                if (item.isNoted()) continue;
                if (!item.isTradable()) continue;
                if (ScriptSettings.getSettingsData().noMember == item.isMembersOnly()) continue;
                int price = item.getLivePrice();
                if (price > ScriptSettings.getSettingsData().maxAlchablePrice) continue;
                int profit = item.getHighAlchValue() - price - natPrice;
                if (profit > 0) Logger.info("Profit " + profit + " " + item.getName());
                if (profit < ScriptSettings.getSettingsData().minProfit)
                    continue;

                autoAlchItems.add(new AlchItem(i,
                        price,
                        Math.min(70, ScriptSettings.getSettingsData().moneyLeftAfterMuling / item.getLivePrice()) // if its expensive buy less
                ));
            }
            Logger.info("Found alch items " + autoAlchItems.size());
            alchItemsCheckTimer.reset();
            hasLookedForAlchitems = true;
            ScriptSettings.getSettingsData().alchItems = autoAlchItems.toArray(autoAlchItems.toArray(new AlchItem[0]));
        }

        if (MyVarps.getTutVarp() >= 1000) {
            if (!ChangeAlchWarning.setHighAlchWarning(ScriptSettings.getSettingsData().alchWarningPrice)) {
                Logger.info("Setting alch warning price");
                return ReactionGenerator.getNormal();
            }

            if (ClientSettings.isAcceptAidEnabled()) {
                Logger.info("Setting accept aid");
                ClientSettings.toggleAcceptAid(false);
                return ReactionGenerator.getNormal();
            }
            if (ClientSettings.isSellPriceWarningEnabled()) {
                Logger.info("Setting price warning");
                ClientSettings.toggleSellPriceWarning(false);
                return ReactionGenerator.getNormal();
            }

            if (ClientSettings.isBuyPriceWarningEnabled()) {
                Logger.info("Setting price warning");
                ClientSettings.toggleBuyPriceWarning(false);
                return ReactionGenerator.getNormal();
            }
        }


        if (Client.getGameStateID() == 45) return 1000;
        if (!Client.isLoggedIn()) return 1000;
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

    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public String[] getPaintInfo() {
        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        SettingsData settings = ScriptSettings.getSettingsData();
        return new String[]{
                "cCAlcherFarm " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy),
                "Event " + AbstractResponseEvent.lastRan,
                "GP " + df.format(OwnedItems.count(ItemID.COINS_995) - settings.moneyLeftAfterMuling) + "/"
                        + df.format(ScriptSettings.getSettingsData().profitThreshold),
                "Mule timer: " + muleOff
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

    final static int PLAY_TIME_VARCINT = 526;

    public static boolean isUnrestrictedWithoutPlaytime() {
        return Skills.getTotalLevel() >= 100 && Quests.getQuestPoints() >= 10;
    }
}
