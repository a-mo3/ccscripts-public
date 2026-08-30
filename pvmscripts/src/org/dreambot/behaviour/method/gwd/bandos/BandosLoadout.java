package org.dreambot.behaviour.method.gwd.bandos;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.behaviour.method.gwd.GWDBoltPreference;
import org.dreambot.behaviour.method.gwd.RingPreference;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum BandosLoadout {
    //    DHIDE_GUTHANS_DCB_BLOWPIPE(
//            new InventoryLoadout(BandosConsts.guthansBrewBlowpipeLoadout),
//            new EquipmentLoadout()
//                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
//                    .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
//                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
//                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
//                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_CROSSBOW)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 200, 400))
//                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
//                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
//                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
//                    .addItem(EquipmentSlot.CHEST, ItemID.GUTHIX_DHIDE_BODY)
//    ),
    // brew variants
    RAINBOW_DHIDE_ACB_BLOWPIPE(
            new InventoryLoadout(BandosConsts.brewBlowpipeLoadout),
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
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 200, 400))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemID.GUTHIX_DHIDE_BODY)
    ),
    RAINBOW_DHIDE_DCB_BLOWPIPE(
            new InventoryLoadout(BandosConsts.brewBlowpipeLoadout),
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
    FULL_CRYSTAL_BOWFA(
            new InventoryLoadout(BandosConsts.brewBlowpipeLoadout),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.BANDOS_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.CRYSTAL_LEGS)
                    .addItem(EquipmentSlot.HANDS, ItemID.ZAMORAK_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemVariants.CRYSTAL_HELM)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.BOWFA)
                    .addItem(EquipmentSlot.ARROWS, ItemID.HONOURABLE_BLESSING)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.CRYSTAL_CHESTPLATE)
    ),
    RAINBOW_DHIDE_ACB(
            new InventoryLoadout(BandosConsts.brewInv),
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
    RAINBOW_DHIDE_DCB(
            new InventoryLoadout(BandosConsts.brewInv),
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
    KARILS_ACB(
            new InventoryLoadout(BandosConsts.brewInv),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.BANDOS_DHIDE_BOOTS)
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
    KARILS_DCB(
            new InventoryLoadout(BandosConsts.brewInv),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.BANDOS_BOOTS)
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
    KARILS_ACB_BLOWPIPE(
            new InventoryLoadout(BandosConsts.brewBlowpipeLoadout),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.BANDOS_DHIDE_BOOTS)
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
    KARILS_DCB_BLOWPIPE(
            new InventoryLoadout(BandosConsts.brewBlowpipeLoadout),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.BANDOS_DHIDE_BOOTS)
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
    RAINBOW_DHIDE_CHEAP_SHIELD(
            new InventoryLoadout(BandosConsts.brewInv),
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
                    .addItem(EquipmentSlot.SHIELD, ItemID.SARADOMIN_DHIDE_SHIELD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemID.GUTHIX_DHIDE_BODY)
    ),
    RAINBOW_DHIDE_DCB_BLOWPIPE_SHIELD(
            new InventoryLoadout(BandosConsts.brewBlowpipeLoadout),
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
                    .addItem(EquipmentSlot.SHIELD, ItemID.SARADOMIN_DHIDE_SHIELD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemID.GUTHIX_DHIDE_BODY)
    ),
    ;

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    BandosLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}
