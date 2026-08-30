package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
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
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.*;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.OwnedItems;
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
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCChaosDruidFarm", author = "camalCase", version = 0.02)
public class ChaosDruids extends AbstractScript implements PaintInfo, HumanMouseListener, ChatListener {
    static Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();
    public static int deathCount = 0;

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
        init(params);
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

    private void init(String... params) {
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        ScriptSettings.setSettingsData(settings);
        new WebhookListener();

        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));


        Supplier<NPC> druidSupplier = () -> NPCs.closest(x -> x.getName().toLowerCase().contains("druid")
                && x.hasAction("Attack") && !x.isInCombat() && x.canReach() && Druids.DRUIDS_WILDY.contains(x));

        if (params != null && Arrays.stream(params).anyMatch(x -> x.toLowerCase().contains("stage")))
            ScriptSettings.getSettingsData().stage = true;
        tree.setSimpleName("cCChaosDruids")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new GetMembership(() -> !ScriptSettings.getSettingsData().stage).setSimpleName("Get Membership"),
                        new MixedCombat(() -> {
                            int[] lvls = new int[]{
                                    Skills.getRealLevel(Skill.DEFENCE),
                                    Skills.getRealLevel(Skill.ATTACK),
                                    Skills.getRealLevel(Skill.STRENGTH)
                            };

                            return Skills.getRealLevel(Skill.HITPOINTS) < 40 && Arrays.stream(lvls).anyMatch(x -> x < ScriptSettings.getSettingsData().combatTargets)
                                    || Skills.getRealLevel(Skill.HITPOINTS) < ScriptSettings.getSettingsData().hitpointsTarget;
                        })
                                .setStyleSupplier(ScriptSettings.getSettingsData().pureMode ? this::pureSwitch : null)
                                .setSimpleName("Training"),

                        new ApulzCombat(() -> {
                            int[] lvls = new int[]{
                                    Skills.getRealLevel(Skill.DEFENCE),
                                    Skills.getRealLevel(Skill.ATTACK),
                                    Skills.getRealLevel(Skill.STRENGTH)
                            };

                            return Arrays.stream(lvls).anyMatch(x -> x < ScriptSettings.getSettingsData().combatTargets)
                                    || Skills.getRealLevel(Skill.HITPOINTS) < ScriptSettings.getSettingsData().hitpointsTarget;
                        })
                                .setStyleSupplier(ScriptSettings.getSettingsData().pureMode ? this::pureSwitch : null)
                                .setSimpleName("Training"),

                        new Fractal(() -> ScriptSettings.getSettingsData().stage)
                                .setAppendLogic(() -> {
                                    System.exit(0);
                                    return true;
                                }),
                        new MuleOff(),
                        new StandardCombat(() -> true, Druids.DRUIDS_WILDY, druidSupplier)
                                .setLootStrategy(x -> x != null && Druids.DRUIDS_WILDY.contains(x)
                                                && (LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootVal
                                                || ItemVariants.LOOTING_BAG.contains(x.getID())),
                                        ItemID.LOBSTER
                                )
                                .setStyleSupplier(
                                        () -> {
                                            if (ScriptSettings.getSettingsData().pureMode) return pureSwitch();
                                            int atk = Skills.getRealLevel(Skill.ATTACK);
                                            int str = Skills.getRealLevel(Skill.STRENGTH);
                                            int def = Skills.getRealLevel(Skill.DEFENCE);
                                            int limit = ScriptSettings.getSettingsData().combatTargets;
                                            if (def + 9 < atk) {
                                                return CombatStyle.DEFENCE;
                                            }
                                            if (atk + 9 <= str || def >= atk) {
                                                return CombatStyle.ATTACK;
                                            }
                                            if (atk >= str) return CombatStyle.STRENGTH;
                                            return Combat.getCombatStyle();
                                        }

                                )
                                .setFoodID(Collections.singletonList(ScriptSettings.getSettingsData().foodId))
                                .setInventoryLoadout(new InventoryLoadout()
                                        .addItem(ItemID.LOBSTER, 1, 8)

                                        .addItem(ItemVariants.LOOTING_BAG)
                                        .setEnabledCondition(() -> ScriptSettings.getSettingsData().useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                                        .setStrictSupplier(() -> BankLocation.EDGEVILLE.getArea(50).contains(Players.getLocal()))
                                )
                                .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_P2P)
                                .setSimpleName("Druids combat")

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
                    Worlds.getRandomWorld(x -> !x.isF2P() && x.isNormal() && x.getMinimumLevel() < Skills.getTotalLevel())
            )) shouldHop = false;
            return ReactionGenerator.getNormal();
        }

        if (ScriptSettings.getSettingsData().useLootingBag && hasLootInBag) {
            if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null) {
                Logger.info("Emptying looting bag");
                Logger.info("Empty loot bag " + new EmptyLootingBagEvent().executed());
                hasLootInBag = false;
            }
        }


        if (Inventory.contains(ItemID.LOOTING_BAG_CLOSED)) {
            Logger.info("Opening looting bag");
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Opening looting bag");
            Inventory.interact(ItemID.LOOTING_BAG_CLOSED, "Open");
            Sleep.sleepUntil(() -> !Inventory.contains(ItemID.LOOTING_BAG_OPENED), 1800);
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
        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        return new String[]{
                "cCChaosDruids " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "isMember: " + Client.isMembers(),
                "Time Until Mule off: " + muleOff,
                "Deaths: " + deathCount,
//                "Location: " + LocationConfig.getName(),
                "LoopSpd: " + loopSpd,
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

    private CombatStyle pureSwitch() {
        int atk = Skills.getRealLevel(Skill.ATTACK);
        int str = Skills.getRealLevel(Skill.STRENGTH);
        if (atk + 5 >= str) return CombatStyle.STRENGTH;
        return CombatStyle.ATTACK;
    }


    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            shouldHop = true;
            deathCount++;
        }
    }
}
