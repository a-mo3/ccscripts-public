package org.dreambot.behaviour.method.gwd.zilyana.tickkillcount;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.zilyana.ZilyanaConsts;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

public class ZilyanaKCAttack extends TickDecision {
    @Override
    public boolean evaluate() {
        // might want own decision for this
        if (Inventory.contains(x -> ZilyanaConsts.secondaryWeapons.contains(x.getId()))) {
            if (Inventory.isFull()) {
                log("Drop steel arrow");
                Inventory.dropAll(ItemID.STEEL_ARROW);
            }
            if (Inventory.isFull() && Inventory.contains(ItemID.TOXIC_BLOWPIPE)) {
                log("Drop cheapest for BP equip");
                PVMUtil.dropCheapest();
            }
            log("Equip secondary");
            Inventory.interact(x -> ZilyanaConsts.secondaryWeapons.contains(x.getId()));
        }

        // atk another priest
        if (!Players.getLocal().isInCombat()) {
            NPC priest = NPCs.closest("Saradomin priest");
            if (priest != null) {
                priest.interact("Attack");
            }
        }
        return false;
    }
}
