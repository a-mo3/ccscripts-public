package org.dreambot.behaviour.method.revs.data;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

import java.util.Arrays;

public enum RevenantEquipmentLoadout {
    FIRE_STAFF(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                    .setStrict(true)
    ),
    DHIDE_MSB(
            new EquipmentLoadout(RevLoadoutBases.dhideBase)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW)


                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUNE_ARROW, 50, 250))
                    .setRefill(2000)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .setEnabledCondition(() -> OwnedItems.containsAny(
                            Arrays.stream(ItemVariants.AVAS.getIds()).mapToInt(x -> x).toArray())
                    )
    ),
    DHIDE_KNIVES(
            new EquipmentLoadout(RevLoadoutBases.dhideBase)
                    .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.RUNE_KNIFE, 50, 250))
                    .setRefill(2000)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .setEnabledCondition(() -> OwnedItems.containsAny(
                            Arrays.stream(ItemVariants.AVAS.getIds()).mapToInt(x -> x).toArray())
                    )
    ),
    DHIDE_URSINE(
            new EquipmentLoadout(RevLoadoutBases.dhideBase)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.URSINE_CHAINMACE)
    ),
    DHIDE_VIGGORAS(
            new EquipmentLoadout(RevLoadoutBases.dhideBase)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.VIGGORA_CHAINMACE)
    ),
    DHIDE_WEBWEAVER(
            new EquipmentLoadout(RevLoadoutBases.dhideBase)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.WEBWEAVER_BOW)
    ),
    DHIDE_CRAWS(
            new EquipmentLoadout(RevLoadoutBases.dhideBase)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.CRAWS_BOW)
    ),
    XER_THAMMAMONS(
            new EquipmentLoadout(RevLoadoutBases.xericanBase)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.THAMMARONS)
    ),
    XER_ACCURSED(
            new EquipmentLoadout(RevLoadoutBases.xericanBase)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.ACCURSED)
    ),
    ;

    public final EquipmentLoadout loadout;

    RevenantEquipmentLoadout(EquipmentLoadout loadout) {
        this.loadout = loadout;
    }
}
