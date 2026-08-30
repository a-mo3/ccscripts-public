package org.dreambot.behaviour.training.magic;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class EnchantDueling extends Fractal {
    public EnchantDueling() {
        super(() -> Skills.getRealLevel(Skill.MAGIC) < 55);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.EMERALD_RING, 1, 25).setRefill(1600)
                .addItem(ItemID.COSMIC_RUNE, 1, 1600)
        ;

        this.paintArraySupplier = () -> new String[]{
                "Magic: " + Skills.getRealLevel(Skill.MAGIC)
        };

        this.prependLogic = () -> {
            int price = (int) (LivePrices.get(ItemID.EMERALD_RING) * 1600 * 1.5);
            if (OwnedItems.count(ItemID.EMERALD_RING) < 25
                    && OwnedItems.contains(ItemID.RING_OF_DUELING8)
                    && OwnedItems.count(ItemID.COINS_995) < price) {
                new SellAllEvent(ItemID.RING_OF_RECOIL, ItemID.SAPPHIRE_RING, ItemID.RING_OF_DUELING8).execute();
                return true;
            }

            if (!Inventory.contains(ItemID.EMERALD_RING) && Inventory.contains(ItemID.RING_OF_DUELING8)) {
                if (!Bank.isOpen()) {
                    log("Open closest");
                    BankUtil.openClosest();
                    return true;
                }

                Bank.depositAll(ItemID.RING_OF_DUELING8);
                return true;
            }
            return false;
        };

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR).setStrict(true)
        ;
    }

    public EnchantDueling(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.EMERALD_RING, 1, 25).setRefill(1600)
                .addItem(ItemID.COSMIC_RUNE, 1, 1600)
        ;

        this.paintArraySupplier = () -> new String[]{
                "Magic: " + Skills.getRealLevel(Skill.MAGIC)
        };

        this.prependLogic = () -> {
            int price = (int) (LivePrices.get(ItemID.EMERALD_RING) * 1600 * 1.5);
            if (OwnedItems.count(ItemID.EMERALD_RING) < 25
                    && OwnedItems.contains(ItemID.RING_OF_DUELING8)
                    && OwnedItems.count(ItemID.COINS_995) < price) {
                new SellAllEvent(ItemID.RING_OF_RECOIL, ItemID.SAPPHIRE_RING, ItemID.RING_OF_DUELING8).execute();
                return true;
            }

            if (!Inventory.contains(ItemID.EMERALD_RING) && Inventory.contains(ItemID.RING_OF_DUELING8)) {
                if (!Bank.isOpen()) {
                    log("Open closest");
                    BankUtil.openClosest();
                    return true;
                }

                Bank.depositAll(ItemID.RING_OF_DUELING8);
                return true;
            }
            return false;
        };

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
        ;
    }


    @Override
    public int onLoop() {
        if (GrandExchange.isOpen() || Bank.isOpen()) {
            log("Close widgets");
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }

        log("Enchant ring");
        int coilCount = Inventory.count(ItemID.RING_OF_DUELING8);
        Magic.castSpellOn(Normal.LEVEL_2_ENCHANT, Inventory.get(ItemID.EMERALD_RING));
        Sleep.sleepUntil(() -> coilCount < Inventory.count(ItemID.RING_OF_DUELING8), 1800, 300);
        return ReactionGenerator.getNormal();
    }
}
