package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.quest.Quests;
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
import org.dreambot.behaviour.*;
import org.dreambot.behaviour.gdk.GenericKite;
import org.dreambot.behaviour.gdk.Restock;
import org.dreambot.behaviour.gdk.melee.MeleeDragons;
import org.dreambot.behaviour.gdk.melee.MeleeRestock;
import org.dreambot.behaviour.quests.*;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
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
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.*;
import org.dreambot.fractals.data.ItemID;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCGreenDragonFarm", author = "camalCase", version = 0.0)
public class GreenDragonFarm extends AbstractScript implements PaintInfo, HumanMouseListener, ChatListener, ItemContainerListener, SpawnListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();
    public static int deathCount = 0;
    boolean shouldHop = false;
    public static final Area MID_DRAGON_AREA = new Area(3197, 3839, 3214, 3830);
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
        Fractal[] dragonLocs = new Fractal[]{
                // top right dragon safe spot 2993, 3620 realid 260
                // top left dragon 2965 3622 realid 263
                // bottom dragon realid 264
                new GenericKite(new Tile(2993, 3620), () -> NPCs.closest(x -> x.getRealID() == 260 && (!x.isInCombat() || x.isInteracting(Players.getLocal())) || Players.getLocal().isInteracting(x)))
                        .setSimpleName("Top right"),
                new GenericKite(new Tile(2965, 3622), () -> NPCs.closest(x -> x.getRealID() == 260 && (!x.isInCombat() || x.isInteracting(Players.getLocal())) || Players.getLocal().isInteracting(x)))
                        .setSimpleName("Top right"),
                new GenericKite(new Tile(2970, 3604), () -> NPCs.closest(x -> x.getRealID() == 264 && (!x.isInCombat() || x.isInteracting(Players.getLocal())) || Players.getLocal().isInteracting(x)))
                        .setSimpleName("Bottom dragon"),
//                new KiteDragon().setSimpleName("Bottom Dragon")
        };

        tree.setSimpleName("cCGreenDragons")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Getting membership"),
                        // todo melee training conditions

                        new MuleOff().setSimpleName("Mule Off"),
                        new AntiPkNode().setSimpleName("Anti PK Mode"),
                        new Fractal(() -> ScriptSettings.getSettingsData().meleeMode).setSimpleName("Melee Mode")
                                .addChildren(
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
                                                        new PriestInPeril().setSimpleName("PIP"), // 1
                                                        new ChildrenOfTheSun().setSimpleName("COS")
                                                ),
                                        new DragonSlayerOne().setSimpleName("DS1 until shield unlocked"),

                                        new MeleeRestock(() -> !Inventory.contains(ScriptSettings.getSettingsData().foodId)
                                                || !Inventory.contains(x -> x.getName().toLowerCase().contains("anti")))
                                                .setSimpleName("Restocking"),
                                        new MeleeDragons(() -> true).setSimpleName("Kill Dragons")
                                ),
                        new Fractal(() -> !ScriptSettings.getSettingsData().meleeMode)
                                .setSimpleName("Kite Mode")
                                .addChildren(

                                        new XMarksTheSpot().setSimpleName("X marks the spot"),
                                        new ClientOfKourend().setSimpleName("Client of kourend"),
                                        SandCrabs.getRange(() -> Skills.getRealLevel(Skill.HITPOINTS) < ScriptSettings.getSettingsData().hitpointsTarget
                                                        || Skills.getRealLevel(Skill.RANGED) < ScriptSettings.getSettingsData().rangedTarget)
                                                .setDefenceTarget(ScriptSettings.getSettingsData().defenceTarget)
                                                .setSimpleName("Range Sandcrabs"),
//                                        new DistributedRangeTraining(() -> ScriptSettings.getSettingsData().meleeMode && Skills.getRealLevel(Skill.HITPOINTS) < ScriptSettings.getSettingsData().hitpointsTarget
//                                                || Skills.getRealLevel(Skill.RANGED) < ScriptSettings.getSettingsData().rangedTarget)
//                                                .setDefenceTarget(ScriptSettings.getSettingsData().defenceTarget)
//                                                .setSimpleName("Range Training"),
                                        new Fractal(() -> ScriptSettings.getSettingsData().getAvas && !PaidQuest.ANIMAL_MAGNETISM.isFinished())
                                                .setSimpleName("Get Avas")
                                                .addChildren(
                                                        new MixedCombat(() -> Combat.getCombatLevel() < 20
                                                                || Skills.getRealLevel(Skill.HITPOINTS) < 10)
                                                                .setSimpleName("Melee training"),

                                                        new XMarksTheSpot().setSimpleName("X marks the spot"),
                                                        new ClientOfKourend().setSimpleName("Client of Kourend"),
                                                        SandCrabs.getRange(() -> Skills.getRealLevel(Skill.HITPOINTS) < 30
                                                                        || Skills.getRealLevel(Skill.RANGED) < 30)
                                                                .setDefenceTarget(ScriptSettings.getSettingsData().defenceTarget)
                                                                .setSimpleName("Range Sandcrabs"),
//                                                        new DistributedRangeTraining(() -> Skills.getRealLevel(Skill.HITPOINTS) < 30
//                                                                || Skills.getRealLevel(Skill.RANGED) < 30)
//                                                                .setDefenceTarget(ScriptSettings.getSettingsData().defenceTarget)
//                                                                .setSimpleName("Range Training"),
                                                        new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS),
                                                        new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                                        new RestlessGhost().setSimpleName("Restless ghost"),
                                                        new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                                        new PriestInPeril().setSimpleName("Priest in peril"),
                                                        new Crafting(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                                        new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                                        new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                                                ),
                                        new GetMoreAvas().setSimpleName("Get more avas"),
                                        new Restock(() -> Equipment.isSlotEmpty(EquipmentSlot.ARROWS)
                                                || !Inventory.contains(ItemID.JUG_OF_WINE)).setSimpleName("Restocking"),
                                        new Fractal(() -> ScriptSettings.getSettingsData().allRangeLocs)
                                                .setSimpleName("All locations")
                                                .addChildren(
                                                        dragonLocs[ShuffleFractal.getLoginValue() % dragonLocs.length]
                                                ),
                                        new GenericKite(new Tile(2970, 3604), () -> NPCs.closest(x -> x.getRealID() == 264 && (!x.isInCombat() || x.isInteracting(Players.getLocal())) || Players.getLocal().isInteracting(x)))
                                                .setSimpleName("Bottom dragon")
                                )
                );
//        new AIAntiban();

        // ernest the chicken webnode
        WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));
    }


    public static boolean hasLootInBag;
    private long loopSpd;
    private long lastTimestamp;
    boolean hasLoadedTrie = false;
    Timer trieRefresh = new Timer(60 * 1000 * 45);
    Timer playerLogTimer = new Timer(60 * 1000);

    @Override
    public int onLoop() {
        if (!ScriptSettings.getSettingsData().disablePkList && trieRefresh.finished() || !hasLoadedTrie) {
            PKTrie.refreshPkerList();
            trieRefresh.reset();
            hasLoadedTrie = true;
        }

        loopSpd = System.currentTimeMillis() - lastTimestamp;
        lastTimestamp = System.currentTimeMillis();
        if (ClientSettings.isAcceptAidEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }


        if (!Combat.isAutoRetaliateOn()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getNormal();
        }

//        Player attackingMe = Players.closest(x -> x.isSkulled() && x.isInteracting(Players.getLocal()));
//        if (Combat.isInWild() && Players.getLocal().isInCombat() && attackingMe != null) {
//            Logger.info("Being attack by " + attackingMe.getName());
//            Logger.info("Level: " + attackingMe.getLevel());
//            attackingMe.getEquipment().forEach(x -> Logger.info("Equipment " + x.getID() + " " + x.getName()));
//        }


        Player attckingMe = Players.closest(x -> x.isSkulled() && x.isInteracting(Players.getLocal()));
        if (Combat.isInWild() && Players.getLocal().isInCombat() && attckingMe != null && playerLogTimer.finished()) {
            Logger.info("Being attack by " + attckingMe.getName());
            Logger.info("Level: " + attckingMe.getLevel());
            Logger.info("My Level: " + Combat.getCombatLevel());
            Logger.info("Wilderness level: " + Combat.getWildernessLevel());
            Logger.info("Predicted: " + AntiPkNode.canAttackMe(attckingMe));
            attckingMe.getEquipment().forEach(x -> Logger.info("Equipment " + x.getID() + " " + x.getName()));
            playerLogTimer.reset();
            PKTrie.reportPker(attckingMe.getName());
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            Logger.info("Disabling hop confirmations");
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getQuick();
        }

        if (!Combat.isInWild() && Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
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
//
        if (ScriptSettings.getSettingsData().useLootingBag && hasLootInBag) {
            if (BankLocation.EDGEVILLE.distance(Players.getLocal().getTile()) < 16 && ItemVariants.LOOTING_BAG.getItem() != null) {
                Logger.info("Emptying looting bag");
                if (!Bank.isOpen()) {
                    if (Walking.shouldWalk()) BankUtil.openClosest();
                    return ReactionGenerator.getQuick();
                }

                new EmptyLootingBagEvent().executed();
                hasLootInBag = false;
                return ReactionGenerator.getQuick();
            }
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

//        List<NPC> dragons = NPCs.all(x -> x.getName().equals("Green dragon"));
//        for (NPC drag : dragons) {
//            Point p = drag.getClickablePoint();
//            graphics.drawString(String.valueOf(drag.getRealID()),
//                    p.x,
//                    p.y
//            );
//        }

//        Character interacting = Players.getLocal().getInteractingCharacter();
//        if (interacting != null) {
//            graphics.drawString(String.valueOf(interacting.getAnimation()),
//                    200,
//                    200
//            );
//        }
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
                "cCGreenDragons " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Time Until Mule off: " + muleOff,
                "Deaths: " + deathCount,
                "LoopSpd: " + loopSpd,
                String.format("Earned %s (%s / hr))", df.format(grossGp), df.format(runtime.getHourlyRate(grossGp))),
                "target: " + target,
//                "Timer: " + MeleeDragons.lastDragonAtk.remaining(),
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

//    @Override
//    public void onInventoryItemAdded(Item item) {
//        if (Combat.isInWild()) {
//            Logger.info("Picked up some " + item.getName() + "*" + item.getAmount());
//            grossGp += item.getLivePrice() * item.getAmount();
//        }
//    }
//
//    @Override
//    public void onLootBagItemAdded(Item item) {
//        if (Combat.isInWild()) {
//            Logger.info("Picked up some " + item.getName() + "*" + item.getAmount());
//            grossGp += item.getLivePrice() * item.getAmount();
//        }
//    }

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
