package org.dreambot.scriptdata;

import lombok.Getter;
import org.dreambot.behaviour.training.crafting.GenericSmelting;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;

public enum CraftingModes {
    GOLD_RINGS(new GenericSmelting(5, ItemID.GOLD_RING, ItemID.GOLD_BAR)
            .setSimpleName("Gold rings")
            .setInventoryLoadout(new InventoryLoadout()
                    .addItem(ItemID.RING_MOULD)
                    .addItem(ItemID.GOLD_BAR, 27)
                    .setRefill(1000)
            )),
    GOLD_NECKLACES(new GenericSmelting(6, ItemID.GOLD_NECKLACE, ItemID.GOLD_BAR)
            .setSimpleName("Gold necklaces")
            .setInventoryLoadout(new InventoryLoadout()
                    .addItem(ItemID.NECKLACE_MOULD)
                    .addItem(ItemID.GOLD_BAR, 27)
                    .setRefill(1000)
            )),
    GOLD_AMULETS_U(new GenericSmelting(8, ItemID.GOLD_AMULET_U, ItemID.GOLD_BAR)
            .setSimpleName("Gold amulets")
            .setInventoryLoadout(new InventoryLoadout()
                    .addItem(ItemID.AMULET_MOULD)
                    .addItem(ItemID.GOLD_BAR, 27)
                    .setRefill(1000)
            )),
    SAPPHIRE_RINGS(new GenericSmelting(20, ItemID.SAPPHIRE_RING, ItemID.GOLD_BAR)
            .setSimpleName("Sapphire rings")
            .setInventoryLoadout(new InventoryLoadout()
                    .addItem(ItemID.RING_MOULD)
                    .addItem(ItemID.SAPPHIRE, 13)
                    .setRefill(1000)
                    .addItem(ItemID.GOLD_BAR, 13)
                    .setRefill(1000)
            )),
    SAPPHIRE_NECKLACES(new GenericSmelting(22, ItemID.SAPPHIRE_NECKLACE, ItemID.GOLD_BAR)
            .setSimpleName("Sapphire necklaces")
            .setInventoryLoadout(new InventoryLoadout()
                    .addItem(ItemID.NECKLACE_MOULD)
                    .addItem(ItemID.SAPPHIRE, 13)
                    .setRefill(1000)
                    .addItem(ItemID.GOLD_BAR, 13)
                    .setRefill(1000)
            )),
    SAPPHIRE_AMULETS_U(new GenericSmelting(24, ItemID.SAPPHIRE_AMULET_U, ItemID.GOLD_BAR)
            .setSimpleName("Sapphire amulets")
            .setInventoryLoadout(new InventoryLoadout()
                    .addItem(ItemID.AMULET_MOULD)
                    .addItem(ItemID.SAPPHIRE, 13)
                    .setRefill(1000)
                    .addItem(ItemID.GOLD_BAR, 13)
                    .setRefill(1000)
            )),

    EMERALD_RINGS(new GenericSmelting(27, ItemID.EMERALD_RING, ItemID.GOLD_BAR)
            .setSimpleName("Emerald rings")
            .setInventoryLoadout(new InventoryLoadout()
                    .addItem(ItemID.RING_MOULD)
                    .addItem(ItemID.EMERALD, 13)
                    .setRefill(1000)
                    .addItem(ItemID.GOLD_BAR, 13)
                    .setRefill(1000)
            )),
    EMERALD_NECKLACES(new GenericSmelting(29, ItemID.EMERALD_NECKLACE, ItemID.GOLD_BAR)
            .setSimpleName("Emerald necklaces")
            .setInventoryLoadout(new InventoryLoadout()
                    .addItem(ItemID.NECKLACE_MOULD)
                    .addItem(ItemID.EMERALD, 13)
                    .setRefill(1000)
                    .addItem(ItemID.GOLD_BAR, 13)
                    .setRefill(1000)
            )),
    EMERALD_AMULETS_U(new GenericSmelting(31, ItemID.EMERALD_AMULET_U, ItemID.GOLD_BAR)
            .setSimpleName("Emerald amulets")
            .setInventoryLoadout(new InventoryLoadout()
                    .addItem(ItemID.AMULET_MOULD)
                    .addItem(ItemID.EMERALD, 13)
                    .setRefill(1000)
                    .addItem(ItemID.GOLD_BAR, 13)
                    .setRefill(1000)
            )),
    RUBY_NECKLACES(new GenericSmelting(40, ItemID.RUBY_NECKLACE, ItemID.GOLD_BAR)
            .setSimpleName("Ruby necklaces")
            .setInventoryLoadout(new InventoryLoadout()
                    .addItem(ItemID.NECKLACE_MOULD)
                    .addItem(ItemID.RUBY, 13)
                    .setRefill(1000)
                    .addItem(ItemID.GOLD_BAR, 13)
                    .setRefill(1000)
            )),
    // diamond takes an obnoxious amount of money so we wont be doing that
    ;

    CraftingModes(Fractal method) {
        this.method = method;
    }

    @Getter
    private final Fractal method;

}
