package org.dreambot.behaviour.method.gwd.zammy;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.behaviour.method.gwd.GWDBoltPreference;
import org.dreambot.behaviour.method.gwd.RingPreference;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum ZammyLoadout {
    KARILS_CB(
            new InventoryLoadout(ZammyConsts.brewInv),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
                    .addItem(EquipmentSlot.FEET, ItemID.BANDOS_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .addItem(EquipmentSlot.HANDS, ItemID.ZAMORAK_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.SARADOMIN_COIF)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUBY_BOLTS_E, 400, 700))
                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)

                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)

                    .addItem(EquipmentSlot.SHIELD, ItemID.RUNE_KITESHIELD) // todo maybe range shield
//                    .addItem(EquipmentSlot.SHIELD, ItemID.DRAGON_DEFENDER).enabledIfOwned()
    ),
//    KARILS_WHIP(
//            new InventoryLoadout(ZammyConsts.brewInv)
//                    .addItem(ItemID.CANNON_BASE),
//            new EquipmentLoadout()
//                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.ARCHERS)
//                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)
//                    .addItem(EquipmentSlot.FEET, ItemID.BANDOS_BOOTS)
//                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
//                    .addItem(EquipmentSlot.HANDS, ItemID.ZAMORAK_BRACERS)
//                    .addItem(EquipmentSlot.HAT, ItemID.SARADOMIN_COIF)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_DRAGON_BOLTS_E, 300, 400))
//                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DRAGON)
//                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.DIAMOND_BOLTS_E, 400, 700))
//                    .setEnabledCondition(() -> GWDBoltPreference.boltPreference == GWDBoltPreference.DIAMOND)
//                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
//                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()
//                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
//
//                    .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
//
//                    .addItem(EquipmentSlot.SHIELD, ItemID.RUNE_KITESHIELD)
//                    .addItem(EquipmentSlot.SHIELD, ItemID.DRAGON_DEFENDER).enabledIfOwned()
//    )
    ;

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    ZammyLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}
