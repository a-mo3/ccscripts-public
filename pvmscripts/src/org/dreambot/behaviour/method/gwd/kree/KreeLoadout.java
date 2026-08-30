package org.dreambot.behaviour.method.gwd.kree;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.behaviour.method.gwd.GWDBoltPreference;
import org.dreambot.behaviour.method.gwd.RingPreference;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum KreeLoadout {
    // brew variants
    BREW_RAINBOW_DHIDE_ACB_BLOWPIPE(
            new InventoryLoadout(KreeConsts.brewBlowpipeLoadout),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)

                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ARMADYL_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemID.GUTHIX_DHIDE_BODY)
    ),
    BREW_RAINBOW_DHIDE_DCB_BLOWPIPE(
            new InventoryLoadout(KreeConsts.brewBlowpipeLoadout),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemID.GUTHIX_DHIDE_BODY)
    ),
    BREW_RAINBOW_DHIDE_ACB(
            new InventoryLoadout(KreeConsts.brewInv),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ARMADYL_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemID.GUTHIX_DHIDE_BODY)
    ),
    BREW_RAINBOW_DHIDE_DCB(
            new InventoryLoadout(KreeConsts.brewInv),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemID.GUTHIX_DHIDE_BODY)
    ),
    BREW_KARILS_ACB(
            new InventoryLoadout(KreeConsts.brewInv),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .addItem(EquipmentSlot.HANDS, ItemID.ZAMORAK_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ARMADYL_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
    ),
    BREW_KARILS_DCB(
            new InventoryLoadout(KreeConsts.brewInv),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .addItem(EquipmentSlot.HANDS, ItemID.ZAMORAK_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
    ),
    BREW_KARILS_ACB_BLOWPIPE(
            new InventoryLoadout(KreeConsts.brewBlowpipeLoadout),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .addItem(EquipmentSlot.HANDS, ItemID.ZAMORAK_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ARMADYL_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
    ),
    BREW_KARILS_DCB_BLOWPIPE(
            new InventoryLoadout(KreeConsts.brewBlowpipeLoadout),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemID.UNHOLY_SYMBOL)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.NONE)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
    ),
    BREW_KARILS_ACB_BLOWPIPE_DINHS(
            new InventoryLoadout(KreeConsts.brewBlowpipeLoadout)
                    .addItem(ItemID.DINHS_BULWARK),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ARMADYL_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemID.UNHOLY_SYMBOL)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.NONE)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
    ),
    BREW_CRYSTAL_BLOWPIPE_DINHS(
            new InventoryLoadout(KreeConsts.brewBlowpipeLoadout)
                    .addItem(ItemID.RUNE_CROSSBOW)
                    .addItem(ItemID.DINHS_BULWARK),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.ARMADYL_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.CRYSTAL_LEGS)
                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.CRYSTAL_HELM)
                    .addItem(EquipmentSlot.WEAPON, ItemID.BOW_OF_FAERDHINEN)
                    .addItem(EquipmentSlot.ARROWS, ItemID.UNHOLY_BLESSING)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.CRYSTAL_CHESTPLATE)
    ),
    BREW_KARILS_DCB_BLOWPIPE_DINHS(
            new InventoryLoadout(KreeConsts.brewBlowpipeLoadout)
                    .addItem(ItemID.DINHS_BULWARK),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .addItem(EquipmentSlot.HANDS, ItemID.ZAMORAK_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
    );

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    KreeLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}
