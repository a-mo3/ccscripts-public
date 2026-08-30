package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.widget.Widgets;
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
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.MoonlightMoths;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.antelopes.AntelopeBranch;
import org.dreambot.behaviour.antelopes.sunfire.SunlightAntelopes;
import org.dreambot.behaviour.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.eaglespeak.EaglesPeak;
import org.dreambot.behaviour.impl.AntiPkNode;
import org.dreambot.behaviour.impl.EdgevilleBankLeaf;
import org.dreambot.behaviour.impl.GetBoxes;
import org.dreambot.behaviour.impl.NewBlacksChins;
import org.dreambot.behaviour.training.HunterBranch;
import org.dreambot.behaviour.training.NewRedChins;
import org.dreambot.behaviour.training.fletch.FletchLogs;
import org.dreambot.behaviour.training.fletch.HeadlessArrows;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.muling.Log;
import org.dreambot.settings.BondSettings;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.script.SettingsData;
import org.dreambot.settings.script.TrainMode;
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
import java.util.stream.Collectors;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCMoonlightAnetelopeFarm", author = "camalCase", version = 0.2)
public class Antelope extends AbstractScript implements PaintInfo, HumanMouseListener, ChatListener, ItemContainerListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();
    int blacksCaught;
    DecimalFormat df = new DecimalFormat("###,###,###");
    int splinterCount = 0;

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

    boolean hasRedChins;
    public static boolean isMoths;

    private int hunterTrainingTarge() {
        String scriptName = ScriptManager.getScriptManager().getCurrentScript().getSDNName().toLowerCase();

        if (scriptName.contains("sunlight")) return 72;
        if (scriptName.contains("moth")) return 75;
        return 91;
    }

    private void init() {
        String scriptName = ScriptManager.getScriptManager().getCurrentScript().getSDNName();
        isMoths = scriptName.toLowerCase().contains("moths");
        boolean isSunlight = scriptName.toLowerCase().contains("sunlight");
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        ScriptSettings.setSettingsData(settings);
        new WebhookListener();


        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));
        hasRedChins = Client.getInstance().getScriptManager().hasSDNScript(1670) || Client.getInstance().getScriptManager().hasSDNScript(1671);
        boolean hasBlackChins = Client.getInstance().getScriptManager().hasSDNScript(1669) || Client.getInstance().getScriptManager().hasSDNScript(1668);
        boolean hasMoths = Client.getInstance().getScriptManager().hasSDNScript(1797) || Client.getInstance().getScriptManager().hasSDNScript(1796);
        Area FALCONRY_AREA = new Area(2363, 3621, 2394, 3572);

        tree.setSimpleName(isMoths ? "cCMoonlightMoths" : "cCAntelopeFarm")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get Membership"),
                        new EmptyDeathsCoffer().setSimpleName("Empty death"),
                        new ChildrenOfTheSun().setSimpleName("Children of the sun"),

                        new Fractal(() -> !isMoths && ScriptSettings.getSettingsData().fletch && Skills.getRealLevel(Skill.FLETCHING) < (isSunlight ? 62 : 72))
                                .addChildren(
                                        new HeadlessArrows(() -> Skills.getRealLevel(Skill.FLETCHING) < 20)
                                                .setSimpleName("Make headless arrows"),
                                        new FletchLogs(ItemID.OAK_LOGS, ItemID.OAK_SHORTBOW_U, 200, 25).setSimpleName("Fletch oak short"),
                                        new FletchLogs(ItemID.OAK_LOGS, ItemID.OAK_LONGBOW_U, 700, 40).setSimpleName("Fletch oak long"),
                                        new FletchLogs(ItemID.WILLOW_LOGS, ItemID.WILLOW_LONGBOW_U, 200, 55).setSimpleName("Fletch Willow long"),
                                        new FletchLogs(ItemID.MAPLE_LOGS, ItemID.MAPLE_LONGBOW_U, 2600, 99).setSimpleName("Fletch maple long")
                                ).setSimpleName("Fletch"),

                        new Fractal(() -> Skills.getRealLevel(Skill.HUNTER) < hunterTrainingTarge() && hasBlackChins
                                && ScriptSettings.getSettingsData().trainMode == TrainMode.BLACK_CHINS)
                                .setSimpleName("Training(Black Chins)")
                                .addChildren(
                                        new EaglesPeak(() -> Skills.getRealLevel(Skill.HUNTER) >= 27 && !PaidQuest.EAGLES_PEAK.isFinished()).setSimpleName("Eagles Peak"),
                                        new Fractal(() -> Skills.getRealLevel(Skill.HUNTER) < 73).setSimpleName("Training").addChildren(
                                                new NewRedChins(() -> Skills.getRealLevel(Skill.HUNTER) >= 63
                                                        && hasRedChins
                                                        && ScriptSettings.getSettingsData().redChins)
                                                        .setSimpleName("Red Chins"),
                                                new HunterBranch().setSimpleName("Hunter training")
                                        ),
                                        new MuleOff().setSimpleName("Mule"),
                                        new Fractal().setSimpleName("Black Chins").addChildren(
                                                new GetBoxes().setSimpleName("Get boxes"),
                                                new EdgevilleBankLeaf().setSimpleName("EdgeBank"),
                                                new AntiPkNode().setSimpleName("AntiPK"),
                                                new NewBlacksChins().setSimpleName("Blacks")
                                        )
                                ),
                        new TalkToFractal(() -> PlayerSettings.getBitValue(9652) < 3, new Tile(3280, 3412), () -> NPCs.closest("Regulus Cento"))
                                .setDialogueOptions("Let's do it!")
                                .setSimpleName("First time valamore"),
                        new MuleOff().setSimpleName("Mule"),
                        new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < (isSunlight ? 72 : 75)).setSimpleName("Hunter training"),
                        new SunlightAntelopes(() -> isSunlight).setSimpleName("Sunlights")
                                .setAppendLogic(() -> {
                                            if (FALCONRY_AREA.contains(Players.getLocal())) {
                                                Magic.castSpell(Normal.HOME_TELEPORT);
                                                Sleep.sleepUntil(() -> !FALCONRY_AREA.contains(Players.getLocal()), 30_0000);
                                                return true;
                                            }
                                            return false;
                                        }
                                ),
                        new MoonlightMoths(() -> isMoths).setSimpleName("Moths")
                                .setAppendLogic(() -> {
                                            if (FALCONRY_AREA.contains(Players.getLocal())) {
                                                Magic.castSpell(Normal.HOME_TELEPORT);
                                                Sleep.sleepUntil(() -> !FALCONRY_AREA.contains(Players.getLocal()), 30_0000);
                                                return true;
                                            }
                                            return false;
                                        }
                                ),
                        new MoonlightMoths(() -> ScriptSettings.getSettingsData().trainMode == TrainMode.MOTHS
                                && !isMoths && hasMoths && Skills.getRealLevel(Skill.HUNTER) < 91).setSimpleName("Moths"),
                        new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 91).setSimpleName("Hunter training"),
                        new AntelopeBranch(() -> true).setSimpleName("Antelopes")
                                .setAppendLogic(() -> {
                                            if (FALCONRY_AREA.contains(Players.getLocal())) {
                                                Magic.castSpell(Normal.HOME_TELEPORT);
                                                Sleep.sleepUntil(() -> !FALCONRY_AREA.contains(Players.getLocal()), 30_0000);
                                                return true;
                                            }
                                            return false;
                                        }
                                )
                );
//        new AIAntiban();


        WebFinder.getWebFinder().createAndAddNode(new Tile(2357, 3465, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2349, 3470, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2337, 3473, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2326, 3477, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2320, 3485, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2320, 3490, 0));

        Area eaglesPeakForest = new Area(
                new Tile(2314, 3489, 0),
                new Tile(2322, 3499, 0),
                new Tile(2332, 3505, 0),
                new Tile(2347, 3500, 0),
                new Tile(2353, 3490, 0),
                new Tile(2361, 3492, 0),
                new Tile(2357, 3505, 0),
                new Tile(2345, 3512, 0),
                new Tile(2331, 3513, 0),
                new Tile(2317, 3501, 0));

        LocalPathFinder l = LocalPathFinder.getLocalPathFinder();
        for (Tile t : eaglesPeakForest.getTiles()) {
            l.addBlacklistedTile(t);
        }


        Area dragonsBeHere = new Area(
                new Tile(3117, 3747, 0),
                new Tile(3188, 3744, 0),
                new Tile(3186, 3683, 0),
                new Tile(3179, 3656, 0),
                new Tile(3136, 3655, 0),
                new Tile(3116, 3702, 0));
        WebFinder wf = WebFinder.getWebFinder();
        List<AbstractWebNode> dragonNodes = wf.getAll().stream().filter(x -> dragonsBeHere.contains(x.getTile())).collect(Collectors.toList());
        dragonNodes.forEach(wf::removeNode);

    }


    Timer onMountainTimer = new Timer(3 * 60 * 1000);
    Timer onBoatTimer = new Timer(3 * 60 * 1000);
    Timer playerLogTimer = new Timer(60 * 1000);
    public static boolean hasStuffInBags = false;

    @Override
    public int onLoop() {
//        if (!Client.isLoggedIn()) {
//            webhookListener.loginResponse(LoginUtility.login());
//        }
//
//
//        if (Bank.getLastBankHistoryCacheTime() <= 1) {
//            Logger.info("Get bank cache");
//            if (Bank.isOpen()) Bank.close();
//            if (Walking.shouldWalk()) Bank.open();
//            return ReactionGenerator.getLong();
//        }
        if (Inventory.contains(ItemID.MOONLIGHT_ANTELOPE_FUR, ItemID.SUNLIGHT_ANTELOPE_FUR, ItemID.RAW_SUNLIGHT_ANTELOPE, ItemID.RAW_MOONLIGHT_ANTELOPE)) {
            hasStuffInBags = true;
        }

        if (Inventory.contains(ItemID.LARGE_FUR_POUCH_CLOSED)) {
            Inventory.interact(ItemID.LARGE_FUR_POUCH_CLOSED, "Open");
        }


        if (Inventory.contains(ItemID.LARGE_MEAT_POUCH_CLOSED)) {
            Inventory.interact(ItemID.LARGE_MEAT_POUCH_CLOSED, "Open");
        }

        if (Inventory.contains(ItemID.LARGE_MEAT_POUCH_OPEN, ItemID.LARGE_FUR_POUCH_OPEN)
                && hasStuffInBags && Bank.getClosestBankLocation().distance(Players.getLocal().getTile()) < 20) {
            if (!Inventory.isFull()) {
                Logger.info("Empty bags");
                Inventory.interact(ItemID.LARGE_FUR_POUCH_OPEN, "Empty");
                Sleep.sleep(200);
                Inventory.interact(ItemID.LARGE_MEAT_POUCH_OPEN, "Empty");
                hasStuffInBags = false;
                return ReactionGenerator.getNormal();
            }
        }


        // failsafe for a walker bug im unable to reproduce on boats
        if (Players.getLocal().getZ() != 1) {
            onBoatTimer.reset();
        }

        if (onBoatTimer.finished()) {
            Logger.info("Escape boat");
            Inventory.interact(ItemID.VARROCK_TELEPORT, "Break");
            onBoatTimer.reset();
            return ReactionGenerator.getNormal();
        }

        Player attckingMe = Players.closest(x -> x.isSkulled() && x.isInteracting(Players.getLocal()));
        if (Combat.isInWild() && Players.getLocal().isInCombat() && attckingMe != null && playerLogTimer.finished()) {
            Logger.info("Being attack by " + attckingMe.getName());
            Logger.info("Level: " + attckingMe.getLevel());
            Logger.info("My Level: " + Combat.getCombatLevel());
            Logger.info("Wilderness level: " + Combat.getWildernessLevel());
            Logger.info("Predicted: " + AntiPkNode.canAttackMe(attckingMe));
            attckingMe.getEquipment().forEach(x -> Logger.info("Equipment " + x.getID() + " " + x.getName()));
            playerLogTimer.reset();
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            Logger.info("Disabling hop confirmations");
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getQuick();
        }

        if (Combat.isAutoRetaliateOn()) {
            Log.info("Disable auto retaliate");
            if (Bank.isOpen()) Bank.close();
            Combat.toggleAutoRetaliate(false);
            return ReactionGenerator.getNormal();
        }

        Character femi = NPCs.closest("Femi");
        if (femi != null && femi.distance() < 5 && Dialogues.inDialogue()) {
            Dialog.solve("bit busy");
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isAcceptAidEnabled()) {
            Logger.info("Toggling off accept aid");
            if (Bank.isOpen()) Bank.close();
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isSellPriceWarningEnabled()) {
            Logger.info("Toggling off sell price warning");
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleSellPriceWarning(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isBuyPriceWarningEnabled()) {
            Logger.info("Toggling off buy price warning");
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
        tree.deregisterListeners();
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
        Player lp = Players.getLocal();
        Character interacting = lp == null ? null : lp.getCharacterInteractingWithMe();
        return new String[]{
                (isMoths ? "cCMoonlightMoths" : "cCAntelopeFarm ") + runtime.formatTime(),
                "Owned reds " + hasRedChins,
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Hunter lvl: " + Skills.getRealLevel(Skill.HUNTER),
                "Mule timer " + (MuleOff.timer == null ? "Still training" : formatTime(MuleOff.timer.remaining())),
                "Interacting " + (interacting == null ? "" : interacting) + " " + (interacting == null ? "" : interacting.distance()),

                String.format("Moths %s (%s)",
                        df.format(MoonlightMoths.earned),
                        df.format(runtime.getHourlyRate(MoonlightMoths.earned))
                ),
                String.format("Antelopes %s (%s) Splinters: %s",
                        df.format(AntelopeBranch.lootValue),
                        df.format(runtime.getHourlyRate(AntelopeBranch.lootValue + splinterCount * LivePrices.getHigh(ItemID.SUNFIRE_SPLINTERS))),
                        df.format(splinterCount)
                ),
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

    public static boolean shouldHop;
    int deathCount;

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            shouldHop = true;
            deathCount++;
        }
    }


    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        int amountAdded = incoming.getAmount() - existing.getAmount();
        if (incoming.getID() == ItemID.SUNFIRE_SPLINTERS) {
            splinterCount += amountAdded;
        }
        if (!Combat.isInWild()) return;
        if (incoming.getID() == ItemID.BLACK_CHINCHOMPA) {
            // amount other than 1 means something sus happeneing
            if (amountAdded == 1) {
                blacksCaught++;
            }
        }

    }
}
