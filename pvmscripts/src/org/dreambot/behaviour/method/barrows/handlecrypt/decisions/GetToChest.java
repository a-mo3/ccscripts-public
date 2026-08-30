package org.dreambot.behaviour.method.barrows.handlecrypt.decisions;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.method.barrows.BarrowsBrother;
import org.dreambot.behaviour.method.barrows.handlecrypt.HandleCryptBranch;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.scripts.BarrowsScript;

import java.util.List;
import java.util.stream.Collectors;

public class GetToChest extends TickDecision {
    public static final Area CHEST_AREA = new Area(3546, 9699, 3556, 9689);
    public static final int CHEST_ID = 20973;

    public GetToChest() {
        setSimpleName("Get to chest");

        // web walker issue from now checking obstacles throughout an entire path seem to be fixed by just removing all nodes
        WebFinder wf = WebFinder.getWebFinder();
        List<AbstractWebNode> dragonNodes = wf.getAll().stream().filter(x -> HandleCryptBranch.BARROWS_CRYPT.contains(x.getTile())).collect(Collectors.toList());
        dragonNodes.forEach(wf::removeNode);
    }

    Tile lastOn = null;
    Timer walkerStuckFailsafe = new Timer(6_000);
    @Override
    public boolean evaluate() {
        if (Inventory.contains(ItemID.VIAL)) {
            log("Drop vials");
            Inventory.dropAll(ItemID.VIAL);
        }

        if (!CHEST_AREA.contains(Players.getLocal())) {
            if (lastOn == null) {
                lastOn = Players.getLocal().getTile();
                walkerStuckFailsafe.reset();
            }
            if (!lastOn.equals(Players.getLocal().getTile())) {
                lastOn = Players.getLocal().getTile();
                walkerStuckFailsafe.reset();
            }
            if (walkerStuckFailsafe.finished()) {
                log("On same tile to long do a little shuffle");
                Walking.walk(lastOn.getArea(2).getRandomTile());
                return true;
            }

            log("Walk to chest area");
//            BarrowsNodeManager.manage();
            Walking.setDisableMinimap(true);
            if (Walking.shouldWalk()) Walking.walk(CHEST_AREA);
            Walking.setDisableMinimap(false);
            return true;
        }

        GameObject chest = GameObjects.closest(CHEST_ID);
        if (chest == null) {
            log("Failed to find chest object");
            return false;
        }

        chest.interact();
        if (BarrowsBrother.killedBrothersCount() < 6) {
            Sleep.sleepUntil(() -> HintArrow.getPointed() != null, 3400);
        } else {
            Sleep.sleepUntil(Widgets::isOpen, 3400);
        }

        WidgetChild rewards = Widgets.get(155, 3);
        if (rewards == null) {
            log("Reward not found");
            return false;
        }

        for (WidgetChild reward : rewards.getChildren()) {
            if (reward == null) continue;
            log("Reward " + reward.getItemId() + "*" + reward.getItemStack());
            BarrowsScript.grossGp += LivePrices.get(reward.getItemId()) * reward.getItemStack();
        }

        return false;
    }
}
