package org.dreambot.behaviour.method.chaosdwarves;

import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.behaviour.CombatLoadouts;
import org.dreambot.behaviour.StandardCombat;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ChaosDwarves extends Fractal {
    Area CHAOS_DWARVES = new Area(3013, 3771, 3032, 3752);
    List<Integer> LOOT = Arrays.asList(
            ItemID.CHAOS_RUNE,
            ItemID.MIND_RUNE,
            ItemID.LAW_RUNE,
            ItemID.AIR_RUNE,
            ItemID.MIND_RUNE,
            ItemID.NATURE_RUNE,
            ItemID.COSMIC_RUNE,
            ItemID.DEATH_RUNE,
            ItemID.WATER_RUNE,

            ItemID.MITHRIL_SQ_SHIELD,
            ItemID.MITHRIL_LONGSWORD,
            ItemID.STEEL_FULL_HELM,
            ItemID.MITHRIL_BAR,
            ItemID.COAL,
            ItemID.MUDDY_KEY
    );

    public ChaosDwarves(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Chaos Dwarves");

        addChildren(
                // todo some kind of f2p anti pk
                new StandardCombat(() -> true, CHAOS_DWARVES, () -> NPCs.closest(x -> x.getName().equals("Chaos dwarf") && !x.isInCombat()))
                        .setFoodID(Arrays.asList(ItemID.LOBSTER))
                        .setLootStrategy(x -> LOOT.contains(x.getID()) || LivePrices.get(x.getID()) > ItemID.LOBSTER,
                                ItemID.LOBSTER)
                        .setInventoryLoadout(new InventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                                // for varrock teleport
                                .addItem(ItemID.AIR_RUNE, 3, 10)
                                .setRefill(100)
                                .addItem(ItemID.FIRE_RUNE, 1, 6)
                                .setRefill(100)
                                .addItem(ItemID.LAW_RUNE, 1, 5)
                                .setRefill(45)
                        )
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P) // todo maybe use a cheaper loadout
                        .setSimpleName("Fight")
        );
    }
}
