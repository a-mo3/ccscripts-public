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
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
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
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.eaglespeak.EaglesPeak;
import org.dreambot.behaviour.impl.AntiPkNode;
import org.dreambot.behaviour.impl.EdgevilleBankLeaf;
import org.dreambot.behaviour.impl.GetBoxes;
import org.dreambot.behaviour.impl.NewBlacksChins;
import org.dreambot.behaviour.training.BoxTrapState;
import org.dreambot.behaviour.training.HunterBranch;
import org.dreambot.behaviour.training.NewRedChins;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.muling.Log;
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
import java.util.stream.Collectors;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCBlackChinsFarm", author = "camalCase", version = 0.2)
public class BlacksFarm extends AbstractScript implements PaintInfo, HumanMouseListener, ChatListener, ItemContainerListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    public static final Area SHRIMP_AREA = new Area(3240, 3159, 3246, 3141);
    public static final Area BARB_VILLAGE_FISHING = new Area(3101, 3422, 3111, 3435);
    WebhookListener webhookListener = new WebhookListener();
    int blacksCaught;
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

    boolean hasRedChins;

    private void init() {
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        ScriptSettings.setSettingsData(settings);
        new WebhookListener();

        Client.getInstance().getRandomManager().disableSolver(RandomEvent.LOGIN);
        Client.getInstance().getRandomManager().registerSolver(new CustomLoginHandler("CUSTOM_LOGIN"));
        Client.getInstance().getRandomManager().enableSolver("CUSTOM_LOGIN");

        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));
        hasRedChins = Client.getInstance().getScriptManager().hasSDNScript(1670)
                || Client.getInstance().getScriptManager().hasSDNScript(1671);
        tree.setSimpleName("cCBlacksFarm")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                        new MuleOff().setSimpleName("Mule"),
                        new EaglesPeak(() -> Skills.getRealLevel(Skill.HUNTER) >= 27 && !PaidQuest.EAGLES_PEAK.isFinished()).setSimpleName("Eagles Peak"),
                        new Fractal(() -> Skills.getRealLevel(Skill.HUNTER) < 73).setSimpleName("Training").addChildren(
                                new NewRedChins(() -> Skills.getRealLevel(Skill.HUNTER) >= 63
                                        && hasRedChins
                                        && ScriptSettings.getSettingsData().redChins)
                                        .setSimpleName("Red Chins"),
                                new HunterBranch().setSimpleName("Hunter training")
                        ),
//                        new MuleOff().setSimpleName("Mule"),
                        new Fractal().setSimpleName("Black Chins").addChildren(
                                new GetBoxes().setSimpleName("Get boxes"),
                                new EdgevilleBankLeaf().setSimpleName("EdgeBank"),
                                new AntiPkNode().setSimpleName("AntiPK"),
                                new NewBlacksChins().setSimpleName("Blacks")
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
    boolean hasLoadedTrie = false;
    Timer trieRefresh = new Timer(60 * 1000 * 45);

    @Override
    public int onLoop() {
        if (Client.isLoggedIn() && !Worlds.getCurrent().isNormal()) {
            Logger.info("Logging out for abnormal world");
            Client.setIdleTime(30_000);
            return ReactionGenerator.getNormal();
        }
//
//
//        if (Bank.getLastBankHistoryCacheTime() <= 1) {
//            Logger.info("Get bank cache");
//            if (Bank.isOpen()) Bank.close();
//            if (Walking.shouldWalk()) Bank.open();
//            return ReactionGenerator.getLong();
//        }
        if (trieRefresh.finished() || !hasLoadedTrie) {

            PKTrie.refreshPkerList();
            trieRefresh.reset();
            hasLoadedTrie = true;
        }

        if (AntiPkNode.shouldHop && Client.isMembers() && !Combat.isInWild()) {
            Logger.info("Hopping");
            if (WorldHopper.hopWorld(Worlds.getRandomWorld(x -> !x.isF2P()
                    && x.getMinimumLevel() < Skills.getTotalLevel()
                    && x.isNormal()))) AntiPkNode.shouldHop = false;
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
            PKTrie.reportPker(attckingMe.getName());
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            Logger.info("Disabling hop confirmations");
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getQuick();
        }

        // failsafe for a walker bug im unable to reproduce at the base on eagles peak
        if (!EaglesPeak.ON_MOUNTAIN.contains(Players.getLocal())) {
            onMountainTimer.reset();
        }

        if (onMountainTimer.finished()) {
            WorldHopper.quickHop(Worlds.getRandomWorld(x -> x.isMembers()
                    && x.getMinimumLevel() < Skills.getTotalLevel()
                    && x.isNormal()).getWorld());
            onMountainTimer.reset();
            return ReactionGenerator.getNormal();
        }


        // failsafe for a walker bug im unable to reproduce on boats
        if (Players.getLocal().getZ() != 1) {
            onBoatTimer.reset();
        }

        if (onBoatTimer.finished()) {
            Inventory.interact(ItemID.VARROCK_TELEPORT, "Break");
            onBoatTimer.reset();
            return ReactionGenerator.getNormal();
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

        try {
            for (BoxTrapState s : NewBlacksChins.boxTrapStates) {
                graphics.setColor(s.isMine() ? Color.GREEN : Color.YELLOW);
                if (s.getOwner() == BoxTrapState.Owner.SOMEONE_ELSE) graphics.setColor(Color.RED);
                if (s.getTile().distance() > 16) continue;
                graphics.drawPolygon(s.getTile().getPolygon());
            }
        } catch (Exception ignored) {
        }

        if (ScriptSettings.getSettingsData().pkerPainting && Combat.isInWild()) {
            for (Player p : Players.all(x -> x.distance() < 20)) {
                if (!AntiPkNode.canAttackMe(p)) {
                    graphics.setColor(Color.GREEN);
                    if (p.isOnScreen()) graphics.drawPolygon(p.getTile().getPolygon());
                } else {
                    if (p.isSkulled()) {
                        graphics.setColor(Color.RED);
                        if (p.isOnScreen()) graphics.drawPolygon(p.getTile().getPolygon());
                    } else {
                        graphics.setColor(Color.ORANGE);
                        if (p.isOnScreen()) graphics.drawPolygon(p.getTile().getPolygon());
                    }
                }
            }
        }

        scriptPaint.paint(graphics);
        fractalPaint.paint(graphics);
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "cCBlacksFarm " + runtime.formatTime(),
                "Owned reds " + hasRedChins,
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Hunter lvl: " + Skills.getRealLevel(Skill.HUNTER),
                "Deaths: " + deathCount + " " + runtime.getHourlyRate(deathCount) + "/hr",
                "Avoided: " + AntiPkNode.avoided,
                "Mule timer " + (MuleOff.timer == null ? "Still training" : formatTime(MuleOff.timer.remaining())),
                String.format("Reds earnt %s (%s)",
                        df.format((long) NewRedChins.chinsCaught * LivePrices.get(ItemID.RED_CHINCHOMPA_10034)),
                        df.format(runtime.getHourlyRate(NewRedChins.chinsCaught * LivePrices.get(ItemID.RED_CHINCHOMPA_10034)))
                ),
                String.format("Blacks earnt %s (%s)",
                        df.format((long) blacksCaught * LivePrices.get(ItemID.BLACK_CHINCHOMPA)),
                        df.format(runtime.getHourlyRate(blacksCaught * LivePrices.get(ItemID.BLACK_CHINCHOMPA)))
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
        if (!Combat.isInWild()) return;
        if (incoming.getID() == ItemID.BLACK_CHINCHOMPA) {
            int amountAdded = incoming.getAmount() - existing.getAmount();
            // amount other than 1 means something sus happeneing
            if (amountAdded == 1) {
                blacksCaught++;
            }
        }
    }
}
