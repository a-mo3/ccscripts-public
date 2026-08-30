package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.*;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.loadout.EquipmentLoadout;
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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCMagicTrees", author = "camalCase", version = 0.0)
public class MagicTreesAIO extends AbstractScript implements PaintInfo, HumanMouseListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();

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

    Area REDWOODS = new Area(1566, 3496, 1574, 3479, 1);
    Area WC_GUILD_AREA = new Area(1549, 3525, 1678, 3461);
    Area WC_ENTRANCE = new Area(1659, 3508, 1663, 3499);

    private void init() {
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        ScriptSettings.setSettingsData(settings);
        new WebhookListener();

        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));
        tree.setSimpleName("cCMagicTrees")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new MuleOff(ScriptSettings.getSettingsData().hoursUntilMuleOff, ScriptSettings.getSettingsData().gpRemainingAfterMuling)
                                .setAppendLogic(() -> {
                                    if (REDWOODS.contains(Players.getLocal())) {
                                        GameObject ropeLadder = GameObjects.closest("Rope ladder");
                                        if (ropeLadder != null && ropeLadder.interact("Climb-down")) {
                                            Sleep.sleepUntil(() -> !REDWOODS.contains(Players.getLocal()), 5000);
                                        }
                                        return true;
                                    }
                                    return false;
                                })
                                .setSimpleName("Mule off"),
                        new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 75 || ScriptSettings.getSettingsData().ftpOnly).setSimpleName("Training"),
                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get Membership"),
//                new AutoProggy().setSimpleName("Auto proggy"),
                        new LampHandler().setSimpleName("Lamp"),
                        new XMarksTheSpot().setSimpleName("X Marks the spot"),
//                        new ClientOfKourend().setSimpleName("Client of kourend"),
//                        new GetHosidiousFavour().setSimpleName("Get hosidious favour"),
                        new GenericChopLeaf(() -> ScriptSettings.getSettingsData().chopRedwood
                                && Skills.getRealLevel(Skill.WOODCUTTING) >= 90,
                                REDWOODS,
                                x -> x.getName().equals("Redwood tree") && x.hasAction("Cut"))
                                .setAction("Cut")
                                .setBankLogs(true)
                                .setAppendLogic(() -> {
                                    if (Inventory.isFull()) {
                                        if (REDWOODS.contains(Players.getLocal())) {
                                            GameObject ropeLadder = GameObjects.closest("Rope ladder");
                                            if (ropeLadder != null && ropeLadder.interact("Climb-down")) {
                                                Sleep.sleepUntil(() -> !REDWOODS.contains(Players.getLocal()), 5000);
                                            }
                                        }
                                        return false;
                                    }

                                    if (!REDWOODS.contains(Players.getLocal()) && MixedChopping.AXE_LOADOUT.isFulfilled()) {
                                        if (Walking.shouldWalk()) {
                                            GameObject ropeLadder = GameObjects.closest("Rope ladder");
                                            if (ropeLadder != null && ropeLadder.interact("Climb-up")) {
                                                Sleep.sleepUntil(() -> REDWOODS.contains(Players.getLocal()), 5000);
                                                return true;
                                            }
                                            if (Walking.shouldWalk(6)) Walking.walk(REDWOODS);
                                        }
                                        return true;
                                    }
                                    return false;
                                })
                                .setSimpleName("Chop Redwoods")
                                .setInventoryLoadout(MixedChopping.AXE_LOADOUT),

                        new GenericChopLeaf(() -> ScriptSettings.getSettingsData().chopMagic
                                && Skills.getRealLevel(Skill.WOODCUTTING) >= 75,
//                                new Area(1576, 3494, 1583, 3481),
                                new Area(1576, 3496, 1586, 3478),
                                x -> x.getName().equals("Magic tree")
                        ).setBankLogs(true)
                                .setSimpleName("Chop Magics")
                                .setInventoryLoadout(MixedChopping.AXE_LOADOUT)
                                .setEquipmentLoadout(new EquipmentLoadout()
                                        .addItem(EquipmentSlot.AMULET, ItemVariants.SKILLS_NECKLACE))
                        // todo redwoods, might not be generic have to climb the tree, maybe just append logic.
                );
//        new AIAntiban();
    }


    @Override
    public int onLoop() {
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
                "cCMagicTrees " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Account Code: " + ShuffleFractal.getLoginValue(),
                "WC lvl: " + Skills.getRealLevel(Skill.WOODCUTTING)
        };
    }
}
