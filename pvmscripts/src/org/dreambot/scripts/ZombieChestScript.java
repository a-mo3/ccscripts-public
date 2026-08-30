package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.ZombieChestSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class ZombieChestScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<ZombieChestSettings> tree = new FractalRoot<>(new ZombieChestSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    final Area ZombieChest = new Area(1285, 10204, 1255, 10176);

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        Logger.info("Init");

        AbstractWebNode webNode0 = new BasicWebNode(3292, 3519, 0);
        AbstractWebNode webNode1 = new BasicWebNode(3302, 3525, 0);
        AbstractWebNode webNode2 = new BasicWebNode(3314, 3529, 0);
        AbstractWebNode webNode3 = new BasicWebNode(3325, 3532, 0);
        AbstractWebNode webNode4 = new BasicWebNode(3333, 3538, 0);
        AbstractWebNode webNode5 = new BasicWebNode(3339, 3553, 0);
        AbstractWebNode webNode6 = new BasicWebNode(3342, 3571, 0);
        AbstractWebNode webNode7 = new BasicWebNode(3347, 3577, 0);
        AbstractWebNode webNode8 = new BasicWebNode(3356, 3591, 0);
        AbstractWebNode webNode9 = new BasicWebNode(3360, 3596, 0);
        AbstractWebNode webNode10 = new BasicWebNode(3365, 3602, 0);
        AbstractWebNode webNode11 = new BasicWebNode(3367, 3609, 0);
        AbstractWebNode webNode12 = new BasicWebNode(3366, 3615, 0);
        AbstractWebNode webNode13 = new BasicWebNode(3366, 3625, 0);
        AbstractWebNode webNode14 = new BasicWebNode(3371, 3624, 0);
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

        AbstractWebNode[] webNodes = {webNode0, webNode1, webNode2, webNode3, webNode4, webNode5, webNode6, webNode7, webNode8, webNode9, webNode10, webNode11, webNode12, webNode13, webNode14,};
        WebFinder.getWebFinder().addWebNodes(webNodes);

        Area CHEST_AREA = new Area(3369, 3628, 3375, 3622);
        SlayerTaskMap.minLootValue = tree.getSettings().minLootValue;
        tree.setSimpleName("cCZombieChest");
        tree.addChildren(
                new GetMembershipBranch().setSimpleName("Get Membership"),

                new Fractal(() -> CombatUtil.get().isTeleblocked())
                        .setSimpleName("Remove TB")
                        .setPrependLogic(() -> {
                            WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.getMinimumLevel() < Skills.getTotalLevel() && x.isMembers() && x.isNormal() && x.getWorld() != 401));
                            return true;
                        }),

                // go open chest
                new TalkToFractal(() -> true, CHEST_AREA, () -> GameObjects.closest("Zombie Pirate's Locker"))
                        .setInteraction("Open")
                        .setLoadoutCondition(() -> !Combat.isInWild() || CombatUtil.getThreat() != null || !Inventory.contains(ItemID.ZOMBIE_PIRATE_KEY))
                        .setInventoryLoadout(new InventoryLoadout() // this will handle the tp out
                                .addItem(ItemID.ZOMBIE_PIRATE_KEY, tree.getSettings().keysPerTrip)
                                .setBuyPrice(tree.getSettings().keyBuyPrice)
                                .setRefill(tree.getSettings().keysPerTrip * tree.getSettings().restockMultiplier)
                                .addItem(ItemID.VARROCK_TELEPORT, 3)
                                .setRefill(100)
                                .setStrict(true))
                        .setSimpleName("Open lockers")
        );
//        new AIAntiban();
    }

    boolean wasInWild;

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        // todo hop world after every time being in wild
        if (Combat.isInWild()) {
            wasInWild = true;
        } else {
            if (wasInWild) {
                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.getMinimumLevel() < Skills.getTotalLevel() && x.isMembers() && x.isNormal() && x.getWorld() != 401));
                wasInWild = false;
            }
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
        return "cCZombieChestScript";
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
        if (!ZombieChest.contains(Players.getLocal())) return;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (!ZombieChest.contains(Players.getLocal())) return;
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
        if (!ZombieChest.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }
}
