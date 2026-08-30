package org.dreambot.behaviour.method.gwd.nex.frozendoor;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

public class MakeFrozenKey extends Fractal {
    public MakeFrozenKey() {
        super(() -> OwnedItems.containsAll(
                ItemID.FROZEN_KEY_PIECE_ARMADYL,
                ItemID.FROZEN_KEY_PIECE_ZAMORAK,
                ItemID.FROZEN_KEY_PIECE_SARADOMIN,
                ItemID.FROZEN_KEY_PIECE_BANDOS
        ));
        setSimpleName("Make whole frozen key");

        inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.FROZEN_KEY_PIECE_ARMADYL)
                .addItem(ItemID.FROZEN_KEY_PIECE_ZAMORAK)
                .addItem(ItemID.FROZEN_KEY_PIECE_SARADOMIN)
                .addItem(ItemID.FROZEN_KEY_PIECE_BANDOS)
        ;
    }

    @Override
    public int onLoop() {
        if (Players.getLocal().getY() > 4000) {
            log("Get out of boss room");
            Item glory = Equipment.get(x -> ItemVariants.AMULET_OF_GLORY.contains(x.getId()));
            if (glory != null) {
                glory.interact("Edgeville");
            }

            Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getNormal();
        }

        if (Widgets.isOpen()) Widgets.closeAll();
        log("Make key");
        Inventory.combine(ItemID.FROZEN_KEY_PIECE_ARMADYL, ItemID.FROZEN_KEY_PIECE_ZAMORAK);
        return ReactionGenerator.getNormal();
    }
}
