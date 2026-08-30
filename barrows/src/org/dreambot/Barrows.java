package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.general.ItemContainer;
import org.dreambot.api.methods.container.general.ItemContainers;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.map.Region;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.*;
import org.dreambot.behaviour.training.MixedCombat;
import org.dreambot.behaviour.training.SandCrabs;
import org.dreambot.behaviour.training.crafting.Crafting;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.Alching;
import org.dreambot.behaviour.training.magic.EnchantDueling;
import org.dreambot.behaviour.training.magic.EnchantRecoils;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.quests.RestlessGhost;
import org.dreambot.behaviour.training.quests.XMarksTheSpot;
import org.dreambot.behaviour.training.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.training.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.training.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.training.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.training.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebHookUtil;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.discordwebhook.pojo.WebHookPojo;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCBarrows", author = "camalCase", version = 0.0)
public class Barrows extends AbstractScript implements PaintInfo, HumanMouseListener, ChatListener, ItemContainerListener {
    static Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_RIGHT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();
    public static int deathCount = 0;
    final DecimalFormat df = new DecimalFormat("###,###,###");
    int grossMade = 0;

    public static int getHourlyDeathCount() {
        return runtime.getHourlyRate(deathCount);
    }

    boolean shouldHop = false;
    public static final Area MID_DRAGON_AREA = new Area(3197, 3839, 3214, 3830);

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
    public static int kc = 0;

    private void init() {
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        ScriptSettings.setSettingsData(settings);
        new WebhookListener();
        
        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));
        // ernest the chicken webnode
        WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));

        tree.setSimpleName("cCBarrows")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),

                        new Fractal(() -> ScriptSettings.getSettingsData().trainAll && needsToTrain())
                                .setSimpleName("Training")
                                .addChildren(
                                        new MixedCombat(() -> Skills.getRealLevel(Skill.STRENGTH) < 30
                                                || Skills.getRealLevel(Skill.HITPOINTS) < 10)
                                                .setSimpleName("Melee training for quests"),

                                        new XMarksTheSpot().setSimpleName("x marks"),

                                        SandCrabs.getRange(() -> Skills.getRealLevel(Skill.HITPOINTS) < 50
                                                        || Skills.getRealLevel(Skill.RANGED) < 50
                                                        || Skills.getRealLevel(Skill.DEFENCE) < 40)
                                                .setDefenceTarget(Math.max(ScriptSettings.getSettingsData().defenceTarget, 40))
                                                .setSimpleName("Range Sandcrabs until 50hp 50range"),
                                        new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
                                        new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                        new RestlessGhost().setSimpleName("Restless ghost"),
                                        new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                        new PriestInPeril().setSimpleName("Priest in peril"),
                                        new Crafting(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                        new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                        new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < settings.getPrayerTarget()).setSimpleName("Prayer"),
                                        new AnimalMagnetismBranch().setSimpleName("Animal Magnetism"),
                                        new ImpCatcher().setSimpleName("Impcatcher")
                                                .setAppendLogic(() -> {
                                                    if (SpecialWalker.INSIDE_AVAS_ROOM.contains(Players.getLocal())) {
                                                        Magic.castSpell(Normal.HOME_TELEPORT);
                                                        Sleep.sleepUntil(() -> !SpecialWalker.INSIDE_AVAS_ROOM.contains(Players.getLocal()), 30_000);
                                                        return true;
                                                    }
                                                    return false;
                                                }),
                                        new EnchantRecoils().setSimpleName("Enchant Recoils until 27"),
                                        new EnchantDueling().setSimpleName("Enchant Duelings until 55"),
                                        new Alching(() -> true).setSimpleName("Alching")
                                ),

                        new EmptyDeathsCoffer().setSimpleName("Emptying deaths coffer"),
                        new RechargeTrident().setSimpleName("Charge trident"),
                        new MuleOff().setSimpleName("Mule Off"),
                        new GetLoadout(() -> BankLocation.GRAND_EXCHANGE.getArea(100).contains(Players.getLocal())
                                || BankLocation.FEROX_ENCLAVE.getArea(50).contains(Players.getLocal()))
                                .setSimpleName("Get loadout"),
                        new KillBrothers().setSimpleName("Kill brothers"),
                        new HandleCrypt().setSimpleName("Handle crypt")
                );
//        new AIAntiban();
    }


    public static boolean hasLootInBag;
    private long loopSpd;
    private long lastTimestamp;
    final Area LUMBRIDGE = new Area(3217, 3229, 3252, 3191);
    boolean wasAtBarrows = false;
    final Area BARROWS = new Area(3551, 3313, 3582, 3272);

    @Override
    public int onLoop() {
        if (BARROWS.contains(Players.getLocal())) wasAtBarrows = true;

        if (wasAtBarrows && LUMBRIDGE.contains(Players.getLocal())) {
            Logger.info("In Lummy assuming a death");
            KillBrothers.resetBarrowsState();
            KillBrothers.hasDug = false;
            wasAtBarrows = false;
        }

        loopSpd = System.currentTimeMillis() - lastTimestamp;
        lastTimestamp = System.currentTimeMillis();
        if (ClientSettings.isAcceptAidEnabled()) {
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }

//        if (BankLocation.GRAND_EXCHANGE.getArea(50).contains(Players.getLocal())) {
//            if (Equipment.contains(ItemID.TRIDENT_OF_THE_SEAS) && Bank.contains(ItemID.TRIDENT_OF_THE_SEAS_FULL)) {
//                Logger.info("Selling excess tridents");
//                new SellAllEvent(ItemID.TRIDENT_OF_THE_SEAS_FULL).execute();
//            }
//        }

        if (!Combat.isAutoRetaliateOn()) {
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getNormal();
        }

        if (shouldHop && !Players.getLocal().isInCombat()) {
            if (WorldHopper.hopWorld(
                    Worlds.getRandomWorld(x -> !x.isF2P() && x.isNormal() && x.getMinimumLevel() < Skills.getTotalLevel())
            )) shouldHop = false;
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.VIAL, ItemID.STEEL_ARROW)) {
            Inventory.dropAll(ItemID.VIAL, ItemID.STEEL_ARROW);
        }

        WebFinder.getWebFinder().enableWebNodeType(WebNodeType.TELEPORT_NODE);
//
//        if (ScriptSettings.getSettingsData().useLootingBag && hasLootInBag) {
//            if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null) {
//                new EmptyLootingBagEvent().executed();
//            }
//        }
//

        ItemContainer bReward = ItemContainers.getContainer(141);
        if (bReward != null) {
            Logger.info("BARROWS FINISHED");
            GetLoadout.finished = true;
            KillBrothers.resetBarrowsState();
            StringBuilder sb = new StringBuilder();
            sb.append("```diff\n");
            String str = ScriptManager.getScriptManager().getAccountNickname();
            sb.append(str).append("\n");
            int value = 0;
            for (Item i : bReward.getItems()) {
                int price = LivePrices.get(i);
                int ttlPrice = price * i.getAmount();
                sb.append(String.format("+ %s * %s: %s\n", i.getName(), df.format(i.getAmount()), df.format(ttlPrice)));
                value += ttlPrice;
            }
            sb.append(String.format("Gross this session: %s\n", df.format(grossMade)));
            sb.append("```");
            Logger.info("Barrows loot value " + df.format(value) + " total this session " + grossMade);

            if (ScriptSettings.getSettingsData().lootWebhookURL != null && !ScriptSettings.getSettingsData().lootWebhookURL.isEmpty()) {
                try {
                    Logger.info("sending webhook");
                    WebHookUtil.execute(ScriptSettings.getSettingsData().lootWebhookURL,
                            new WebHookPojo().setContent(sb.toString()));
                } catch (IOException e) {
                    Logger.info(e);
                }
            }
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


    public static List<Tile> lastPath = null;

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
        if (lastPath != null) {
            try {
                for (Tile t : lastPath) {
                    boolean hasDoor = Arrays.stream(GameObjects.getObjectsOnTile(t)).anyMatch(x -> x.hasAction("Open"));
                    if (hasDoor) {
                        graphics.setColor(Color.YELLOW);
                    } else {
                        graphics.setColor(Color.white);
                    }
                    if (t != null) graphics.drawPolygon(t.getPolygon());
                }

            } catch (Exception ignored) {
            }

        }
        scriptPaint.paint(graphics);
        fractalPaint.paint(graphics);
    }


    @Override
    public String[] getPaintInfo() {
        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        return new String[]{
                "cCBarrows " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "isMember: " + Client.isMembers(),
                "Time Until Mule off: " + muleOff,
                "Deaths: " + deathCount,
//                "Location: " + LocationConfig.getName(),
                "LoopSpd: " + loopSpd,
                "region " + Region.getRegion().getId(),
                "Gross Made: " + df.format(grossMade) + " KC: " + kc,
                "Event " + AbstractResponseEvent.lastRan
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

        if (message.getMessage().contains("You don't find anything.")) {
            Arrays.stream(BarrowsBrothers.values())
                    .distinct()
                    .forEach(x -> Logger.info(x.name + " " + x.getBitValue()));
            Arrays.stream(FractalAPI.hierarchy).forEach(Logger::info);
        }
    }

    Timer kcTimer = new Timer(60 * 1000);

    @Override
    public void onInventoryItemAdded(Item item) {
        if (!HandleCrypt.CHEST_AREA.contains(Players.getLocal())) {
            return;
        }

        if (kcTimer.finished()) {
            kcTimer.reset();
            kc++;
        }

        if (Arrays.stream(MuleOff.LOOT).anyMatch(x -> item.getID() == x || item.getID() == ItemID.COINS_995)) {
            Logger.info("Got loot " + item.getName());
            grossMade += item.getLivePrice() * item.getAmount();
        }
    }

//
//    @Override
//    public void onInventoryItemChanged(Item incoming, Item existing) {
//        if (!HandleCrypt.CHEST_AREA.contains(Players.getLocal())) {
//            return;
//        }
//
//        int quantity = incoming.getAmount() - existing.getAmount();
//        if (quantity <= 0) return;
//
//        if (Arrays.stream(MuleOff.LOOT).anyMatch(x -> incoming.getID() == x)) {
//            Logger.info("Got loot " + incoming.getName());
//            grossMade += incoming.getLivePrice();
//        }
//
//    }
//

    private boolean needsToTrain() {
        return !PaidQuest.ANIMAL_MAGNETISM.isFinished()
                || Skills.getRealLevel(Skill.RANGED) < 50
                || Skills.getRealLevel(Skill.MAGIC) < 75
                || Skills.getRealLevel(Skill.PRAYER) < 43
                || Skills.getRealLevel(Skill.DEFENCE) < 40;
    }
}
