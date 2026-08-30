package org.dreambot.behaviour.method.gwd.bandos.tickbandosfight;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.bandos.BandosConsts;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

/**
 * Kill 3 guards
 */
public class BandosGuards extends TickDecision {
    @Override
    public boolean evaluate() {
//        if (KillBandosPrayerDecision.growlerTickTiming != KillBandosPrayerDecision.breeTickTiming) return false;
        NPC bandos = NPCs.closest(BandosConsts.BANDOS);
        if (bandos != null) {
            log("Bandos not null");
            return false;
        }

        if (Inventory.contains(x -> BandosConsts.secondaryWeapons.contains(x.getId()))) {
            if (Inventory.isFull()) {
                log("Drop steel arrow");
                Inventory.dropAll(ItemID.STEEL_ARROW);
            }
            if (Inventory.isFull() && Inventory.contains(ItemID.TOXIC_BLOWPIPE)) {
                log("Drop cheapest for BP equip");
                PVMUtil.dropCheapest();
            }
            log("Equip secondary");
            Inventory.interact(x -> BandosConsts.secondaryWeapons.contains(x.getId()));
            return true;
        }

        if (Combat.getSpecialPercentage() >= 50 && Equipment.contains(ItemID.TOXIC_BLOWPIPE))
            Combat.toggleSpecialAttack(true);


        // kill melee first we cant flick against him
        NPC guard = getGuard();
        if (guard == null) {
            log("No guards alive");
            return false;
        }

        if (!guard.equals(Players.getLocal().getInteractingCharacter())) {
            log("Interact with guard");
            guard.interact("Attack");
        }
        return true;
    }

    private NPC getGuard() {
        NPC melee = NPCs.closest(BandosConsts.MELEE_MINION_NAME);
        if (melee != null) return melee;
        NPC magic = NPCs.closest(BandosConsts.MAGIC_MINION_NAME);
        if (magic != null) return magic;
        return NPCs.closest(BandosConsts.RANGE_MINION_NAME);

    }
}
