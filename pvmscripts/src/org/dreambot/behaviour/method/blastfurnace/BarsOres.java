package org.dreambot.behaviour.method.blastfurnace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.fractals.data.ItemID;

@AllArgsConstructor
@Getter
enum BarsOres {
    COPPER_ORE(BlastFurnaceVarbits.BLAST_FURNACE_COPPER_ORE, ItemID.COPPER_ORE),
    TIN_ORE(BlastFurnaceVarbits.BLAST_FURNACE_TIN_ORE, ItemID.TIN_ORE),
    IRON_ORE(BlastFurnaceVarbits.BLAST_FURNACE_IRON_ORE, ItemID.IRON_ORE),
    COAL(BlastFurnaceVarbits.BLAST_FURNACE_COAL, ItemID.COAL),
    MITHRIL_ORE(BlastFurnaceVarbits.BLAST_FURNACE_MITHRIL_ORE, ItemID.MITHRIL_ORE),
    ADAMANTITE_ORE(BlastFurnaceVarbits.BLAST_FURNACE_ADAMANTITE_ORE, ItemID.ADAMANTITE_ORE),
    RUNITE_ORE(BlastFurnaceVarbits.BLAST_FURNACE_RUNITE_ORE, ItemID.RUNITE_ORE),
    SILVER_ORE(BlastFurnaceVarbits.BLAST_FURNACE_SILVER_ORE, ItemID.SILVER_ORE),
    GOLD_ORE(BlastFurnaceVarbits.BLAST_FURNACE_GOLD_ORE, ItemID.GOLD_ORE),
    BRONZE_BAR(BlastFurnaceVarbits.BLAST_FURNACE_BRONZE_BAR, ItemID.BRONZE_BAR),
    IRON_BAR(BlastFurnaceVarbits.BLAST_FURNACE_IRON_BAR, ItemID.IRON_BAR),
    STEEL_BAR(BlastFurnaceVarbits.BLAST_FURNACE_STEEL_BAR, ItemID.STEEL_BAR),
    MITHRIL_BAR(BlastFurnaceVarbits.BLAST_FURNACE_MITHRIL_BAR, ItemID.MITHRIL_BAR),
    ADAMANTITE_BAR(BlastFurnaceVarbits.BLAST_FURNACE_ADAMANTITE_BAR, ItemID.ADAMANTITE_BAR),
    RUNITE_BAR(BlastFurnaceVarbits.BLAST_FURNACE_RUNITE_BAR, ItemID.RUNITE_BAR),
    SILVER_BAR(BlastFurnaceVarbits.BLAST_FURNACE_SILVER_BAR, ItemID.SILVER_BAR),
    GOLD_BAR(BlastFurnaceVarbits.BLAST_FURNACE_GOLD_BAR, ItemID.GOLD_BAR);

    private final int varbit;
    private final int itemID;

    public int getValue() {
        return PlayerSettings.getBitValue(varbit);
    }
}