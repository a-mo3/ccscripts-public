package org.dreambot.behaviour.method.lms.deathdot;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.lms.LMSBranch;
import org.dreambot.behaviour.method.lms.LMSEquipmentItemData;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;
import java.util.List;

public class LMSSwitchDecision extends TickDecision {

    /**
     * todo lms best switches change as the game goes on, we need to solve having subpar items
     * either by dropping or selecting the best
     */
    List<Integer> mageItems = Arrays.asList(
            LMSEquipmentItemData.MYSTIC_ROBE_TOP.getItemId(),
            LMSEquipmentItemData.MYSTIC_ROBE_BOTTOM.getItemId(),
            LMSEquipmentItemData.AHRIMS_STAFF.getItemId(),
            LMSEquipmentItemData.ANCIENT_STAFF.getItemId(),
            LMSEquipmentItemData.SPIRIT_SHIELD.getItemId(),
            LMSEquipmentItemData.OCCULT_NECKLACE.getItemId(),
            LMSEquipmentItemData.IMBUED_GUTHIX_CAPE.getItemId()
    );

    List<Integer> rangeItems = Arrays.asList(
            LMSEquipmentItemData.BLACK_DHIDE_BODY.getItemId(),
            LMSEquipmentItemData.BLACK_DHIDE_CHAPS.getItemId(),
            LMSEquipmentItemData.RUNE_CROSSBOW.getItemId(),
            LMSEquipmentItemData.AMULET_OF_GLORY.getItemId()
    );


    @Override
    public boolean evaluate() {
        if (LMSCounter.actionCounter > 1) return false;
        // switch then attack enemy
        // we could have one
        log("Switch");

        // enemy having correct overhead prayer reduces damage 30%

        for (Integer rangeItem : rangeItems) {
            if (Inventory.contains(rangeItem)) {
                log("Range switch " + rangeItem);
                Inventory.interact(rangeItem);
            }
        }

        // attack enemy
        Player opp = LMSBranch.getEnemy();
        if (opp != null) {
            log("Attack opp");
            opp.interact();
        }
        return false;
    }

}
