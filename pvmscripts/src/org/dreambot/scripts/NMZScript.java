package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.quests.LostCity;
import org.dreambot.behaviour.quests.VampyreSlayer;
import org.dreambot.behaviour.quests.ascentofarceuus.AscentOfArceuus;
import org.dreambot.behaviour.quests.fightarena.FightArena;
import org.dreambot.behaviour.quests.rfd.GetRockCake;
import org.dreambot.behaviour.quests.witchshouse.WitchsHouse;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.nmz.NightmareZone;
import org.dreambot.behaviour.training.nmz.RangeNightmareZone;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.NMZScriptSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionSettingsFractal;
import org.dreambot.webnodes.GWDNodes;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class NMZScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<NMZScriptSettings> tree = new FractalRoot<>(new NMZScriptSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        GWDNodes.init();
        tree.setSimpleName("cCNMZ");
        tree.addChildren(
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Empty grave"),
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43)
                        .setSimpleName("Prayer 43"),
                SandCrabs.getMelee(() -> !reachedBase(60, Skill.DEFENCE, Skill.ATTACK, Skill.STRENGTH)).setSimpleName("Base 60s @ Crabs"),
                new WitchsHouse().setSimpleName("Witchs house"),
                new VampyreSlayer().setSimpleName("NMZ vampire slayer"),
                new FightArena().setSimpleName("NMZ fight arena"),
                new LostCity().setSimpleName("Lost city"),
                new GetRockCake().setSimpleName("Rock cake"),
                new AgilityBranch(() -> Skills.getRealLevel(Skill.AGILITY) < 15).setSimpleName("Agility 15 req"),
                new AscentOfArceuus(),
                SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < Math.min(tree.getSettings().rangeTarget, 60))
                        .setSimpleName("60 base range"),
                new RangeNightmareZone(() -> tree.getSettings().rangeTarget > Skills.getRealLevel(Skill.RANGED),
                        tree.getSettings().rangeEquipment.getLoadout()),
                new NightmareZone(() -> true, tree.getSettings().nmzCustomEquipment.getLoadout())
                        .setAtkMax(tree.getSettings().attackTgt)
                        .setDefMax(tree.getSettings().defenceTgt)
                        .setStrMax(tree.getSettings().strengthTgt)
                        .setSimpleName("Nightmare zone"),
                // not used
                new MuleOff().setSimpleName("<i;e pff")
        );
    }

    private boolean reachedBase(int base, Skill... skills) {
        return Arrays.stream(skills).allMatch(x -> x.getLevel() >= base);
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
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
        return "cCNMZ";
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
}
