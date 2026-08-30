package org.dreambot.behaviour.method.ents;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.behaviour.misc.AdvStandardCombat;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

public class Ents extends Fractal {
    public static Queue<Tile> lastAttackedFrom = new LinkedList<>();

    final Area ENTS_AREA = new Area(
            new Tile(3306, 3625, 0),
            new Tile(3305, 3630, 0),
            new Tile(3311, 3632, 0),
            new Tile(3309, 3614, 0),
            new Tile(3310, 3606, 0),
            new Tile(3305, 3589, 0),
            new Tile(3300, 3580, 0),
            new Tile(3291, 3584, 0),
            new Tile(3301, 3617, 0));

    Area CHAOS_ALTAR = new Area(3267, 3590, 3211, 3640);

    public Ents(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
        setSimpleName("Ents");
        WebFinder wf = WebFinder.getWebFinder();

        List<AbstractWebNode> allNodes = wf.getAll();

        allNodes.removeIf(x -> CHAOS_ALTAR.contains(x.getTile()));

        AbstractWebNode webNode0 = new BasicWebNode(3251, 3538, 0);
        AbstractWebNode webNode1 = new BasicWebNode(3258, 3539, 0);
        AbstractWebNode webNode2 = new BasicWebNode(3267, 3541, 0);
        AbstractWebNode webNode3 = new BasicWebNode(3276, 3552, 0);
        AbstractWebNode webNode4 = new BasicWebNode(3281, 3561, 0);
        AbstractWebNode webNode5 = new BasicWebNode(3288, 3576, 0);
        AbstractWebNode webNode6 = new BasicWebNode(3293, 3583, 0);
        AbstractWebNode webNode7 = new BasicWebNode(3298, 3589, 0);
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

        AbstractWebNode[] webNodes = {webNode0, webNode1, webNode2, webNode3, webNode4, webNode5, webNode6, webNode7,};
        WebFinder.getWebFinder().addWebNodes(webNodes);

        // nodes to walk between the end area
        wf.createAndAddNode(new Tile(3267, 3596, 0));
        wf.createAndAddNode(new Tile(3277, 3594, 0));
        wf.createAndAddNode(new Tile(3286, 3582, 0));
        wf.createAndAddNode(new Tile(3296, 3582, 0));
        wf.createAndAddNode(new Tile(3300, 3587, 0));
        wf.createAndAddNode(new Tile(3301, 3595, 0));
        wf.createAndAddNode(new Tile(3307, 3611, 0));
        wf.createAndAddNode(new Tile(3305, 3619, 0));
        wf.createAndAddNode(new Tile(3309, 3624, 0));

        addChildren(
                // todo maintain which ent trunks you own and chop them
                new CutEnts(() -> NPCs.closest(x -> lastAttackedFrom.stream().anyMatch(i -> i.distance(x) < 5)
                        && x.getName().toLowerCase().contains("trunk")))
                        .setSimpleName("Chop ends"),
                new AdvStandardCombat(() -> true, ENTS_AREA, () -> NPCs.closest("Ent"))
                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
                        .setFlickTiming(1700)
                        .setPrependLogic(() -> {
                            if (Dialogues.inDialogue()) {
                                Dialog.solve("ask again");
                            }

                            if (lastAttackedFrom.size() > 3) lastAttackedFrom.poll();
                            Character target = Players.getLocal().getInteractingCharacter();
                            if (target != null && target.getName().equals("Ent")) {
                                if (!lastAttackedFrom.contains(target.getTile())) {
                                    lastAttackedFrom.add(target.getTile());
                                }
                            }
                            return false;
                        })
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.RUNE_AXE)
                                .setStrictSupplier(() -> !Combat.isInWild())
                                .addItem(ItemID.SHARK, 1, 13)
                                .setStrictSupplier(() -> !Combat.isInWild())
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                        .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
//                                .addItem(EquipmentSlot.ARROWS, ItemID.RUNITE_BOLTS)
                        )
                        .setSimpleName("Kill an ent")
        );
    }
}
