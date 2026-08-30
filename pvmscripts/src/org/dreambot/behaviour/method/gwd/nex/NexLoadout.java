package org.dreambot.behaviour.method.gwd.nex;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.behaviour.method.gwd.GWDBoltPreference;
import org.dreambot.behaviour.method.gwd.RingPreference;
import org.dreambot.behaviour.method.gwd.zilyana.ZilyanaConsts;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.ZilyanaSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;

public enum NexLoadout {
    BREW_RAINBOW_DHIDE_DCB_BLOWPIPE(
            new InventoryLoadout()
                    .addItem(ItemID.SARADOMIN_BREW4, 14).setRefill(50)
                    .addItem(ItemID.SUPER_RESTORE4, 10).setRefill(50)
                    .addItem(ItemID.STAMINA_POTION4, 2).setRefill(40)
//                    .addItem(ItemID.ECUMENICAL_KEY).enabledIfOwned()
                    .addItem(ItemID.RUNE_POUCH),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)
//                    .addItem(EquipmentSlot.RING, ItemID.LIGHTBEARER)
//                    .setEnabledCondition(() -> RingPreference.ringPreference == RingPreference.LIGHTBEARER)

                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_CROSSBOW)
                    .addItem(EquipmentSlot.CHEST, ItemID.ANCIENT_DHIDE_BODY)

                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUBY_BOLTS_E, 400, 700))

                    .addItem(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
    );

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    NexLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}
