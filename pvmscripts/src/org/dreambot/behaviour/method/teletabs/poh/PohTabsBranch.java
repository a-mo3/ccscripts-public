package org.dreambot.behaviour.method.teletabs.poh;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.function.Supplier;

public class PohTabsBranch extends Fractal {
    public static EquipmentLoadout teleportBase = new EquipmentLoadout()
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY);

    public PohTabsBranch(Supplier<Boolean> acceptCondition, PohTeleTabOption option) {
        super(acceptCondition);
        setSimpleName("Tabs");
        this.paintArraySupplier = () -> new String[]{
                "Blacklist: " + EnterHouse.blacklistedOwners,
                "House: " + EnterHouse.lastHouseOwner,
                "",
                "" // space so i can see if the client is small
        };
        addChildren(
                new Fractal(() -> !option.inventoryLoadout.isFulfilled()
                        || !option.equipmentLoadout.isFulfilled())
                        .setInventoryLoadout(option.inventoryLoadout)
                        .setEquipmentLoadout(option.equipmentLoadout)
                        .setSimpleName("Gear up"),
                // get on w 330
                // todo add something for your own house
                new Fractal(() -> Worlds.getCurrentWorld() != 330)
                        .setSimpleName("Hop to 330")
                        .setPrependLogic(() -> {
                            WorldHopper.hopWorld(330);
                            Sleep.sleepUntil(() -> Worlds.getCurrentWorld() == 330, 3300);
                            return true;
                        }),
                // unnote clay / get equipment
                new UnnoteClay(() -> !Inventory.contains(ItemID.SOFT_CLAY))
                        .setSimpleName("Unote clay"),
                new EnterHouse()
                        .setAcceptCondition(() -> !Client.isDynamicRegion())
                        .setSimpleName("Enter house"),
                // get in house and make the tabs
                new MakeTabs(() -> true)
                        .setSimpleName("Make tabs")
        );
    }
}
