package org.dreambot.behaviour.misc;

import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class RechargeCrystal extends Fractal {
    static List<Integer> unchargedIds = Arrays.asList(
            ItemID.BOW_OF_FAERDHINEN_INACTIVE,
            ItemID.CRYSTAL_HELM_INACTIVE,
            ItemID.CRYSTAL_BODY_INACTIVE,
            ItemID.CRYSTAL_LEGS_INACTIVE
    );

    public RechargeCrystal() {

        super(() -> {
            // when in fight dont need to check entire bank and that
            if (Client.isDynamicRegion()) return Equipment.contains(i -> unchargedIds.contains(i.getId()));
            return OwnedItems.contains(i -> unchargedIds.contains(i.getId()));
        });

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.CRYSTAL_BODY_INACTIVE)
                .setStrictSupplier(() -> OwnedItems.contains(ItemID.CRYSTAL_BODY_INACTIVE))
                .addItem(ItemID.CRYSTAL_LEGS_INACTIVE)
                .setStrictSupplier(() -> OwnedItems.contains(ItemID.CRYSTAL_LEGS_INACTIVE))
                .addItem(ItemID.CRYSTAL_HELM_INACTIVE)
                .setStrictSupplier(() -> OwnedItems.contains(ItemID.CRYSTAL_HELM_INACTIVE))
                .addItem(ItemID.BOW_OF_FAERDHINEN_INACTIVE)
        ;
    }

    @Override
    public int onLoop() {
        if (Client.isDynamicRegion()) {
            Logger.info("Telly out to recharge blowpipe");
//            ZulrahSmeebo.teleout();
            return ReactionGenerator.getQuick();
        }
        int requiredShards = OwnedItems.count(x -> unchargedIds.contains(x.getId())) * 100;
        if (requiredShards < OwnedItems.count(ItemID.CRYSTAL_SHARD)) {
            Logger.info("Get shards");
            new GetCrystalShardsEvent().executed();
        }

        if (Equipment.contains(x -> unchargedIds.contains(x.getId()))) {
            Equipment.unequip(x -> unchargedIds.contains(x.getId()));
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isFull()) {
            Logger.info("Deposit all");
            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }

        if (Bank.contains(ItemID.CRYSTAL_SHARD)) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Bank.withdrawAll(ItemID.CRYSTAL_SHARD);
            return ReactionGenerator.getNormal();
        }

        Bank.close();

        if (Dialogues.canEnterInput()) {
            Keyboard.type("100", true);
            Sleep.sleep(3000);
            return ReactionGenerator.getNormal();
        }

        Inventory.combine(Inventory.get(ItemID.CRYSTAL_SHARD), Inventory.get(x -> x.getName().toLowerCase().contains("inactive")));
        return ReactionGenerator.getNormal();
    }
}
