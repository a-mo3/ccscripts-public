package org.dreambot.settings;

import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;

/**
 * Item Variants we support and check for when loading equipment
 */
public enum EquipmentLoadoutVariants {
    GLORY(ItemVariants.AMULET_OF_GLORY),
    RING_OF_WEALTH(ItemVariants.RING_OF_WEALTH),
    COMBAT_BRACELET(ItemVariants.COMBAT_BRACLET),
    KARIL_TOP(ItemVariants.KARIL_TOP),
    KARIL_BOTTOM(ItemVariants.KARIL_SKIRT),
    ARCHER_RING(ItemVariants.ARCHERS_RING),
    ;

    final ItemVariant itemVariant;

    EquipmentLoadoutVariants(ItemVariant itemVariant) {
        this.itemVariant = itemVariant;
    }
}
