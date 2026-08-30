package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.antelope.SunlightAntelopes;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.fletching.FletchBranch;
import org.dreambot.behaviour.training.hunter.EnsureLeftFalconry;
import org.dreambot.behaviour.training.hunter.HunterBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
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
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.MoonlightAntelopeSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class SunlightAntelopeScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<MoonlightAntelopeSettings> tree = new FractalRoot<>(new MoonlightAntelopeSettings(), getScriptName());
    final Area SUNLIGHT_ANTELOPES = new Area(1730, 3021, 1763, 2995);

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

        MuleOff.LOOT = new int[]{
                ItemID.SUNFIRE_SPLINTERS,
                ItemID.SUNLIGHT_ANTELOPE_ANTLER,
                ItemID.SUNLIGHT_ANTLER_BOLTS,
                ItemID.WILLOW_LOGS,
                ItemID.WILLOW_LONGBOW_U,
                ItemID.WILLOW_SHORTBOW_U,

                ItemID.OAK_LOGS,
                ItemID.OAK_LONGBOW_U,
                ItemID.OAK_SHORTBOW_U,

                ItemID.MAPLE_LOGS,
                ItemID.MAPLE_LONGBOW_U,

                ItemID.RING_OF_WEALTH,
                ItemID.AMULET_OF_GLORY_UNCHARGED
        };

        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.SUNFIRE_SPLINTERS,
                ItemID.SUNLIGHT_ANTELOPE_ANTLER,
                ItemID.SUNLIGHT_ANTLER_BOLTS,
        };

        tree.setSimpleName("cCSunfireAntelopess");
        tree.addChildren(
                new EmptyDeathsCoffer().setSimpleName("Empty death"),
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.HITPOINTS) < tree.getSettings().hitpointsTarget
                        || Skills.getRealLevel(Skill.DEFENCE) < tree.getSettings().defenceTarget)
                        .setSimpleName("Getting HP @ Sandcrabs"),

                new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 72)
                        .setSimpleName("Hunter to 72"),
                new EnsureLeftFalconry().setSimpleName("Leave falconry"),

                new AgilityBranch(() -> Skills.getRealLevel(Skill.AGILITY) < tree.getSettings().agilityTarget)
                        .setSimpleName("Agility"),

                new FletchBranch(() -> tree.getSettings().fletchBolts
                        && Skills.getRealLevel(Skill.FLETCHING) < 62)
                        .setSimpleName("Fletching till 62"),

                new ChildrenOfTheSun().setSimpleName("Children of the sun quest"),

                new TalkToFractal(() -> PlayerSettings.getBitValue(9652) < 3, new Tile(3280, 3412), () -> NPCs.closest("Regulus Cento"))
                        .setDialogueOptions("Let's do it!")
                        .setSimpleName("First time valamore"),

                new MuleOff()
                        .setSimpleName("Mule off"),

                new SunlightAntelopes(() -> true)
        );
//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (!Client.isLoggedIn()) {
            Logger.info("Not logged in.");
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

    Tile trapTile = new Tile(1738, 3000);

    @Override
    public void onScriptPaint(Graphics g) {
    }

    @Override
    public String getScriptName() {
        return "cCS-AntelopesFarm";
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


    List<Integer> allowed = Arrays.asList(ItemID.SUNLIGHT_ANTLER_BOLTS, ItemID.SUNFIRE_SPLINTERS);

    public void onInventoryItemAdded(Item item) {
        if (!allowed.contains(item.getId())) return;
        if (!SUNLIGHT_ANTELOPES.contains(Players.getLocal())) return;
        Logger.info("item added");
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!allowed.contains(incoming.getId())) return;
        if (!SUNLIGHT_ANTELOPES.contains(Players.getLocal())) return;
        Logger.info("item changed");
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (!allowed.contains(incoming.getId())) return;
        if (!SUNLIGHT_ANTELOPES.contains(Players.getLocal())) return;
        Logger.info("item swapped");
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }
}
