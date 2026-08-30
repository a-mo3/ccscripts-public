package org.dreambot.behaviour.bankdump;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class OpenTemporossCrate extends Fractal {
    public OpenTemporossCrate() {
        super(() -> Worlds.getCurrent().isMembers() && OwnedItems.contains(ItemID.TEMPOROSS_CASKET));

        setSimpleName("Temporosss loot");
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.TEMPOROSS_CASKET, OwnedItems.count(ItemID.TEMPOROSS_CASKET));

    }

    @Override
    public int onLoop() {

        Inventory.interact(ItemID.TEMPOROSS_CASKET);

        return ReactionGenerator.getNormal();
    }
}
