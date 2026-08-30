package org.dreambot.fractals.loadout;


import org.dreambot.fractals.data.ItemID;

// data class to hold my itemvariants
public class ItemVariants {
    public static final ItemVariant COMBAT_BRACLET = new ItemVariant(
            ItemID.COMBAT_BRACELET6,
            ItemID.COMBAT_BRACELET5,
            ItemID.COMBAT_BRACELET4,
            ItemID.COMBAT_BRACELET3,
            ItemID.COMBAT_BRACELET2,
            ItemID.COMBAT_BRACELET1
    );

    public static final ItemVariant AMULET_OF_GLORY = new ItemVariant(
            ItemID.AMULET_OF_GLORY6,
            ItemID.AMULET_OF_GLORY5,
            ItemID.AMULET_OF_GLORY4,
            ItemID.AMULET_OF_GLORY3,
            ItemID.AMULET_OF_GLORY2,
            ItemID.AMULET_OF_GLORY1
    );

    public static final ItemVariant ENERGY_POTION = new ItemVariant(
            ItemID.ENERGY_POTION4,
            ItemID.ENERGY_POTION3,
            ItemID.ENERGY_POTION2,
            ItemID.ENERGY_POTION1
    );
}
