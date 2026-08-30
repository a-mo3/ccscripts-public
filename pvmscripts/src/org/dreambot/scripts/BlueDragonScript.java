package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.bluedragons.GoToBlueDragons;
import org.dreambot.behaviour.method.bluedragons.KillBlueDragon;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.quests.*;
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
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.agility.AlchAgilityBranch;
import org.dreambot.behaviour.training.combat.F2PMeleeCombats;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.magic.F2PMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.DistributedRangeTraining;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.BlueDragonSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class BlueDragonScript extends PseudoScript implements PaintInfo, ChatListener, ItemContainerListener, SpawnListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    FractalRoot tree = new FractalRoot(new BlueDragonSettings(), getScriptName());
    WebhookListener webhookListener = new WebhookListener();
    public static int deathCount = 0;
    boolean shouldHop = false;
    public static final Area MID_DRAGON_AREA = new Area(3197, 3839, 3214, 3830);
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");
    boolean needsToRecharge = false;

    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();


    public void init() {
        Client.getInstance().addEventListener(this);
        MuleOff.LOOT = new int[]{
                ItemID.BLUE_DRAGONHIDE,
                ItemID.DRAGON_BONES,
                ItemID.RUNE_DAGGER,
                ItemID.NATURE_RUNE,
                ItemID.LAW_RUNE,
                ItemID.ADAMANT_FULL_HELM,
                ItemID.RING_OF_RECOIL,
                ItemID.RUNE_ARROW
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;
        // mule off items should be good with default
        Logger.info("aaaaa " + tree.getChildren().size());
        tree.setSimpleName("cCBlueDragons")
                .addChildren(
                        new AutoProggy().setSimpleName("Auto proggy"),
                        new AntibanFractal().setSimpleName("Antiban"),
                        new ReactionSettingsFractal(),
                        new PutPetAway(),
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("lamp handler"),
                        new EmptyDeathsCoffer().setSimpleName("Empty death"),
                        new F2PMeleeCombats(() -> shouldTrainMelee() && SettingsRepository.findInstanceOf(new BlueDragonSettings()).ftpMeleeTraining,
                                SettingsRepository.findInstanceOf(new BlueDragonSettings()).attackTarget,
                                SettingsRepository.findInstanceOf(new BlueDragonSettings()).strengthTarget,
                                SettingsRepository.findInstanceOf(new BlueDragonSettings()).defenceTarget,
                                () -> {
                                    int atk = Skills.getRealLevel(Skill.ATTACK);
                                    int str = Skills.getRealLevel(Skill.STRENGTH);
                                    int def = Skills.getRealLevel(Skill.DEFENCE);
                                    if (Skills.getRealLevel(Skill.ATTACK) >= SettingsRepository.findInstanceOf(new BlueDragonSettings()).attackTarget)
                                        atk = 100;
                                    if (Skills.getRealLevel(Skill.STRENGTH) >= SettingsRepository.findInstanceOf(new BlueDragonSettings()).strengthTarget)
                                        str = 100;
                                    if (Skills.getRealLevel(Skill.DEFENCE) >= SettingsRepository.findInstanceOf(new BlueDragonSettings()).defenceTarget)
                                        def = 100;
                                    if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                                    if (atk <= def) return CombatStyle.ATTACK;
                                    return CombatStyle.DEFENCE;
                                }
                        ).setSimpleName("F2P Melee Combats"),

                        new F2PMagicBranch(() -> SettingsRepository.findInstanceOf(new BlueDragonSettings()).ftpMagicTraining
                                && Skills.getRealLevel(Skill.MAGIC) < SettingsRepository.findInstanceOf(new BlueDragonSettings()).magicTarget,
                                SettingsRepository.findInstanceOf(new BlueDragonSettings()).defenceTarget
                        ).setSimpleName("F2P Magic Training"),

                        new DistributedRangeTraining(() -> SettingsRepository.findInstanceOf(new BlueDragonSettings()).ftpRangeTraining
                                && Skills.getRealLevel(Skill.RANGED) < SettingsRepository.findInstanceOf(new BlueDragonSettings()).rangeTarget)
                                .setSimpleName("F2P range"),

                        new GetMembershipBranch().setSimpleName("Get Membership"),

                        new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < Math.min(43, SettingsRepository.findInstanceOf(new BlueDragonSettings()).prayerTarget))
                                .setSimpleName("Prayer training"),

                        new ConfigurableMeleeTraining(this::shouldTrainMelee)
                                .setStyleSupplier(() -> {
                                    int atk = Skills.getRealLevel(Skill.ATTACK);
                                    int str = Skills.getRealLevel(Skill.STRENGTH);
                                    int def = Skills.getRealLevel(Skill.DEFENCE);
                                    if (Skills.getRealLevel(Skill.ATTACK) >= SettingsRepository.findInstanceOf(new BlueDragonSettings()).attackTarget)
                                        atk = 100;
                                    if (Skills.getRealLevel(Skill.STRENGTH) >= SettingsRepository.findInstanceOf(new BlueDragonSettings()).strengthTarget)
                                        str = 100;
                                    if (Skills.getRealLevel(Skill.DEFENCE) >= SettingsRepository.findInstanceOf(new BlueDragonSettings()).defenceTarget)
                                        def = 100;
                                    if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                                    if (atk <= def) return CombatStyle.ATTACK;
                                    return CombatStyle.DEFENCE;
                                })
                                .setPrependLogic(() -> {
                                    if (Client.isDynamicRegion()) {
                                        Magic.castSpell(Normal.HOME_TELEPORT);
                                        Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                    }
                                    return false;
                                })
                                .setSimpleName("Melee training"),

                        SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < SettingsRepository.findInstanceOf(new BlueDragonSettings()).rangeTarget)
                                .setDefenceTarget(SettingsRepository.findInstanceOf(new BlueDragonSettings()).defenceTarget)
                                .setSimpleName("Range training")
                                .setPrependLogic(() -> {
                                    if (Client.isDynamicRegion()) {
                                        Magic.castSpell(Normal.HOME_TELEPORT);
                                        Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                    }
                                    return false;
                                }),

                        new RestlessGhost().setSimpleName("Restless ghost"),
                        new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                        new PriestInPeril().setSimpleName("Priest in peril"),
                        new AlchAgilityBranch(() -> SettingsRepository.findInstanceOf(new BlueDragonSettings()).agilityAlch && Skills.getRealLevel(Skill.AGILITY) < 65)
                                .setSimpleName("Alch Agilty"),

                        new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < SettingsRepository.findInstanceOf(new BlueDragonSettings()).magicTarget)
                                .setSimpleName("Magic branch"),

                        new AgilityBranch(() -> Skills.getRealLevel(Skill.AGILITY) < 65).setSimpleName("Get 65 Agility)"),

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

                        new MuleOff()
                                .setSimpleName("Mule Off"),

                        new GetOff330(x -> x.getMinimumLevel() < Skills.getTotalLevel() && x.isNormal() && x.getWorld() != 401 && x.isMembers()).setSimpleName("Off 330"),
                        new KillBlueDragon().setSimpleName("Kill blue dragon"),
                        new GoToBlueDragons().setSimpleName("Go to blue dragons")
                );
        Logger.info("---- " + tree.getChildren().size());
        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));
    }


    public static boolean hasLootInBag = true;
    private long loopSpd;
    private long lastTimestamp;
    boolean hasLoadedTrie = false;
    Timer trieRefresh = new Timer(60 * 1000 * 45);
    Timer playerLogTimer = new Timer(60 * 1000);

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        // todo anti pk
//        if (!ScriptSettings.getSettingsData().disablePkList && trieRefresh.finished() || !hasLoadedTrie) {
//            PKTrie.refreshPkerList();
//            trieRefresh.reset();
//            hasLoadedTrie = true;
//        }

        loopSpd = System.currentTimeMillis() - lastTimestamp;
        lastTimestamp = System.currentTimeMillis();
        if (ClientSettings.isAcceptAidEnabled()) {
            Logger.info("Disable accept aid");
            if (Widgets.isOpen()) Widgets.closeAll();
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }

        if (!Combat.isAutoRetaliateOn()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            Logger.info("Disabling hop confirmations");
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getQuick();
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getNormal();
        }

        if (shouldHop && !Players.getLocal().isInCombat()) {
            if (WorldHopper.hopWorld(
                    Worlds.getRandomWorld(x -> !x.isF2P() && x.isNormal() && x.getWorld() != 401 && x.getMinimumLevel() < Combat.getCombatLevel())
            )) shouldHop = false;
            return ReactionGenerator.getNormal();
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
    public String[] getPaintInfo() {
        String muleOff = "-";
        Player local = Players.getLocal();
        String target = "";
        if (local != null) {
            Character tgt = local.getInteractingCharacter();
            if (tgt != null) target = tgt.getName();
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
        };
    }

    @Override
    public String getScriptName() {
        return "cCBlueDragonFarm";
    }

    @Override
    public int getMoneyMade() {
        return grossGp;
    }

    @Override
    public Timer getRuntime() {
        return runtime;
    }

    @Override
    public long getMuleOffTime() {
        return MuleOff.timer == null ? 0 : MuleOff.timer.remaining();
    }

    @Override
    public Fractal getFractal() {
        return tree;
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
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            shouldHop = true;
            deathCount++;
        }

        if (message.getMessage().toLowerCase().contains("not enough revenant ether")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("has run out of revenant")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("chainmace is out of charges")) {
            needsToRecharge = true;
        }
        if (message.getMessage().toLowerCase().contains("giving it a total of")) {
            needsToRecharge = false;
        }
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (!KillBlueDragon.BLUE_DRAGON_AREA.contains(Players.getLocal())) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!KillBlueDragon.BLUE_DRAGON_AREA.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity >= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
    }

    private boolean shouldTrainMelee() {
        BlueDragonSettings settings = SettingsRepository.findInstanceOf(new BlueDragonSettings());
        if (Skills.getRealLevel(Skill.ATTACK) < settings.attackTarget) return true;
        if (Skills.getRealLevel(Skill.DEFENCE) < settings.defenceTarget) return true;
        return Skills.getRealLevel(Skill.STRENGTH) < settings.strengthTarget;
    }
}
