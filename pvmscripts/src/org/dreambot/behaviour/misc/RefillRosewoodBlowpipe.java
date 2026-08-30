package org.dreambot.behaviour.misc;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

public class RefillRosewoodBlowpipe extends Fractal {
    public RefillRosewoodBlowpipe() {
        super(() -> OwnedItems.contains(ItemID.ROSEWOOD_BLOWPIPE_EMPTY));


        prependLogic = () -> {
            if (Equipment.contains(ItemID.ROSEWOOD_BLOWPIPE, ItemID.ROSEWOOD_BLOWPIPE_EMPTY)) {
                if (Inventory.isFull()) new BankAllInventoryEvent().execute();
                Equipment.unequip(EquipmentSlot.WEAPON);
                return true;
            }
            return false;
        };

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.ROSEWOOD_BLOWPIPE_EMPTY)
                .addItem(ItemID.ADAMANT_DART, 2000)
                ;
    }

    @Override
    public int onLoop() {
        if (Bank.isOpen() || GrandExchange.isOpen()) {
            log("Close widgets");
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }

        log("Charge blowpipe");
        Inventory.combine(ItemID.ROSEWOOD_BLOWPIPE_EMPTY, ItemID.ADAMANT_DART);
        return ReactionGenerator.getNormal();
    }
}
