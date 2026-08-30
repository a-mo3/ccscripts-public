package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.data.ActionMode;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.rogueschest.OpenRogueChests;
import org.dreambot.behaviour.method.rogueschest.RogueChestAntiPK;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.witchshouse.WitchsHouse;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.training.thieving.ThievingBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.behaviour.wilddiary.EasyWildernessDiary;
import org.dreambot.behaviour.wilddiary.HardWildernessDiary;
import org.dreambot.behaviour.wilddiary.MediumWildernessDiary;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.discordwebhook.scouter.ScoutFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.RoguesChestSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class RoguesChestScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<RoguesChestSettings> tree = new FractalRoot<>(new RoguesChestSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    final Area ROGUES_CHEST_LOOT_AREA = new Area(3271, 3950, 3302, 3931);

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        // the nodes below rogues castle near the chaos ele, we want to walk on the north side across the water
        Area LOWER_ROGUES_CASTLE_NODES = new Area(3323, 3924, 3226, 3904);
        WebFinder wf = WebFinder.getWebFinder();
        List<AbstractWebNode> dragonNodes = wf.getAll().stream().filter(x -> LOWER_ROGUES_CASTLE_NODES.contains(x.getTile())).collect(Collectors.toList());
        dragonNodes.forEach(wf::removeNode);

        Area wildernesChaosTempleNodes = new Area(3218, 3637, 3259, 3586);
        List<AbstractWebNode> badNodes = wf.getAll().stream().filter(x -> wildernesChaosTempleNodes.contains(x.getTile())).collect(Collectors.toList());
        badNodes.forEach(wf::removeNode);

        AbstractWebNode webNode0 = new BasicWebNode(3198, 3950, 0);
        AbstractWebNode webNode1 = new BasicWebNode(3202, 3954, 0);
        AbstractWebNode webNode2 = new BasicWebNode(3206, 3961, 0);
        AbstractWebNode webNode3 = new BasicWebNode(3212, 3961, 0);
        AbstractWebNode webNode4 = new BasicWebNode(3221, 3961, 0);
        AbstractWebNode webNode5 = new BasicWebNode(3231, 3961, 0);
        AbstractWebNode webNode6 = new BasicWebNode(3239, 3961, 0);
        AbstractWebNode webNode7 = new BasicWebNode(3248, 3962, 0);
        AbstractWebNode webNode8 = new BasicWebNode(3258, 3962, 0);
        AbstractWebNode webNode9 = new BasicWebNode(3269, 3961, 0);
        AbstractWebNode webNode10 = new BasicWebNode(3277, 3960, 0);
        AbstractWebNode webNode11 = new BasicWebNode(3288, 3954, 0);
        AbstractWebNode webNode12 = new BasicWebNode(3295, 3949, 0);
        AbstractWebNode webNode13 = new BasicWebNode(3299, 3944, 0);
        AbstractWebNode webNode14 = new BasicWebNode(3300, 3937, 0);
        AbstractWebNode webNode15 = new BasicWebNode(3300, 3930, 0);
        AbstractWebNode webNode16 = new BasicWebNode(3296, 3918, 0);
        AbstractWebNode webNode17 = new BasicWebNode(3289, 3918, 0);
        AbstractWebNode webNode18 = new BasicWebNode(3286, 3922, 0);
        AbstractWebNode webNode19 = new BasicWebNode(3293, 3926, 0);
        AbstractWebNode webNode20 = new BasicWebNode(3295, 3930, 0);
        AbstractWebNode webNode21 = new BasicWebNode(3296, 3937, 0);
        AbstractWebNode webNode22 = new BasicWebNode(3290, 3940, 0);
        webNode0.addDualConnections(WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15));
        WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15).addDualConnections(webNode0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);
        webNode1.addDualConnections(webNode2);
        webNode2.addDualConnections(webNode1);
        webNode2.addDualConnections(webNode3);
        webNode3.addDualConnections(webNode2);
        webNode3.addDualConnections(webNode4);
        webNode4.addDualConnections(webNode3);
        webNode4.addDualConnections(webNode5);
        webNode5.addDualConnections(webNode4);
        webNode5.addDualConnections(webNode6);
        webNode6.addDualConnections(webNode5);
        webNode6.addDualConnections(webNode7);
        webNode7.addDualConnections(webNode6);
        webNode7.addDualConnections(webNode8);
        webNode8.addDualConnections(webNode7);
        webNode8.addDualConnections(webNode9);
        webNode9.addDualConnections(webNode8);
        webNode9.addDualConnections(webNode10);
        webNode10.addDualConnections(webNode9);
        webNode10.addDualConnections(webNode11);
        webNode11.addDualConnections(webNode10);
        webNode11.addDualConnections(webNode12);
        webNode12.addDualConnections(webNode11);
        webNode12.addDualConnections(webNode13);
        webNode13.addDualConnections(webNode12);
        webNode13.addDualConnections(webNode14);
        webNode14.addDualConnections(webNode13);
        webNode14.addDualConnections(webNode15);
        webNode15.addDualConnections(webNode14);
        webNode15.addDualConnections(webNode16);
        webNode16.addDualConnections(webNode15);
        webNode16.addDualConnections(webNode17);
        webNode17.addDualConnections(webNode16);
        webNode17.addDualConnections(webNode18);
        webNode18.addDualConnections(webNode17);
        webNode18.addDualConnections(webNode19);
        webNode19.addDualConnections(webNode18);
        webNode19.addDualConnections(webNode20);
        webNode20.addDualConnections(webNode19);
        webNode20.addDualConnections(webNode21);
        webNode21.addDualConnections(webNode20);
        webNode21.addDualConnections(webNode22);
        webNode22.addDualConnections(webNode21);

        AbstractWebNode[] webNodes = {webNode0, webNode1, webNode2, webNode3, webNode4, webNode5, webNode6, webNode7, webNode8, webNode9, webNode10, webNode11, webNode12, webNode13, webNode14, webNode15, webNode16, webNode17, webNode18, webNode19, webNode20, webNode21, webNode22,};
        WebFinder.getWebFinder().addWebNodes(webNodes);
        Logger.info("Init");

        Area CHEST_AREA = new Area(
                new Tile(3282, 3946, 0),
                new Tile(3282, 3942, 0),
                new Tile(3287, 3942, 0),
                new Tile(3287, 3938, 0),
                new Tile(3290, 3936, 0),
                new Tile(3291, 3947, 0),
                new Tile(3282, 3947, 0));

        Area chaosEly = new Area(
                new Tile(3259, 3947, 0),
                new Tile(3281, 3947, 0),
                new Tile(3275, 3940, 0),
                new Tile(3275, 3926, 0),
                new Tile(3279, 3922, 0),
                new Tile(3280, 3904, 0),
                new Tile(3238, 3904, 0));
        Arrays.stream(chaosEly.getTiles()).forEach(
                t -> LocalPathFinder.getLocalPathFinder().addBlacklistedTile(t)
        );

        SlayerTaskMap.minLootValue = tree.getSettings().minLootValue;
        tree.setSimpleName("cCRoguesChest");
        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ScoutFractal(),
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("Lamp handler"),
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43)
                        .setSimpleName("Prayer 43"),
                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Get off 330"),

                new Fractal(() -> !PaidQuest.WITCHS_HOUSE.isFinished() && (tree.getSettings().alwaysWitchesHouse || Skills.getRealLevel(Skill.HITPOINTS) < 20))
                        .addChildren(
                                new WitchsHouse().setSimpleName("Witchs house")
                        ).setSimpleName("Witches house"),

                new EasyWildernessDiary().setSimpleName("Easy"),
                new MediumWildernessDiary().setSimpleName("Medium"),
                new HardWildernessDiary(() -> tree.getSettings().doHardWildernessDiary).setSimpleName("Hard diary"),

                new ThievingBranch(() -> Skills.getRealLevel(Skill.THIEVING) < 84)
                        .setSimpleName("84 thief'in"),

                new MuleOff()
                        .setSimpleName("Mule Off"),

                new RogueChestAntiPK(tree.getSettings()).setSimpleName("Rogues AntiPk"),
                new OpenRogueChests(() -> true)
        );
//        new AIAntiban();
    }

    boolean wasInWild;

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        // todo hop world after every time being in wild
//        if (Combat.isInWild()) {
//            wasInWild = true;
//        } else {
//            if (wasInWild) {
//                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.getMinimumLevel() < Skills.getTotalLevel() && x.isMembers() && x.isNormal() && x.getWorld() != 401));
//            }
//        }

        if (ClientSettings.areItemPilesOnDeathEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable death piles");
            ClientSettings.toggleItemPilesOnDeath(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isWildernessLeversWarningEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Turn off wilderness lever");
            ClientSettings.toggleWildernessLeversWarning(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.getNPCAttackOptionsMode() == ActionMode.HIDDEN) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Set depends on combat levels npc attack mode");
            ClientSettings.setNPCAttackOptionsMode(ActionMode.DEPENDS_ON_COMBAT_LEVELS);
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
        return "cCRoguesChest";
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
        Logger.info("item added");
        if (!ROGUES_CHEST_LOOT_AREA.contains(Players.getLocal())) return;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (!ROGUES_CHEST_LOOT_AREA.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        Logger.info("item swapped");
        if (!ROGUES_CHEST_LOOT_AREA.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }
}
