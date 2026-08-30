package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
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
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.GetMoreAvas;
import org.dreambot.behaviour.MixedCombat;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.SandCrabs;
import org.dreambot.behaviour.dragons.*;
import org.dreambot.behaviour.quests.*;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.animalmagnetism.util.LeaveAvaRoom;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.druidicritual.DruidicRitual;
import org.dreambot.behaviour.quests.dwarfcannon.DwarfCannon;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.entertheabyss.EnterTheAbyss;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.quests.runemysteries.RuneMysteries;
import org.dreambot.behaviour.quests.theknightssword.TheKnightsSword;
import org.dreambot.behaviour.training.crafting.Crafting;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
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
import org.dreambot.settings.script.DragonMode;
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

@ScriptManifest(category = Category.MONEYMAKING, name = "cCBrutalBlues", author = "camalCase", version = 0.0)
public class BrutalBlues extends AbstractScript implements PaintInfo, HumanMouseListener, ChatListener, ItemContainerListener, SpawnListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();
    public static int deathCount = 0;
    boolean shouldHop = false;
    public static int grossGp = 0;
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

        String scriptName = ScriptManager.getScriptManager().getCurrentScript().getSDNName();
        if (scriptName.toLowerCase().contains("blue")) ScriptSettings.getSettingsData().dragonMode = DragonMode.BLUE;
        if (scriptName.toLowerCase().contains("black")) ScriptSettings.getSettingsData().dragonMode = DragonMode.BLACK;
        if (scriptName.toLowerCase().contains("red")) ScriptSettings.getSettingsData().dragonMode = DragonMode.RED;

        // for testing
        Logger.info(ScriptManager.getScriptManager().getCurrentScript().getScriptId());
        if (ScriptManager.getScriptManager().getCurrentScript().getScriptId() < 0) {
            Logger.info("Set black");
            ScriptSettings.getSettingsData().dragonMode = DragonMode.BLACK;
        }

        tree.setSimpleName("cCBrutalDragons")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Getting membership"),


                        new Fractal(() -> !PaidQuest.ANIMAL_MAGNETISM.isFinished())
                                .setSimpleName("Get Avas")
                                .addChildren(
                                        new MixedCombat(() -> Skills.getRealLevel(Skill.STRENGTH) + Skills.getRealLevel(Skill.ATTACK) < 30
                                                || Skills.getRealLevel(Skill.HITPOINTS) < 10)
                                                .setSimpleName("Melee training"),

                                        new XMarksTheSpot().setSimpleName("X marks the spot"),
                                        new ClientOfKourend().setSimpleName("Client of Kourend"),
                                        SandCrabs.getRange(() -> Skills.getRealLevel(Skill.HITPOINTS) < 30
                                                        || Skills.getRealLevel(Skill.RANGED) < 30)
                                                .setDefenceTarget(ScriptSettings.getSettingsData().defenceTarget)
                                                .setSimpleName("Range Sandcrabs"),
                                        new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS),
                                        new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                        new RestlessGhost().setSimpleName("Restless ghost"),
                                        new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                        new PriestInPeril().setSimpleName("Priest in peril"),
                                        new Crafting(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                        new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                        new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                                ),

                        new Fractal(() -> FreeQuest.DRAGON_SLAYER.getConfigValue() < 1).setSimpleName("Unlock dragon shield")
                                .addChildren(
                                        new LeaveAvaRoom().setSimpleName("Leave ava"),
                                        new MixedCombat(() -> Combat.getCombatLevel() < ScriptSettings.getSettingsData().combatTarget
                                                || Skills.getRealLevel(Skill.HITPOINTS) < ScriptSettings.getSettingsData().hitpointsTarget)
                                                .setSimpleName("Melee training"),
                                        new Fractal(() -> Quests.getQuestPoints() < 32).setSimpleName("Questin")
                                                .addChildren(
                                                        new XMarksTheSpot().setSimpleName("X marks the spot"),
                                                        new ClientOfKourend().setSimpleName("Client of kourend"),
                                                        new CooksAssistant().setSimpleName("Cooks assistant"), // 1
                                                        new RomeoAndJulietBranch().setSimpleName("Romeo and juliet"), // 5
                                                        new ImpCatcher().setSimpleName("Imp catcher"), // 1
                                                        new DoricsQuest().setSimpleName("Dorics quest"), // 1
                                                        new TheKnightsSword().setSimpleName("Knights sword"), // 1
                                                        new RuneMysteries().setSimpleName("Rune mysteries"), // 1
                                                        new DwarfCannon().setSimpleName("Dwarf cannon"), // 1
                                                        new EnterTheAbyss().setSimpleName("Enter the abyss"),// 0
                                                        new GoblinDiplomacy().setSimpleName("Goblin diplomacy"), // 5
                                                        new DruidicRitual().setSimpleName("Druidic Ritual"), // 4
                                                        new ErnestTheChicken().setSimpleName("Ernest the chicken"),// 4
                                                        new VampyreSlayer().setSimpleName("Vampyre Slayer"), // 3
                                                        new SheepShearer().setSimpleName("Sheep shearer"), // 1
                                                        new MonksFriend().setSimpleName("Monks Friend"), // 1
                                                        new RestlessGhost().setSimpleName("Restless Ghost"), // 1
                                                        new PriestInPeril().setSimpleName("PIP") // 1
                                                ),
                                        new DragonSlayerOne().setSimpleName("DS1 until shield unlocked")
                                ),

                        SandCrabs.getRange(() -> Skills.getRealLevel(Skill.HITPOINTS) < 30
                                        || Skills.getRealLevel(Skill.RANGED) < ScriptSettings.getRangeTarget())
                                .setDefenceTarget(ScriptSettings.getSettingsData().defenceTarget)
                                .setSimpleName("Range Sandcrabs"),

                        new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < ScriptSettings.getPrayerTarget())
                                .setSimpleName("Prayer"),

                        new SlayerBranch(() -> ScriptSettings.getSettingsData().dragonMode == DragonMode.BLACK
                                && Skills.getRealLevel(Skill.SLAYER) < 77).setSimpleName("Slayer"),

                        new MuleOff().setSimpleName("Mule Off"),
                        new GetMoreAvas().setSimpleName("Get more avas"),
                        new LeaveAvaRoom().setSimpleName("Leave ava"),
                        new Restock().setSimpleName("Restock"),
                        new RechargePrayer().setSimpleName("Recharge prayer"),
                        new KillBrutalBlues(ScriptSettings::isBlue).setSimpleName("Kill brutal blues"),
                        new KillBrutalBlacks(ScriptSettings::isBlack).setSimpleName("Blacks"),
                        new KillBrutalReds(ScriptSettings::isRed).setSimpleName("Kill brutal reds")
                );
//        new AIAntiban();

        // ernest the chicken webnode
        WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));
    }


    public static boolean hasLootInBag;
    private long loopSpd;
    private long lastTimestamp;
    final Area HELLHOUNDS = new Area(1637, 10073, 1653, 10056);

    @Override
    public int onLoop() {
        loopSpd = System.currentTimeMillis() - lastTimestamp;
        lastTimestamp = System.currentTimeMillis();
        if (ClientSettings.isAcceptAidEnabled()) {
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }

        if (HELLHOUNDS.contains(Players.getLocal())) {
            if (!Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
                Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
            }
        }

        Player attackingMe = Players.closest(x -> x.isSkulled() && x.isInteracting(Players.getLocal()));
        if (Combat.isInWild() && Players.getLocal().isInCombat() && attackingMe != null) {
            Logger.info("Being attack by " + attackingMe.getName());
            Logger.info("Level: " + attackingMe.getLevel());
            attackingMe.getEquipment().forEach(x -> Logger.info("Equipment " + x.getID() + " " + x.getName()));
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            Logger.info("Disabling hop confirmations");
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getQuick();
        }

        if (!HELLHOUNDS.contains(Players.getLocal()) && Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
            Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
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

        if (Inventory.contains(ItemID.LOOTING_BAG_CLOSED)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Opening looting bag");
            Inventory.interact(ItemID.LOOTING_BAG_CLOSED, "Open");
        }

        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 30) {
            Walking.toggleRun();
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
    }


    @Override
    public String[] getPaintInfo() {
        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        Player local = Players.getLocal();
        String target = "";
        Character tgt = null;
        if (local != null) {
            tgt = local.getInteractingCharacter();
            if (tgt != null) target = tgt.getName();
        }

        return new String[]{
                "cCBrutalDragons " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Time Until Mule off: " + muleOff,
                "Deaths: " + deathCount,
                "LoopSpd: " + loopSpd,
                String.format("Earned %s (%s / hr))", df.format(grossGp), df.format(runtime.getHourlyRate(grossGp))),
                "target: " + target + " " + (tgt == null ? "" : tgt.getAnimation()),
//                "Timer: " + KillBrutalBlues.lastDragonAtk.remaining(),
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

    List<Integer> ignoreIDs = Arrays.asList(ItemID.RUNE_ARROW, ItemID.MITHRIL_ARROW);

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
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
        if (quantity >= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
    }
}
