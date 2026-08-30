package org.dreambot.behaviour.method.gwd.zammy.range;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.gwd.zammy.ZammyCounters;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

import java.util.*;

public class TickZammyLoot extends TickDecision {
    public TickZammyLoot() {
        setSimpleName("Zam Loot");
    }

    Set<Integer> alchables = new HashSet<>(Arrays.asList(
            ItemID.RUNE_FULL_HELM,
            ItemID.ADAMANT_PLATEBODY,
            ItemID.RUNE_MED_HELM,
            ItemID.RUNE_SQ_SHIELD,
            ItemID.RUNE_CHAINBODY,
            ItemID.RUNE_BATTLEAXE,
            ItemID.RUNE_KITESHIELD,
            ItemID.RUNE_PLATESKIRT
    ));

    List<Integer> food = Arrays.asList(
            ItemID.MONKFISH,
            ItemID.SUMMER_PIE,
            ItemID.MANTA_RAY,
            ItemID.HALF_A_SUMMER_PIE
    );

    List<Integer> ignoredItems = Arrays.asList(
            ItemID.MAGIC_POTION3,
            ItemID.SUPER_DEFENCE3,
            ItemID.CHAOS_TALISMAN
    );

    final Area ZAMMY_ROOM = new Area(2917, 5332, 2937, 5317, 2);

    @Override
    public boolean evaluate() {
        NPC zam = NPCs.closest(ZammyCounters.ZAMMY_NAME);
        if (zam != null) return false;
        // set rotation index to 0
        TickRangeZammyBranch.rotationIndex = 0;

        GroundItem expensiveLoot = GroundItems.closest(x -> x.getItem().getLivePrice() > 50_000 && ZAMMY_ROOM.contains(x));
        if (expensiveLoot != null) {
            log("Expensive loot found " + expensiveLoot.getName());
            if (Inventory.isFull()) {
                log("Drop cheapest item in inv, inv is full");
                PVMUtil.dropCheapest();
            }
            log("expensive loot " + expensiveLoot);
            expensiveLoot.interact("Take");
            return true;
        }

        Item alchable = Inventory.get(x -> alchables.contains(x.getId()));
        if (Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY) && alchable != null) {
            log("Alch item: " + alchable);
            Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, alchable);
            return true;
        }


        GroundItem normalLoot = GroundItems.all(ZAMMY_ROOM::contains)
                .stream()
                .filter(x -> !ignoredItems.contains(x.getId()))
                // if inventory is full still
                .filter(x -> !Inventory.isFull() || (x.getItem().isStackable() && Inventory.contains(x.getId())))
                // take summer pies and eat them for run
                .filter(x -> food.contains(x.getId()) || (x.getItem().getLivePrice() + 1) * x.getAmount() > 1500)
                .min(Comparator.comparingDouble(Entity::distance))
                .orElse(null);
        if (normalLoot != null) {
            // checks inventory fullness implicitly here
            log("Take normal loot");
            normalLoot.interact("Take");
            return true;
        }

        return false;
    }
}
