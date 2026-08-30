package org.dreambot.behaviour.method.teletabs;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.ArceuusTeleTabSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class MineArceuusEssence extends Fractal {
    Area ESSENCE_MINE = new Area(1760, 3862, 1767, 3843);

    public MineArceuusEssence(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_PICKAXE) // todo make this adapt to level and consider atk
        ;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.CHISEL)
                .addItem(ItemID.ARCEUUS_LIBRARY_TELEPORT)
                .setEnabledCondition(() -> BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50)
        ;

        for (int id : SettingsRepository.findInstanceOf(new ArceuusTeleTabSettings()).option.runeReqs) {
            inventoryLoadout.addItem(id, 50, 1500);
        }
    }

    @Override
    public int onLoop() {
        if (!ESSENCE_MINE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(ESSENCE_MINE.getCenter());
            return ReactionGenerator.getNormal();
        }

        GameObject runeStone = GameObjects.closest("Dense runestone");
        if (runeStone != null) {
            runeStone.interact();
            Sleep.sleepUntil(Inventory::isFull,
                    () -> Players.getLocal().isAnimating(),
                    6400,
                    200
            );
        }

        return ReactionGenerator.getNormal();
    }
}
