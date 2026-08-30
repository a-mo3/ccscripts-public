package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
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
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.brutals.BrutalLoadouts;
import org.dreambot.behaviour.method.brutals.BrutalSafespotBranch;
import org.dreambot.behaviour.misc.MuleOff;
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
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.ConfigurableRangeTraining;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.BrutalBlueDragonSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;

public class BrutalBlueDragonScript extends PseudoScript implements PaintInfo, ChatListener, ItemContainerListener, SpawnListener {
    Timer runtime = new Timer();
    FractalRoot<BrutalBlueDragonSettings> tree = new FractalRoot<>(new BrutalBlueDragonSettings(), getScriptName());
    int grossGp = 0;

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
        tree.setSimpleName("cCBrutalBlueDragons")
                .addChildren(
                        new ReactionSettingsFractal(),
                        new PutPetAway(),
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("lamp handler"),
                        new EmptyDeathsCoffer().setSimpleName("Empty death"),

                        new GetMembershipBranch().setSimpleName("Get Membership"),
                        new AutoProggy().setSimpleName("Auto proggy"),
                        new AntibanFractal().setSimpleName("Antiban"),

                        new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < Math.min(43, tree.getSettings().prayerTarget))
                                .setSimpleName("Prayer training"),

//                        new ConfigurableMeleeTraining(())
//                                .setStyleSupplier(() -> {
//                                    int atk = Skills.getRealLevel(Skill.ATTACK);
//                                    int str = Skills.getRealLevel(Skill.STRENGTH);
//                                    int def = Skills.getRealLevel(Skill.DEFENCE);
//                                    if (Skills.getRealLevel(Skill.ATTACK) >= tree.getSettings().attackTarget)
//                                        atk = 100;
//                                    if (Skills.getRealLevel(Skill.STRENGTH) >= tree.getSettings().strengthTarget)
//                                        str = 100;
//                                    if (Skills.getRealLevel(Skill.DEFENCE) >= tree.getSettings().defenceTarget)
//                                        def = 100;
//                                    if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
//                                    if (atk <= def) return CombatStyle.ATTACK;
//                                    return CombatStyle.DEFENCE;
//                                })
//                                .setPrependLogic(() -> {
//                                    if (Client.isDynamicRegion()) {
//                                        Magic.castSpell(Normal.HOME_TELEPORT);
//                                        Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
//                                    }
//                                    return false;
//                                })
//                                .setSimpleName("Melee training"),

                        new ConfigurableRangeTraining(() -> Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget, tree.getSettings().defenceTarget)
                                .setSimpleName("Range training")
                                .setPrependLogic(() -> {
                                    if (Client.isDynamicRegion()) {
                                        Magic.castSpell(Normal.HOME_TELEPORT);
                                        Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                    }
                                    return false;
                                }),


                        new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < tree.getSettings().magicTarget)
                                .setSimpleName("Magic branch"),

                        new Fractal(() -> Quests.getQuestPoints() < 32).setSimpleName("Questin for antidragon shield")
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

                        // todo make sure prayers are configured
                        new BrutalSafespotBranch(() -> true,
                                () -> NPCs.closest(x -> x.distance() < 6
                                        && x.getCharactersInteractingWithMe().stream().noneMatch(p -> p instanceof Player)),
                                new Tile(1626, 10073))
                                .setInventoryLoadout(BrutalLoadouts.BRUTAL_INVENTORY)
                                .setEquipmentLoadout(BrutalLoadouts.WATER_STAFF)
                                .setSimpleName("Brutal blues")

                );
        Logger.info("---- " + tree.getChildren().size());
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();

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
        return "cCBrutalBlueDragonFarm";
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
    public void onInventoryItemAdded(Item item) {
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity >= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
    }

}
