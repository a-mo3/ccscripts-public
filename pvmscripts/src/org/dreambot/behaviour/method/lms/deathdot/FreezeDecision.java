package org.dreambot.behaviour.method.lms.deathdot;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.magic.Ancient;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.lms.LMSBranch;
import org.dreambot.behaviour.method.lms.LMSEquipmentItemData;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;
import java.util.List;

public class FreezeDecision extends TickDecision {

    List<Integer> mageItems = Arrays.asList(
            LMSEquipmentItemData.MYSTIC_ROBE_TOP.getItemId(),
            LMSEquipmentItemData.MYSTIC_ROBE_BOTTOM.getItemId(),
            LMSEquipmentItemData.AHRIMS_STAFF.getItemId(),
            LMSEquipmentItemData.ANCIENT_STAFF.getItemId(),
            LMSEquipmentItemData.SPIRIT_SHIELD.getItemId(),
            LMSEquipmentItemData.OCCULT_NECKLACE.getItemId(),
            LMSEquipmentItemData.IMBUED_GUTHIX_CAPE.getItemId()
    );

    @Override
    public boolean evaluate() {
        if (LMSCounter.actionCounter == 1) {
            for (Integer item : mageItems) {
                if (Inventory.contains(item)) {
                    log("Mage switch " + item);
                    Inventory.interact(item);
                }
            }
            return true;
        }

        if (LMSCounter.actionCounter > 1) return false;
        if (!Magic.canCast(Ancient.ICE_BARRAGE)) {
            log("Cant cast ice barrage");
        }
        // we should freeze when out opponent is not frozen
        Player enemy = LMSBranch.getEnemy();
        if (enemy == null) return false;
        // already frozen
        if (enemy.getRenderableHeight() == 1000) return false;

//        int[] enemyStats = EquipmentItemData.getPlayerStats(enemy);
//        int[] ourStats = EquipmentItemData.getPlayerStats(Players.getLocal());
//        // calculate the chance of landing a freeze
//        int ourUpper = ourStats[3] * (Skill.MAGIC.getBoostedLevel() + 64);
//        // we use our stats for this because im lms every has the same, and we assume they boost like we do
//        int enemyUpper = (int) (enemyStats[8] * (Skill.MAGIC.getBoostedLevel() * .75 + Skill.DEFENCE.getBoostedLevel()  * .25 + 8));
//        log("Us " + ourUpper + " Opp " + enemyUpper);
//        log("Our chance " + ((ourUpper-enemyUpper)/ourUpper));

        log("Freeze opp");
        Magic.castSpellOn(Ancient.ICE_BARRAGE, enemy);
        return false;
    }
}
