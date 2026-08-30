package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.EmptyDeathsCoffer;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.TurnInLootKeys;
import org.dreambot.behaviour.dragons.*;
import org.dreambot.behaviour.training.impcatcher.ImpCatcher;
import org.dreambot.behaviour.training.magic.EnchantRecoils;
import org.dreambot.behaviour.training.magic.F2PMultipleSpots;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.NpcID;
import org.dreambot.fractals.events.AbstractEvent;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.pktrie.PKTrie;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCLavaDragonFarm", author = "camalCase", version = 0.02)
public class LavaDragonFarm extends AbstractScript implements PaintInfo, HumanMouseListener, ChatListener, ItemContainerListener {
    static Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();
    public static int deathCount = 0;

    DecimalFormat df = new DecimalFormat("###,###,###");
    int grossMade = 0;

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
    boolean hasSetLocation;

    private void init() {
        Client.getInstance().getRandomManager().disableSolver(RandomEvent.LOGIN);
        Client.getInstance().getRandomManager().registerSolver(new CustomLoginHandler("CUSTOM_LOGIN"));
        Client.getInstance().getRandomManager().enableSolver("CUSTOM_LOGIN");
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
        tree.setSimpleName("cCLavaDragons")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),
                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),

                        // range mode
//                        new Fractal().setSimpleName("Range mode")
//                                .addChildren(
//                                        SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < 61).setSimpleName("Range train"),
//                                        new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
//                                        new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
//                                       new RestlessGhost().setSimpleName("Restless ghost"),
//                                        new ErnestTheChicken().setSimpleName("Ernest the chicken"),
//                                        new PriestInPeril().setSimpleName("Priest in peril"),
//                                        new Crafting(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
//                                        new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop")
//                                ),
                        new ImpCatcher().setSimpleName("Imp Catcher"),
                        new EnchantRecoils().setSimpleName("Enchant Recoils until 35"),
                        new F2PMultipleSpots(() -> Skills.getRealLevel(Skill.HITPOINTS) < ScriptSettings.getSettingsData().hitpointsTarget)
                                .setDefenceTarget(ScriptSettings.getSettingsData().defenceTarget),
//                        new MagicCombat(
//                                ScriptSettings.getSettingsData().hitpointsTarget,
//                                ScriptSettings.getSettingsData().defenceTarget
//                        ).setSimpleName("Magic combat training"),
                        new AntiPk().setSimpleName("AntiPk"),
                        new EmptyDeathsCoffer().setSimpleName("Emptying deaths coffer"),
                        new DrinkWine(() -> Skills.getBoostedLevel(Skill.HITPOINTS) <= ScriptSettings.getSettingsData().eatAbove
                                && (Walking.getDestination() != null || !Players.getLocal().isHealthBarVisible())
                                && Inventory.contains(ItemID.JUG_OF_WINE)
                                && Combat.getHealthPercent() < 100).setSimpleName("Drink Wine"),
                        new ExitDragon().setSimpleName("Exit Dragons"),
                        new TurnInLootKeys().setSimpleName("Turn in loot keys"),
                        new RechargeTrident().setSimpleName("Recharge trident"),
                        new RechargeSceptre().setSimpleName("Recharge sceptre"),
                        new MuleOff().setSimpleName("Mule Off"),
                        new PathToDragons(() -> !Combat.isInWild() || PathToDragons.WILDERNESS_PLATO.contains(Players.getLocal()))
                                .setSimpleName("Goto Dragons"),
                        new Dragons(() -> true)
                                .setSimpleName("Fight Dragons")
                );
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Web", "Slash"));
//        new AIAntiban();
    }


    public static boolean hasLootInBag;
    private long loopSpd;
    private long lastTimestamp;
    Timer playerLogTimer = new Timer(60 * 1000);
    boolean hasLoadedTrie = false;
    Timer trieRefresh = new Timer(60 * 1000 * 45);

    @Override
    public int onLoop() {
        if (Client.isLoggedIn() && !Worlds.getCurrent().isNormal()) {
            Logger.info("Logging out for abnormal world");
            Client.setIdleTime(30_000);
            return ReactionGenerator.getNormal();
        }

        if (ScriptSettings.getSettingsData().stopAt55) {
            if (Skills.getRealLevel(Skill.MAGIC) >= 55) return -1;
        }

        if (trieRefresh.finished() || !hasLoadedTrie) {
            Logger.info("Refresh pk list");
            PKTrie.refreshPkerList();
            trieRefresh.reset();
            hasLoadedTrie = true;
        }

        Player attckingMe = Players.closest(x -> x.isSkulled() && x.isInteracting(Players.getLocal()));
        if (Combat.isInWild() && Players.getLocal().isInCombat() && attckingMe != null && playerLogTimer.finished()) {
            Logger.info("Being attack by " + attckingMe.getName());
            Logger.info("Level: " + attckingMe.getLevel());
            Logger.info("My Level: " + Combat.getCombatLevel());
            Logger.info("Wilderness level: " + Combat.getWildernessLevel());
            Logger.info("Predicted: " + AntiPk.canAttackMe(attckingMe));
            attckingMe.getEquipment().forEach(x -> Logger.info("Equipment " + x.getID() + " " + x.getName()));
            playerLogTimer.reset();
            PKTrie.reportPker(attckingMe.getName());
        }

        loopSpd = System.currentTimeMillis() - lastTimestamp;
        lastTimestamp = System.currentTimeMillis();
        if (ClientSettings.isAcceptAidEnabled()) {
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }

        Item stamina = ItemVariants.STAMINA_POTION.getItem();
        if (Combat.isInWild() && stamina != null && !Walking.isStaminaActive() && Walking.getRunEnergy() < 20) {
            Logger.info("Drinking stamina");
            Inventory.interact(stamina, "Drink");
            return ReactionGenerator.getQuick();
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            Logger.info("Editing settings - world hop confirm");
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getNormal();
        }

        if (shouldHop && !Players.getLocal().isInCombat()) {
            Logger.info("Hopping worlds after being attacked");
            if (WorldHopper.hopWorld(
                    Worlds.getRandomWorld(x -> !x.isF2P() && x.isNormal() && x.getMinimumLevel() < Skills.getTotalLevel())
            )) shouldHop = false;
            return ReactionGenerator.getNormal();
        }

        if (ScriptSettings.getSettingsData().useLootingBag && hasLootInBag) {
            if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null) {
                Logger.info("Emptying looting bags");
                new EmptyLootingBagEvent().executed();
            }
        }

        if (!hasSetLocation && Skills.getRealLevel(Skill.HITPOINTS) > 30) {
            ArrayList<DragonLocation> safespots = new ArrayList<>(Arrays.asList(
                    new DragonLocation(new Tile(3188, 3843, 0), // middle west
                            () -> NPCs.closest(x -> x.getID() == NpcID.LAVA_DRAGON && x.distance() < 12),
                            "Middle West-side"),

                    new DragonLocation(new Tile(3184, 3813, 0), // bottom west
                            () -> NPCs.closest(x -> x.getID() == NpcID.LAVA_DRAGON && x.distance() < 8),
                            "Bottom West-side"),

                    new DragonLocation(new Tile(3215, 3835, 0),// middle east
                            () -> NPCs.closest(x -> x.getID() == NpcID.LAVA_DRAGON && MID_DRAGON_AREA.contains(x)),
                            "Middle East-side")
            ));

//            if (ScriptSettings.getSettingsData().useNewSpots) {
//                safespots.add(
//                        new DragonLocation(new Tile(3207, 3811, 0), // bottom west
//                                () -> NPCs.closest(x -> x.getID() == NpcID.LAVA_DRAGON && x.distance() < 10 && x.getX() < Players.getLocal().getX()),
//                                "bottom new"));
//
//                safespots.add(
//                        new DragonLocation(new Tile(3209, 3809, 0), // bottom west
//                                () -> NPCs.closest(x -> x.getID() == NpcID.LAVA_DRAGON && x.distance() < 8 && x.getX() > Players.getLocal().getX()),
//                                "mid new"));
//
//                safespots.add(
//                        new DragonLocation(new Tile(3218, 3822, 0), // bottom west
//                                () -> NPCs.closest(x -> x.getID() == NpcID.LAVA_DRAGON && x.distance() < 8 && x.getY() > Players.getLocal().getY()),
//                                "Top new"));
//            }

            LocationConfig.setLocation(safespots.get(ShuffleFractal.getLoginValue() % safespots.size()));
            hasSetLocation = true;
        }

        if (ClientSettings.isSellPriceWarningEnabled()) {
            Logger.info("Editing settings - sell price warning");
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleSellPriceWarning(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isBuyPriceWarningEnabled()) {
            Logger.info("Editing settings - buy price warning");
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
        Logger.info("click @ " + e.getPoint());
        if (Gui.wasDiscordButtonClicked(e.getPoint())) {
            try {
                Desktop.getDesktop().browse(new URI(""));
            } catch (IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (Gui.wasButtonClicked(e.getPoint())) {
            Logger.info("Was clicked");
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
        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());

        String interacting = "";
        Character attackingMe = Players.getLocal().getCharacterInteractingWithMe();
        if (attackingMe != null) interacting = attackingMe.getName();
        return new String[]{
                "cCLavaDragons " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "isMember: " + Client.isMembers(),
                "Time Until Mule off: " + muleOff,
                "Earned: " + df.format(grossMade) + " (" + df.format(runtime.getHourlyRate(grossMade)) + ")",
                "Deaths: " + deathCount + " Dodged: " + AntiPk.dodgedCount,
                "Location: " + LocationConfig.getName(),
                "LoopSpd: " + loopSpd,
//                "Using new spots: " + ScriptSettings.getSettingsData().useNewSpots,
                "Event " + AbstractResponseEvent.history.toString(),
                "Interacting w/ me " + interacting,
                "Game state " + Client.getGameState(),
//                Client.getUsername() + ":" + Client.getForumUser(),
//                AnalyticsReporter.hashStringSHA256(Client.getUsername() + ":" + Client.getForumUser())
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
        if (!Combat.isInWild()) return;
        grossMade += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Combat.isInWild()) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) return;

        grossMade += incoming.getLivePrice() * quantity;
    }

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossMade += item.getLivePrice() * item.getAmount();
    }
}
