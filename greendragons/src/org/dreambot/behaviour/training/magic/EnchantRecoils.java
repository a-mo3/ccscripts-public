package org.dreambot.behaviour.training.magic;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

public class EnchantRecoils extends Fractal {
    public EnchantRecoils() {
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SAPPHIRE_RING, 1, 25).setRefill(1600)
                .addItem(ItemID.COSMIC_RUNE, 1, 1600)
        ;

        this.appendLogic = () -> {
            if (!Inventory.contains(ItemID.SAPPHIRE_RING) && Inventory.contains(ItemID.RING_OF_RECOIL)) {
                if (!Bank.isOpen()) {
                    BankUtil.openClosest();
                    return true;
                }

                Bank.depositAll(ItemID.RING_OF_RECOIL);
                return true;
            }
            return false;
        };

        this.paintArraySupplier = () -> new String[]{
                "Magic: " + Skills.getRealLevel(Skill.MAGIC)
        };

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER)
        ;
    }

    @Override
    public boolean isValid() {
        return Skills.getRealLevel(Skill.MAGIC) < 27;
    }

    @Override
    public int onLoop() {
        if (GrandExchange.isOpen() || Bank.isOpen()) {
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }

        int coilCount = Inventory.count(ItemID.RING_OF_RECOIL);
        Magic.castSpellOn(Normal.LEVEL_1_ENCHANT, Inventory.get(ItemID.SAPPHIRE_RING));
        Sleep.sleepUntil(() -> coilCount < Inventory.count(ItemID.RING_OF_RECOIL), 1800, 300);
        return ReactionGenerator.getNormal();
    }
}
