package org.dreambot.behaviour.dragons;

import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.script.StaffMode;
import org.dreambot.settings.timing.ReactionGenerator;


public class RechargeTrident extends Fractal {
    InventoryLoadout rechargeLoadout = new InventoryLoadout()
            .addItem(ItemID.UNCHARGED_TRIDENT).setEnabledCondition(() -> !Equipment.contains(ItemID.UNCHARGED_TRIDENT))
            .addItem(ItemID.DEATH_RUNE, 2500)
            .addItem(ItemID.CHAOS_RUNE, 2500)
            .addItem(ItemID.FIRE_RUNE, 12500)
            .addItem(ItemID.COINS_995, 25000)
//            .addItem(ItemID.ZULRAHS_SCALES, 2500)
            .setMuleRequestAmount(750_000)
            .setStrict(true);
//            .addItem(ItemVariant.RING_OF_DUELING);


    public RechargeTrident() {
        this.eventBreakCondition = () -> !Worlds.getCurrent().isMembers();
        this.inventoryLoadout = rechargeLoadout;
    }

    @Override
    public boolean isValid() {
        return Bank.isCached() && OwnedItems.contains(ItemID.UNCHARGED_TRIDENT) && ScriptSettings.getSettingsData().staffMode == StaffMode.TRIDENT;
    }

    @Override
    public int onLoop() {
        if (Bank.isOpen()) {
            Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (Equipment.contains(ItemID.UNCHARGED_TRIDENT)) {
            if (Inventory.isFull()) {
                new BankAllInventoryEvent().execute();
                return ReactionGenerator.getNormal();
            }

            Equipment.unequip(EquipmentSlot.WEAPON);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.canEnterInput()) {
            Logger.info("should type");
            Keyboard.type("1m", true);
            return ReactionGenerator.getNormal();
        }

        Item coins = Inventory.get(ItemID.COINS_995);
//        Item trident = Inventory.get(getUnchargedTrident());
        Item trident = Inventory.get(ItemID.UNCHARGED_TRIDENT);
        if (coins != null && trident != null) {
            coins.useOn(trident);
            Sleep.sleepUntil(Dialogues::canEnterInput, 2400);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }

//    private boolean hasChargedTrident() {
////        if (Equipment.contains(ItemID.TRIDENT_OF_THE_SWAMP_E)) return true;
//        if (Equipment.contains(ItemID.TRIDENT_OF_THE_SWAMP)) return true;
//        return false;
//    }
//
//    private int getUnchargedTrident() {
//        if (OwnedItems.contains(ItemID.UNCHARGED_TOXIC_TRIDENT) || Equipment.contains(ItemID.UNCHARGED_TRIDENT))
//            return ItemID.UNCHARGED_TOXIC_TRIDENT;
//        return 0;
//    }
}
