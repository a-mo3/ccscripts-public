package org.dreambot.behaviour.method.gwd.zilyana.tickzilyanafight;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.gwd.zilyana.ZilyanaConsts;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * Kill growler if the guards and attacking on the same tick
 */
public class ZilyanaGuards extends TickDecision {
    @Override
    public boolean evaluate() {
        if (KillZilyanaPrayerDecision.growlerTickTiming != KillZilyanaPrayerDecision.breeTickTiming) return false;
        NPC zil = NPCs.closest("Commander Zilyana");
        NPC starlight = NPCs.closest("Starlight");
        if (zil != null || starlight != null) {
            log("Zil or starlight is still alive but we need to kill growler");
            return false;
        }

        NPC growler = NPCs.closest("Growler");
        if (growler == null) {
            log("No growler");
            return false;
        }

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
            return true;
        }

        if (Combat.getSpecialPercentage() >= 50 && Equipment.contains(ItemID.TOXIC_BLOWPIPE))
            Combat.toggleSpecialAttack(true);

        Character tgt = Players.getLocal().getInteractingCharacter();
        if (!growler.equals(tgt)) {
            log("Attack growler");
            growler.interact("Attack");
            return true;
        }
        return true;
    }
}
