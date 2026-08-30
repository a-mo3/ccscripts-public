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

    public static final ItemVariant SKILLS_NECKLACE = new ItemVariant(
            ItemID.SKILLS_NECKLACE6,
            ItemID.SKILLS_NECKLACE5,
            ItemID.SKILLS_NECKLACE4,
            ItemID.SKILLS_NECKLACE3,
            ItemID.SKILLS_NECKLACE2,
            ItemID.SKILLS_NECKLACE1
    );

    public static final ItemVariant STAMINA_POTION = new ItemVariant(
            ItemID.STAMINA_POTION4,
            ItemID.STAMINA_POTION3,
            ItemID.STAMINA_POTION2,
            ItemID.STAMINA_POTION1
    );

    public static final ItemVariant RING_OF_WEALTH = new ItemVariant(
            ItemID.RING_OF_WEALTH_5,
            ItemID.RING_OF_WEALTH_4,
            ItemID.RING_OF_WEALTH_3,
            ItemID.RING_OF_WEALTH_2,
            ItemID.RING_OF_WEALTH_1
    );

    public static final ItemVariant TRIDENT = new ItemVariant(
            ItemID.TRIDENT_OF_THE_SEAS_FULL,
            ItemID.TRIDENT_OF_THE_SEAS,
            ItemID.UNCHARGED_TRIDENT
    );

    public static final ItemVariant LOOTING_BAG = new ItemVariant(
            ItemID.LOOTING_BAG_OPENED,
            ItemID.LOOTING_BAG_CLOSED
    );
}
