package org.dreambot.behaviour.method.gwd.zammy.range;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.zammy.ZammyCounters;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

public class TickZammyKillGuards extends TickDecision {
    final String MELEE_GUARD_NAME = "Tstanon Karlak";
    final String RANGE_GUARD_NAME = "Zakl'n Gritch";
    final String MAGIC_GUARD_NAME = "Balfrug Kreeyath";

    @Override
    public boolean evaluate() {
        NPC zam = NPCs.closest(ZammyCounters.ZAMMY_NAME);
        if (zam != null) return false;
        // always kill guards because they can drop spear and sword shards

        // switch to blowpipe if you have it
        if (Inventory.contains(ItemID.TOXIC_BLOWPIPE)) {
            log("Switch to blowpipe");
            if (Inventory.getEmptySlots() == 0) {
                log("Drop item to make space");
                PVMUtil.dropCheapest();
                return false;
            }
            Inventory.interact(ItemID.TOXIC_BLOWPIPE);
            return true;
        }

        NPC guard = NPCs.closest(MELEE_GUARD_NAME);
        if (guard != null) {
            if (!guard.equals(Players.getLocal().getInteractingCharacter())) {
                log("Attack melee");
                guard.interact("Attack");
                return true;
            }
        }

        guard = NPCs.closest(RANGE_GUARD_NAME);
        if (guard != null) {
            if (!guard.equals(Players.getLocal().getInteractingCharacter())) {
                log("Attack range");
                guard.interact("Attack");
                return true;
            }
        }

        guard = NPCs.closest(MAGIC_GUARD_NAME);
        if (guard != null) {
            if (!guard.equals(Players.getLocal().getInteractingCharacter())) {
                log("Attack magic");
                guard.interact("Attack");
                return true;
            }
        }
        return false;
    }
}
