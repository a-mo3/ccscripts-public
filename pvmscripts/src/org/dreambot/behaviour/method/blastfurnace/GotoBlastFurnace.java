package org.dreambot.behaviour.method.blastfurnace;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.BlastFurnaceSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;

public class GotoBlastFurnace extends Fractal {
    public GotoBlastFurnace(List<Integer> reqs) {
        super(() -> !BLAST_FURNACE_AREA.contains(Players.getLocal()) || needsToBuy(reqs));
        // only need to buy ores if you dont own any of one of them
        loadoutCondition = () -> needsToBuy(reqs);

        this.inventoryLoadout = new InventoryLoadout();
        for (Integer i : reqs) {
            inventoryLoadout.addItem(i + 1, ItemID.RUNITE_ORE == i ? 800 : 2000);
        }
        if (SettingsRepository.findInstanceOf(new BlastFurnaceSettings()).useStaminas)
            inventoryLoadout.addItem(ItemID.STAMINA_POTION4, 10)
                    .setRefill(50)
                    ;

        WebFinder wf = WebFinder.getWebFinder();

        EntranceWebNode geTrapdoor = new EntranceWebNode(3140, 3504, 0);
        geTrapdoor.setAction("Travel");
        geTrapdoor.setEntityName("Trapdoor");
        geTrapdoor.addDualConnections(wf.getNearest(new Tile(3146, 3507, 0), 5));
        geTrapdoor.addOutgoingConnections(wf.getNearest(new Tile(2909, 10174, 0), 5));

        EntranceWebNode cartToGE = new EntranceWebNode(2923, 10171);
        cartToGE.setAction("Ride");
        cartToGE.setEntityName("Train cart");
        cartToGE.addDualConnections(wf.getNearest(cartToGE));
        cartToGE.addOutgoingConnections(geTrapdoor);

        wf.addWebNode(geTrapdoor);
        wf.addWebNode(cartToGE);

        // nodes towards the bf entrance
        AbstractWebNode webNode0 = new BasicWebNode(2930, 10186, 0);
        AbstractWebNode webNode1 = new BasicWebNode(2931, 10192, 0);
        webNode0.addDualConnections(WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15));
        WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15).addDualConnections(webNode0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);
        webNode1.addDualConnections(WebFinder.getWebFinder().getNearestGlobal(webNode1.getTile(), 15));
        WebFinder.getWebFinder().getNearestGlobal(webNode1.getTile(), 15).addDualConnections(webNode1);

        AbstractWebNode[] webNodes = {webNode0, webNode1,};
        WebFinder.getWebFinder().addWebNodes(webNodes);

        EntranceWebNode bfEntrance = new EntranceWebNode(2930, 10196, 0);
        bfEntrance.setAction("Climb-down");
        bfEntrance.setEntityName("Stairs");
        bfEntrance.addDualConnections(wf.getNearest(bfEntrance));

        EntranceWebNode bfExit = new EntranceWebNode(1939, 4956, 0);
        bfExit.setAction("Climb-up");
        bfExit.setEntityName("Stairs");
        bfExit.addDualConnections(bfEntrance);

        BasicWebNode bfNode = new BasicWebNode(1940, 4959, 0);
        bfNode.addDualConnections(bfExit);

        wf.addWebNode(bfEntrance);
        wf.addWebNode(bfExit);
        wf.addWebNode(bfNode);
    }

    private static boolean needsToBuy(List<Integer> reqs) {
        // might need to do some noted stuff
        return Bank.isCached() && reqs.stream().anyMatch(x -> !OwnedItems.contains(x));
    }

    public static final Area BLAST_FURNACE_AREA = new Area(1934, 4975, 1957, 4955);
    public static final Tile INFRONT_OF_DOOR = new Tile(2930, 10185);
    public static final Area BF_ENTERANCE = new Area(2927, 10197, 2933, 10186);

    @Override
    public int onLoop() {
        if (!BLAST_FURNACE_AREA.contains(Players.getLocal())) {
            if (!BF_ENTERANCE.contains(Players.getLocal())) {
                log("Bypass door exploit");
                // get past door exploit
                if (!INFRONT_OF_DOOR.equals(Players.getLocal().getTile())) {
                    if (Walking.shouldWalk()) Walking.walkExact(INFRONT_OF_DOOR);
                    return ReactionGenerator.getNormal();
                }

                if (ObjectUtil.interact(6977))
                    Sleep.sleepUntil(() -> Players.getLocal().getY() > INFRONT_OF_DOOR.getY(), 600);

                return ReactionGenerator.getNormal();
            }
            log("Walk to blast furnace");
            if (Walking.shouldWalk()) Walking.walk(BLAST_FURNACE_AREA);
            return ReactionGenerator.getNormal();
        }

        log("Bank all.");
        new BankAllInventoryEvent().execute();
        return ReactionGenerator.getNormal();
    }
}
