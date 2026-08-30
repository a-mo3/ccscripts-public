package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.clues.CheckClueScroll;
import org.dreambot.behaviour.method.clues.ClueScrollBranch;
import org.dreambot.behaviour.method.clues.GetClueBranch;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.LostCity;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
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
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.MediumClueSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;

public class MediumClueScrollScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<MediumClueSettings> tree = new FractalRoot<>(new MediumClueSettings(), getScriptName());

    Area DRAYNOR = new Area(3073, 3256, 3097, 3245);

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCMediumClueScrollFarm");
        MediumClueSettings settings = tree.getSettings();

        MuleOff.LOOT = new int[]{
                ItemID.CHAOS_RUNE
        };

        tree.addChildren(
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),

                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.ATTACK) < 30
                        || Skills.getRealLevel(Skill.DEFENCE) < 40
                        || Skills.getRealLevel(Skill.STRENGTH) < 15
                ).setSimpleName("Clue scroll combat reqs"),

                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 40)
                        .setSimpleName("Clue scroll 40 magic"),

                new ConfigurableRangeTraining(() -> Skills.getRealLevel(Skill.RANGED) < 50)
                        .setSimpleName("Clue scroll 50 range"),

                new AgilityBranch(() -> Skills.getRealLevel(Skill.AGILITY) < 35)
                        .setSimpleName("Clue scroll 35 agility req"),

                new ChildrenOfTheSun().setSimpleName("Children of the sun"),
                new LostCity().setSimpleName("Lost city"),
                new PriestInPeril().setSimpleName("Priest in peril"),
                // todo get hands on a medium clue
                new GetClueBranch(() -> Bank.isCached() && !OwnedItems.contains("Clue scroll (medium)"), tree.getSettings())
                        .setSimpleName("Get clue scroll"),

                // solve a medium clue
                new ClueScrollBranch(() -> true).setSimpleName("Solve clues")
        );
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Current type: " + (CheckClueScroll.lastClue == null ? "-" : CheckClueScroll.lastClue.getType())
        };
    }

    @Override
    public String getScriptName() {
        return "cCMedClueFarm";
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
        if (Widgets.isOpen()) return;
        if (!DRAYNOR.contains(Players.getLocal())) return;
        if (!item.getName().contains("seed")) return;
        grossGp += item.getLivePrice();

    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!DRAYNOR.contains(Players.getLocal())) return;
        if (!existing.getName().contains("seed")) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }
        int gp = quantity * existing.getLivePrice();
        grossGp += gp;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (!DRAYNOR.contains(Players.getLocal())) return;
    }
}
