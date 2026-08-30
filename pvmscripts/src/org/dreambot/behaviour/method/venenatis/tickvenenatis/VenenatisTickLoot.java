package org.dreambot.behaviour.method.venenatis.tickvenenatis;

import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.method.venenatis.VenenatisData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.scriptdata.CallistoSettings;
import org.dreambot.scriptdata.VenenatisSettings;

import java.util.Comparator;

public class VenenatisTickLoot extends TickDecision {

    public VenenatisTickLoot(VenenatisSettings settings) {
    }

    @Override
    public boolean evaluate() {
        NPC venenatis = NPCs.closest(VenenatisData.VENENATIS_NAME);
        if (venenatis != null) {
            return false;
        }
        TickVenenatisBranch.venenatisAttackStyle = Skill.RANGED;
        TickVenenatisBranch.venenatisAttackCounter = 0;

        GroundItem bestLoot = GroundItems.all()
                .stream()
                .filter(x -> (x.getItem().isStackable() ? (x.getAmount() * (LivePrices.get(x.getId()) + 1)) : x.getItem().getLivePrice()) > 500)
                .max(Comparator.comparingInt(x -> x.getItem().getLivePrice() * x.getAmount()))
                .orElse(null);
        if ((bestLoot == null || (bestLoot.getItem().getLivePrice()+1) * bestLoot.getAmount() < 2500)) {
            log("No loot worth taking " + bestLoot);
            return false;
        }
        return false;
    }
}
