package org.dreambot.behaviour.training.hunter.sallys;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GenericSalamander extends Fractal {


    final Area location;

    List<Integer> droppables = Arrays.asList(
            ItemID.SWAMP_LIZARD,
            ItemID.ORANGE_SALAMANDER,
            ItemID.RED_SALAMANDER,
            ItemID.BLACK_SALAMANDER,
            ItemID.IMMATURE_TECU_SALAMANDER
    );

    public GenericSalamander(Supplier<Boolean> acceptCondition, Area location) {
        super(acceptCondition);
        this.location = location;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.ROPE, 1, 12)
                .addItem(ItemID.SMALL_FISHING_NET, 1, 12)
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
        ;

        setSimpleName("Salamander");
    }

    @Override
    public int onLoop() {
        if (!location.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(location);
            return ReactionGenerator.getNormal();
        }

        // take fallen supplies
        GroundItem item = GroundItems.closest(x -> location.contains(x) && (x.getId() == ItemID.ROPE || x.getId() == ItemID.SMALL_FISHING_NET));
        if (item != null) {
            log("Take fallen item");
            item.interact();
            Sleep.sleepUntil(() -> !item.exists(), 1400);
            return ReactionGenerator.getNormal();
        }

        // loot trap
        GameObject lootableTrap = GameObjects.closest(8992);
        if (lootableTrap != null) {
            log("Loot trap ");
            lootableTrap.interact();
            Sleep.sleepUntil(() -> !lootableTrap.exists(), 1600);
            return ReactionGenerator.getNormal();
        }

        // set trap
        GameObject tree = GameObjects.closest(8990);
        if (tree != null) {
            log("Set trap");
            tree.interact("Set-trap");
        }
        return ReactionGenerator.getNormal();
    }
}
