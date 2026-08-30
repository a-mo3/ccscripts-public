package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.mta.BuyMTAReward;
import org.dreambot.behaviour.method.mta.MTAPointManager;
import org.dreambot.behaviour.method.mta.MTAReward;
import org.dreambot.behaviour.method.mta.UnlockMTA;
import org.dreambot.behaviour.method.mta.alchemy.AlchemyRoomMTA;
import org.dreambot.behaviour.method.mta.enchant.EnchantRoomMTA;
import org.dreambot.behaviour.method.mta.graveyard.GraveyardRoomMTA;
import org.dreambot.behaviour.method.mta.telekinetic.TelekineticMazeMTA;
import org.dreambot.behaviour.method.mta.telekinetic.TelekineticNodeManager;
import org.dreambot.behaviour.method.spindel.ExitWithLoot;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.TimedShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.MTASettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;

public class MTAScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<MTASettings> tree = new FractalRoot<>(new MTASettings(), getScriptName());

    @Override
    public void onArgs(String... args) {
        for (String arg : args) {
            Logger.info("Checking arg-" + arg);
            reward = Arrays.stream(MTAReward.values())
                    .filter(reward -> arg.equals(reward.name()))
                    .findFirst().orElse(null);
        }
    }

    MTAReward reward = null;

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

//        WithdrawLoadoutEvent.sellList = LootSpindel.LOOT;
        MuleOff.LOOT = new int[]{
                ItemID.INFINITY_BOOTS,
                ItemID.INFINITY_BOTTOMS,
                ItemID.INFINITY_GLOVES,
                ItemID.INFINITY_TOP,
                ItemID.INFINITY_HAT,
                ItemID.MASTER_WAND,
                ItemID.MAGES_BOOK,

                ItemID.EMERALD_RING,
                ItemID.RING_OF_DUELING8,
                ItemID.SAPPHIRE_RING,
                ItemID.RING_OF_RECOIL
        };

        Logger.info("Init");
        tree.setSimpleName("cCMTA");
        if (reward == null) reward = tree.getSettings().mtaRewardTarget;

        MTAPointManager pointManager = MTAPointManager.get();
        tree.addChildren(
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Empty coffer"),
                new MuleOff().setSimpleName("Mule off"),

//                new WitchsHouse().setSimpleName("Witchs house"),
                new TimedShuffleFractal(Calculations.random(30, 80))
                        .addChildren(
                                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.ATTACK) < 30).setSimpleName("30 atk for battle staves"),
                                new ConfigurableMagicBranch(() -> Skill.MAGIC.getLevel() < 57).setSimpleName("57 mage")
                        ),
                new UnlockMTA().setSimpleName("Unlock MTA"),
                new EnchantRoomMTA(() -> reward.getRequiredEnchantPoints() > MTAPointManager.get().getEnchantPoints()),
                new AlchemyRoomMTA(() -> reward.getRequiredAlchemyPoints() > MTAPointManager.get().getAlchemyPoints()),
                new GraveyardRoomMTA(() -> reward.getRequiredGraveyardPoints() > MTAPointManager.get().getGraveyardPoints()),
                new TelekineticMazeMTA(() -> reward.getRequiredTelekineticPoints() > MTAPointManager.get().getTelekineticPoints()),
                new BuyMTAReward(() -> true, reward)
        );
//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        TelekineticNodeManager.manage();

        if (!Combat.isInWild() && !Bank.isCached()) {
            if (!SpecialWalker.leaveAvasRoom()) return ReactionGenerator.getNormal();
            if (Bank.isOpen()) Bank.close();
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }


        if (!ChangeAlchWarning.setHighAlchWarning(500_000)) {
            Logger.info("Setting alch warning price");
            return ReactionGenerator.getNormal();
        }


        // dont run the tree while hopping worlds because equipment state will make you do loadouts you shouldnt
        if (Client.getGameStateId() == 45) return ReactionGenerator.getQuick();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    Timer cacheTime = new Timer(5 * 1000);
    int invValue = -1;

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

        // todo something here for melee mode
        if (cacheTime.finished()) {
            cacheTime.reset();
            invValue = ExitWithLoot.inventoryValue();
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Enchant p " + MTAPointManager.get().getEnchantPoints(),
                "Alchemy p " + MTAPointManager.get().getAlchemyPoints(),
                "Grave p " + MTAPointManager.get().getGraveyardPoints(),
                "Tele p " + MTAPointManager.get().getTelekineticPoints(),
                "Reward " + reward,
                "Magic level " + Skill.MAGIC.getLevel()
        };
    }

    @Override
    public String getScriptName() {
        return "cCMTAFarm";
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

    @Override
    public void onScriptPaint(Graphics g) {
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


    // todo something here for tracking when you buy rewards ig
    public void onInventoryItemAdded(Item item) {
        if (Players.getLocal().getZ() != 1) return;
        boolean isReward = Arrays.stream(MTAReward.values()).anyMatch(x -> x.getItemName().equals(item.getName()));
        if (isReward) grossGp += item.getLivePrice();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
    }
}
