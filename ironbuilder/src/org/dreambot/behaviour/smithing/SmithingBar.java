package org.dreambot.behaviour.smithing;

import org.dreambot.behaviour.mining.MiningDTO;
import org.dreambot.behaviour.mining.MiningMode;
import org.dreambot.loadouts.InventoryLoadout;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.utility.OwnedItems;

public enum SmithingBar {
    BRONZE(ItemID.BRONZE_BAR, 1, new InventoryLoadout()
            .addItem(new InventoryLoadoutItem(ItemID.TIN_ORE)
                    .setInventoryMax(14).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.BRONZE)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.TIN_ORE) > 300))
            )
            .addItem(new InventoryLoadoutItem(ItemID.COPPER_ORE)
                    .setInventoryMax(14).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.BRONZE)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.TIN_ORE) > 300))
            )
    ),
    IRON(ItemID.IRON_BAR, 15, new InventoryLoadout()
            .addItem(new InventoryLoadoutItem(ItemID.IRON_ORE)
                    .setInventoryMax(28).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.IRON)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.IRON_ORE) > 300))
            )
    ),
//    SILVER(ItemID.SILVER_BAR, 20, new InventoryLoadout()
////            .addItem(
////            )
//    ),
    STEEL(ItemID.STEEL_BAR, 30, new InventoryLoadout()
            .addItem(new InventoryLoadoutItem(ItemID.IRON_ORE)
                    .setInventoryMax(8).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.IRON)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.IRON_ORE) > 300))
            )
            // todo this will mine in wildy
            .addItem(new InventoryLoadoutItem(ItemID.COAL)
                    .setInventoryMax(16).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.COAL)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.COAL) > 300))
            )
    ),
    GOLD(ItemID.GOLD_BAR, 40, new InventoryLoadout()
            .addItem(new InventoryLoadoutItem(ItemID.GOLD_ORE)
                    .setInventoryMax(28).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.GOLD)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.GOLD_ORE) > 300))
            )
    ),
    MITHRIL(ItemID.MITHRIL_BAR, 50, new InventoryLoadout()
            .addItem(new InventoryLoadoutItem(ItemID.MITHRIL_ORE)
                    .setInventoryMax(5).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.MITHRIL)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.MITHRIL_ORE) > 300))
            )
            // todo this will mine in wildy
            .addItem(new InventoryLoadoutItem(ItemID.COAL)
                    .setInventoryMax(20).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.COAL)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.COAL) > 300))
            )
    ),
    ADAMANT(ItemID.ADAMANTITE_BAR, 70, new InventoryLoadout()
            .addItem(new InventoryLoadoutItem(ItemID.ADAMANTITE_ORE)
                    .setInventoryMax(4).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.ADAMANTITE)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.ADAMANTITE_ORE) > 300))
            )
            // todo this will mine in wildy
            .addItem(new InventoryLoadoutItem(ItemID.COAL)
                    .setInventoryMax(24).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.COAL)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.COAL) > 300))
            )
    ),
    RUNE(ItemID.RUNITE_BAR, 85, new InventoryLoadout()
            .addItem(new InventoryLoadoutItem(ItemID.RUNITE_ORE)
                    .setInventoryMax(3).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.RUNITE)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.RUNITE_ORE) > 20))
            )
            // todo this will mine in wildy
            .addItem(new InventoryLoadoutItem(ItemID.COAL)
                    .setInventoryMax(24).setInventoryMin(1)
                    .setRestockMethod(new MiningDTO().setBankOre(true).setMode(MiningMode.COAL)
                            .toFractal().setAcceptCondition(() -> OwnedItems.count(ItemID.COAL) > 300))
            )
    ),
    ;

    final int barId;
    final int level;
    final InventoryLoadout loadout;

    SmithingBar(int barId, int level, InventoryLoadout loadout) {
        this.barId = barId;
        this.level = level;
        this.loadout = loadout;
    }
}
