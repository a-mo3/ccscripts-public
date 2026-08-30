package org.dreambot.behaviour.antelopes.sunfire;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class GotoSunlightAntelopes extends Fractal {
    Area FALCONRY_AREA = new Area(2363, 3621, 2394, 3572);
    public GotoSunlightAntelopes() {
        setInventoryLoadout(new InventoryLoadout()
                .addItem(ItemID.KNIFE, 1)
                .addItem(ItemID.CHISEL, 1)
                .addItem(ItemID.IRON_AXE, 1)
                .addItem(ItemID.VARROCK_TELEPORT, 5)
                .setRefill(50)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.VARROCK_TELEPORT))
                .addItem(ItemID.TEASING_STICK)
                .addItem(ItemID.JUG_OF_WINE, 8)
                .setRefill(200)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.JUG_OF_WINE))
                .addItem(ItemVariants.STAMINA_POTION, 1, 4)
                .setRefill(50)
                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
        );

        setEquipmentLoadout(new EquipmentLoadout()
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
        );

        this.appendLogic = () -> {
            if (FALCONRY_AREA.contains(Players.getLocal())) {
                Magic.castSpell(Normal.HOME_TELEPORT);
                Sleep.sleepUntil(() -> !FALCONRY_AREA.contains(Players.getLocal()), 30_0000);
                return true;
            }
            return false;
        };
    }

    public static final Area SUNFIRE_AREA = new Area(1728, 3024, 1762, 2994, 0);

    @Override
    public boolean isValid() {
        if (SUNFIRE_AREA.contains(Players.getLocal()) && Walking.getRunEnergy() > 10 && !Walking.isRunEnabled()) {
            Walking.toggleRun();
        }
        return !SUNFIRE_AREA.contains(Players.getLocal()) || !inventoryLoadout.isFulfilled();
    }

    @Override
    public int onLoop() {
        if (Walking.shouldWalk()) Walking.walk(SUNFIRE_AREA);
        return ReactionGenerator.getNormal();
    }
}
