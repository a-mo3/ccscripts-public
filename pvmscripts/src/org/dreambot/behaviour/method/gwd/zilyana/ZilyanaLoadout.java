package org.dreambot.behaviour.method.gwd.zilyana;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.behaviour.method.gwd.GWDBoltPreference;
import org.dreambot.behaviour.method.gwd.RingPreference;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum ZilyanaLoadout {
    // brew variants
//    BREW_RAINBOW_DHIDE_ACB_BLOWPIPE(
//            new InventoryLoadout(ZilyanaConsts.brewBlowpipeLoadout),
//            new EquipmentLoadout()
//                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
//                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
//                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
//                    .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
//                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
//                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
//                    .addItem(EquipmentSlot.WEAPON, ItemID.ARMADYL_CROSSBOW)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
//                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
//                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
//                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
//                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
//                    .addItem(EquipmentSlot.CHEST, ItemID.GUTHIX_DHIDE_BODY)
//    ),
    BREW_RAINBOW_DHIDE_DCB_BLOWPIPE(
            new InventoryLoadout(ZilyanaConsts.brewBlowpipeLoadout),
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
//    BREW_RAINBOW_DHIDE_ACB(
//            new InventoryLoadout(ZilyanaConsts.brewInv),
//            new EquipmentLoadout()
//                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
//                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
//                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
//                    .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
//                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
//                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
//                    .addItem(EquipmentSlot.WEAPON, ItemID.ARMADYL_CROSSBOW)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
//                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
//                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
//                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
//                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
//                    .addItem(EquipmentSlot.CHEST, ItemID.GUTHIX_DHIDE_BODY)
//    ),
    BREW_RAINBOW_DHIDE_DCB(
            new InventoryLoadout(ZilyanaConsts.brewInv),
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
//    BREW_KARILS_ACB(
//            new InventoryLoadout(ZilyanaConsts.brewInv),
//            new EquipmentLoadout()
//                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
//                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
//                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
//                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
//                    .addItem(EquipmentSlot.HANDS, ItemID.ZAMORAK_BRACERS)
//                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
//                    .addItem(EquipmentSlot.WEAPON, ItemID.ARMADYL_CROSSBOW)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
//                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
//                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
//                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
//                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
//                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
//    ),
    BREW_KARILS_DCB(
            new InventoryLoadout(ZilyanaConsts.brewInv),
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
//    BREW_KARILS_ACB_BLOWPIPE(
//            new InventoryLoadout(ZilyanaConsts.brewBlowpipeLoadout),
//            new EquipmentLoadout()
//                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
//                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
//                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
//                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
//                    .addItem(EquipmentSlot.HANDS, ItemID.ZAMORAK_BRACERS)
//                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
//                    .addItem(EquipmentSlot.WEAPON, ItemID.ARMADYL_CROSSBOW)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
//                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
//                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
//                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
//                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
//                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
//    ),
    BREW_KARILS_DCB_BLOWPIPE(
            new InventoryLoadout(ZilyanaConsts.brewBlowpipeLoadout),
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

    ZilyanaLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}
