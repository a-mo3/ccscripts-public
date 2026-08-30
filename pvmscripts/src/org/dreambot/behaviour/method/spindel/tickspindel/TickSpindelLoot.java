package org.dreambot.behaviour.method.spindel.tickspindel;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TickSpindelLoot extends TickDecision {
    Set<Integer> pickableLoot = new HashSet<>(Arrays.asList(
            ItemID.DRAGON_2H_SWORD,
            ItemID.DRAGON_PICKAXE,
            ItemID.SUPER_RESTORE4,
            ItemID.VOIDWAKER_GEM,
            ItemID.FANGS_OF_VENENATIS,
            ItemID.TREASONOUS_RING,

            ItemID.PRAYER_POTION3,
            ItemID.PRAYER_POTION2,
            ItemID.PRAYER_POTION1,

            ItemID.STAMINA_POTION3,
            ItemID.STAMINA_POTION2,
            ItemID.STAMINA_POTION1,

            ItemID.RANGING_POTION3,
            ItemID.RANGING_POTION2,
            ItemID.RANGING_POTION1,

            ItemID.DARK_CRAB,
            ItemID.SUPER_RESTORE4,
            ItemID.RUNE_PICKAXE,
            ItemID.RUNE_KNIFE,
            ItemID.RUNE_2H_SWORD,
            ItemID.CHAOS_RUNE,
            ItemID.BLOOD_RUNE,
            ItemID.DEATH_RUNE,
            ItemID.DIAMOND_BOLTS_E,
            ItemID.CANNONBALL,
            ItemID.UNCUT_DIAMOND,
            ItemID.GOLD_ORE,
            ItemID.MAGIC_LOGS,
            ItemID.LIMPWURT_ROOT,
            ItemID.ONYX_BOLT_TIPS,
            ItemID.RED_SPIDERS_EGGS,
            ItemID.UNCUT_DRAGONSTONE,
            ItemID.UNCUT_RUBY,
            ItemID.GRIMY_SNAPDRAGON,
            ItemID.UNICORN_HORN,
            ItemID.ANTIDOTE4_5952,
            ItemID.MAGIC_SEED,
            ItemID.PALM_TREE_SEED,
            ItemID.BLIGHTED_SUPER_RESTORE3,
            ItemID.BLIGHTED_SUPER_RESTORE4,
            ItemID.COMBAT_POTION2
    ));

    @Override
    public boolean evaluate() {
        GroundItem expensiveLoot = GroundItems.closest(x -> pickableLoot.contains(x.getId()) || x.getItem().getLivePrice() * x.getAmount() > 50_000);
        if (expensiveLoot != null) {
            log("Expensive loot " + expensiveLoot);
            if (Inventory.isFull()) {
                PVMUtil.dropCheapest();
            }
            expensiveLoot.interact();
            return true;
        }
        return false;
    }
}
