package org.dreambot.behaviour.method.revs;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.RevenantSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

public class ChargeBracelet extends Fractal {
    InventoryLoadout loadout = new InventoryLoadout()
            .addItem(ItemID.BRACELET_OF_ETHEREUM_UNCHARGED)
            .addItem(ItemID.REVENANT_ETHER, SettingsRepository.findInstanceOf(new RevenantSettings()).braceletRecharge);

    public ChargeBracelet() {
        super(() -> !Combat.isInWild()
                && SettingsRepository.findInstanceOf(new RevenantSettings()).useEtherBracelet
                && OwnedItems.contains(ItemID.BRACELET_OF_ETHEREUM_UNCHARGED)
                && !Equipment.contains(ItemID.BRACELET_OF_ETHEREUM)
        );


        this.prependLogic = () -> {
            if (Equipment.contains(ItemID.BRACELET_OF_ETHEREUM_UNCHARGED)) {
                if (Widgets.isOpen()) Widgets.closeAll();
                if (Inventory.isFull()) new BankAllInventoryEvent().execute();
                Equipment.unequip(EquipmentSlot.HANDS);
                return true;
            }
            // if you own a charged one, get that out
            if (Inventory.contains(ItemID.BRACELET_OF_ETHEREUM)) {
                if (Widgets.isOpen()) Widgets.closeAll();
                Inventory.interact(ItemID.BRACELET_OF_ETHEREUM, "Wear");
                return true;
            }

            if (Bank.contains(ItemID.BRACELET_OF_ETHEREUM)) {
                if (!Bank.isOpen()) {
                    if (Walking.shouldWalk()) Bank.open();
                    return true;
                }

                if (Inventory.isFull()) new BankAllInventoryEvent().execute();
                Bank.withdraw(ItemID.BRACELET_OF_ETHEREUM);
                return true;
            }
            return false;
        };
    }

    @Override
    public int onLoop() {
        if (Widgets.isOpen()) {
            Widgets.closeAll();
        }

        if (Equipment.contains(ItemID.BRACELET_OF_ETHEREUM_UNCHARGED)) {
            if (Inventory.isFull()) new BankAllInventoryEvent().execute();
            Equipment.unequip(EquipmentSlot.HANDS);
            return ReactionGenerator.getNormal();
        }


        if (!loadout.isFulfilled()) {
            Logger.info("Get loadout");
            new WithdrawLoadoutEvent(loadout, null)
                    .executed();
            return ReactionGenerator.getNormal();
        }

        Inventory.combine(ItemID.BRACELET_OF_ETHEREUM_UNCHARGED, ItemID.REVENANT_ETHER);
        return ReactionGenerator.getNormal();
    }
}
