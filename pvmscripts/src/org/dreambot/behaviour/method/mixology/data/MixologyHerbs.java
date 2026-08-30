package org.dreambot.behaviour.method.mixology.data;

import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.OwnedItems;

import java.util.Arrays;

public enum MixologyHerbs {
    GUAM(ItemID.GUAM_LEAF, ItemID.GUAM_POTION_UNF, ItemID.GRIMY_GUAM_LEAF, PotionReagent.MOX, 10, 3),
    MARRENTILL(ItemID.MARRENTILL, ItemID.MARRENTILL_POTION_UNF, ItemID.GRIMY_MARRENTILL, PotionReagent.MOX, 13, 5),
    TARROMIN(ItemID.TARROMIN, ItemID.TARROMIN_POTION_UNF, ItemID.GRIMY_TARROMIN, PotionReagent.MOX, 15, 11),
    HARRALANDER(ItemID.HARRALANDER, ItemID.HARRALANDER_POTION_UNF, ItemID.GRIMY_HARRALANDER, PotionReagent.MOX, 20, 20),
    RANARR_WEED(ItemID.RANARR_WEED, ItemID.RANARR_POTION_UNF, ItemID.GRIMY_RANARR_WEED, PotionReagent.LYE, 26, 25),
    TOADFLAX(ItemID.TOADFLAX, ItemID.TOADFLAX_POTION_UNF, ItemID.GRIMY_TOADFLAX, PotionReagent.LYE, 32, 30),
    IRIT(ItemID.IRIT_LEAF, ItemID.IRIT_POTION_UNF, ItemID.GRIMY_IRIT_LEAF, PotionReagent.AGA, 30, 40),
    AVANTOE(ItemID.AVANTOE, ItemID.AVANTOE_POTION_UNF, ItemID.GRIMY_AVANTOE, PotionReagent.LYE, 30, 48),
    KWUARM(ItemID.KWUARM, ItemID.KWUARM_POTION_UNF, ItemID.GRIMY_KWUARM, PotionReagent.LYE, 33, 54),
    HUASCA(ItemID.HUASCA, ItemID.HUASCA_POTION_UNF, ItemID.GRIMY_HUASCA, PotionReagent.AGA, 20, 58),
    SNAPDRAGON(ItemID.SNAPDRAGON, ItemID.SNAPDRAGON_POTION_UNF, ItemID.GRIMY_SNAPDRAGON, PotionReagent.LYE, 40, 59),
    CADANTINE(ItemID.CADANTINE, ItemID.CADANTINE_POTION_UNF, ItemID.CADANTINE, PotionReagent.AGA, 34, 65),
    LANTADYME(ItemID.LANTADYME, ItemID.LANTADYME_POTION_UNF, ItemID.GRIMY_LANTADYME, PotionReagent.AGA, 40, 67),
    DWARF_WEED(ItemID.DWARF_WEED, ItemID.DWARF_WEED_POTION_UNF, ItemID.GRIMY_DWARF_WEED, PotionReagent.AGA, 42, 70),
    TORSTOL(ItemID.TORSTOL, ItemID.TORSTOL_POTION_UNF, ItemID.GRIMY_TORSTOL, PotionReagent.AGA, 44, 75),
    ;

    final int itemId;
    final int unfPotionId;
    final int grimyId;
    final PotionReagent potionReagent;
    final int quantity;
    final int levelReq;

    MixologyHerbs(int itemId, int unfPotionId, int grimyId, PotionReagent potionComponent, int quantity, int levelReq) {
        this.itemId = itemId;
        this.unfPotionId = unfPotionId;
        this.grimyId = grimyId;
        this.potionReagent = potionComponent;
        this.quantity = quantity;
        this.levelReq = levelReq;
    }

    public static int getCheapest(PotionReagent type, boolean includeUnf, boolean includeGrimy) {
        // todo replace with live calculation later, maybe comment out herbs with bad volumes
        // todo filter out the herbs above our level
        switch (type) {
            case MOX:
                return HARRALANDER.itemId;
            case LYE:
                return TOADFLAX.itemId;
            case AGA:
                return LANTADYME.itemId;
        }
        return AVANTOE.itemId;
    }

    /**
     * @param type reagent type
     * @return the amount of reagent one would have if they converted all the owned materials to paste
     */
    public static int getOwnedPotential(PotionReagent type) {
        return Arrays.stream(values())
                .filter(x -> x.potionReagent == type)
                .mapToInt(x -> OwnedItems.count(x.itemId, true) * x.quantity)
                .sum();
    }

    /**
     * @return the ID for an item we own that can be converted to
     */
    public static int getOwnedIdForType(PotionReagent reagent) {
        return Arrays.stream(values())
                .filter(x -> x.potionReagent == reagent)
                .filter(x -> x.getOwned() > 0)
                .mapToInt(MixologyHerbs::getOwned)
                .findFirst()
                .orElse(-1);
    }

    private int getOwned() {
        if (OwnedItems.contains(itemId)) return itemId;
        if (OwnedItems.contains(unfPotionId)) return unfPotionId;
        // exclude grimy because i havent added cleaning logic
        return -1;
    }
}
