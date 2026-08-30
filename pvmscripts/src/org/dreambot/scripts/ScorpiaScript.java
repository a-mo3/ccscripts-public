package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.scorpia.FightScorpia;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
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
import org.dreambot.scriptdata.ScorpiaSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class ScorpiaScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<ScorpiaSettings> tree = new FractalRoot<>(new ScorpiaSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    Area MONEY_ZONE = new Area(1652, 3784, 1697, 3751);

    @Override
    public void onArgs(String... args) {
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

        tree.setSimpleName("cCScorpias");
        tree.addChildren(
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < tree.getSettings().magicTarget)
                        .setSimpleName("Magic"),

                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget)
                        .setSimpleName("Prayer"),

//                new RechargeTrident().setSimpleName("Recharge trident"),

                new MuleOff()
                        .setSimpleName("Mule Off"),

                new FightScorpia(() -> true).setSimpleName("Fight scorpia")

        );
//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (ClientSettings.areItemPilesOnDeathEnabled()) {
            if (Bank.isOpen()) Bank.close();
            Logger.info("Disabling item piles on death");
            ClientSettings.toggleItemPilesOnDeath(false);
            return ReactionGenerator.getNormal();
        }

        if (!ChangeAlchWarning.setHighAlchWarning(500_000)) {
            Logger.info("Setting alch warning price");
            return ReactionGenerator.getNormal();
        }
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
        return "cCScorpiaFarm";
    }

    @Override
    public int getMoneyMade() {
        return FightScorpia.netGp;
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
