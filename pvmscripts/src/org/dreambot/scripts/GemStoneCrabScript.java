package org.dreambot.scripts;

import com.google.common.collect.ImmutableMap;
import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.gemstone.GemstoneCrab;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabMeleeLoadout;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.XMarksTheSpot;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.quests.rfd.GetRockCake;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.fractals.util.StopScriptFractal;
import org.dreambot.scriptdata.GemstoneCrabSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;
import org.dreambot.webintegration.WebLoadoutLoader;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

public class GemStoneCrabScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<GemstoneCrabSettings> tree = new FractalRoot<>(new GemstoneCrabSettings(), getScriptName());

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

        Logger.info("Init");
        SlayerTaskMap.minLootValue = 1000;
        tree.setSimpleName("cCGemstoneCrab");
        AbstractEvent.globalInterruptCondition = () -> Inventory.contains("Coin pouch");

        MuleOff.LOOT = new int[]{
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        HashMap<Skill, CombatStyle> skillStyleMap = new HashMap<>();
        skillStyleMap.put(Skill.STRENGTH, CombatStyle.STRENGTH);
        skillStyleMap.put(Skill.ATTACK, CombatStyle.ATTACK);
        skillStyleMap.put(Skill.DEFENCE, CombatStyle.DEFENCE);


        EquipmentLoadout parsedMagicLoadout = null;
        if (tree.getSettings().customMagicLoadout != null && !tree.getSettings().customMagicLoadout.isEmpty()) {
            Logger.info("Trying to parse custom loadout");
            try {
                parsedMagicLoadout = WebLoadoutLoader.parseEquipment(tree.getSettings().customMagicLoadout);
            } catch (Exception e) {
                Logger.info(e);
                e.printStackTrace();
            }
        }

        EquipmentLoadout parsedRangeLoadout = null;
        if (tree.getSettings().customRangeLoadout != null && !tree.getSettings().customRangeLoadout.isEmpty()) {
            Logger.info("Trying to parse custom loadout");
            try {
                parsedRangeLoadout = WebLoadoutLoader.parseEquipment(tree.getSettings().customRangeLoadout);
            } catch (Exception e) {
                Logger.info(e);
                e.printStackTrace();
            }
        }
        Logger.info("Parsed " + parsedRangeLoadout);

        EquipmentLoadout parsedMeleeLoadout = null;
        if (tree.getSettings().customMeleeLoadout != null && !tree.getSettings().customMeleeLoadout.isEmpty()) {
            Logger.info("Trying to parse custom loadout");
            try {
                parsedMeleeLoadout = WebLoadoutLoader.parseEquipment(tree.getSettings().customMeleeLoadout);
            } catch (Exception e) {
                Logger.info(e);
                e.printStackTrace();
            }
        }

        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),

                new PrayerBranch(() -> Skill.PRAYER.getLevel() < tree.getSettings().prayerTarget)
                        .setSimpleName("Prayer training"),

                new ChildrenOfTheSun().setSimpleName("COS"),
                // kill gemstone
                new GetRockCake(() -> tree.getSettings().meleeLoadout == GemstoneCrabMeleeLoadout.DHAROKS && GemstoneCrabMeleeLoadout.unlockedDharoks()),
                GemstoneCrab.getMelee(new ImmutableMap.Builder<Skill, Integer>()
                                .put(Skill.ATTACK, tree.getSettings().attackTarget)
                                .put(Skill.DEFENCE, tree.getSettings().defenceTarget)
                                .put(Skill.STRENGTH, tree.getSettings().strengthTarget)
                                .build()
                        )
                        .setEquipmentLoadout(parsedMeleeLoadout != null ? parsedMeleeLoadout : tree.getSettings().meleeLoadout.equipmentLoadout)
                        .setInventoryLoadout(tree.getSettings().meleeLoadout.inventoryLoadout)
                        .setSimpleName("Melee"),

                // range
                new Fractal(() -> tree.getSettings().rangeTarget > 30
                        && Skills.getRealLevel(Skill.RANGED) >= 30
                        && !PaidQuest.ANIMAL_MAGNETISM.isFinished())
                        .addChildren(
                                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < 30
                                        || Skills.getRealLevel(Skill.HITPOINTS) < 10)
                                        .setSimpleName("Melee training for ava quests"),

                                new XMarksTheSpot().setSimpleName("x marks"), // not certain what this is for but not going to risk changing rn
                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                new RestlessGhost().setSimpleName("Restless ghost"),
                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                new PriestInPeril().setSimpleName("Priest in peril"),
                                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                        )
                        .setSimpleName("Get avas"),
                new GetMoreAvas().setSimpleName("Get more avas"),
                GemstoneCrab.getRange(tree.getSettings().rangeTarget, tree.getSettings().rangeDefenceTarget)
                        .setEquipmentLoadout(parsedRangeLoadout != null ? parsedRangeLoadout : tree.getSettings().rangeLoadout.equipmentLoadout)
                        .setInventoryLoadout(tree.getSettings().rangeLoadout.inventoryLoadout)
                        .setSimpleName("Range"),

                // magic
                GemstoneCrab.getMagic(tree.getSettings().magicTarget, tree.getSettings().magicDefenceTarget)
                        .setEquipmentLoadout(parsedMagicLoadout != null ? parsedMagicLoadout : tree.getSettings().magicLoadout.equipmentLoadout)
                        .setInventoryLoadout(tree.getSettings().magicLoadout.inventoryLoadout)
                        .setSimpleName("Magic"),

                new StopScriptFractal(),
                new MuleOff().setSimpleName("Mule off") // here so it inits the muling settings needed for reverse muling
        );
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();

        if (ClientSettings.isLevelUpInterfaceEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable level up message");
            ClientSettings.toggleLevelUpInterface(false);
            return ReactionGenerator.getNormal();
        }

        if (!Client.isLoggedIn()) return ReactionGenerator.getNormal();
        if (Client.getGameStateID() == 45) return ReactionGenerator.getNormal(); // 45 is loading
        return tree.run();
    }

    Timer runtime = new Timer();
    public static int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

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
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
        };
    }

    @Override
    public String getScriptName() {
        return "cCGemstoneCrab";
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


    public void onInventoryItemAdded(Item item) {
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
    }

    public static boolean reachedBase(int base, Skill... skills) {
        return Arrays.stream(skills).allMatch(x -> x.getLevel() >= base);
    }
}
