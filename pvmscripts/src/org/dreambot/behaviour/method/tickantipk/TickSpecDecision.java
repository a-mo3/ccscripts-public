package org.dreambot.behaviour.method.tickantipk;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Switch to available spec weapon and spec
 */
public class TickSpecDecision extends TickDecision {
    final Supplier<Player> topEnemy;
    // itemid, spec % cost
    Map<Integer, Integer> specCostMap = new HashMap<>();
    public TickSpecDecision(Supplier<Player> topEnemy1) {
        this.topEnemy = topEnemy1;
        setSimpleName("Spec decision");

        specCostMap.put(ItemID.GRANITE_MAUL, 60);
        specCostMap.put(ItemID.DRAGON_MACE, 25);
    }


    @Override
    public boolean evaluate() {
        if (Combat.getSpecialPercentage() < 25) return false;
        Item specWeapon = Inventory.get(x -> specCostMap.containsKey(x.getId()));
        if (specWeapon == null) {
            specWeapon = Equipment.get(x -> specCostMap.containsKey(x.getId()));
            if (specWeapon == null) return false;
        }

        if (specCostMap.get(specWeapon.getId()) < Combat.getSpecialPercentage()) {
            log("Doesn't have enough to spec with " + specWeapon.getName() + " only has " + Combat.getSpecialPercentage());
            return false;
        } else {
            log("Spec");
            if (Inventory.contains(specWeapon)) {
                log("Equip spec weapon");
                Inventory.interact(specWeapon);
            }
            Combat.toggleSpecialAttack(true);
            // return true so we dont go to the usual attack decision, which would switch back to the normal weapon
            return true;
        }
    }
}
