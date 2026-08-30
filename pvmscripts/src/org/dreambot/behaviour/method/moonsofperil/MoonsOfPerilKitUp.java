package org.dreambot.behaviour.method.moonsofperil;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * Kit up as in get the gear set you need, dharoks and switches, NOT in the kit for the fight (bream, moonlight pots)
 */
public class MoonsOfPerilKitUp extends Fractal {
    public static final Area BOSS_ROOMS = new Area(
            new Tile(1411, 9664, 0),
            new Tile(1418, 9709, 0),
            new Tile(1465, 9702, 0),
            new Tile(1467, 9657, 0),
            new Tile(1526, 9647, 0),
            new Tile(1504, 9605, 0),
            new Tile(1377, 9606, 0),
            new Tile(1365, 9652, 0)
    );

    public MoonsOfPerilKitUp(EquipmentLoadout equipmentLoadout,
                             InventoryLoadout inventoryLoadout) {
        super(() -> !Client.isInCutscene() && !Client.isDynamicRegion()
                && !BOSS_ROOMS.contains(Players.getLocal())
                && (!equipmentLoadout.isFulfilled() || (Players.getLocal().getX() > 2000 && !inventoryLoadout.isFulfilled()))
        );

        setPrependLogic(() -> {
            if ((Inventory.contains(ItemID.ABYSSAL_WHIP) && !Equipment.contains(ItemID.ABYSSAL_WHIP))
                    || (Inventory.contains(ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD) && !Equipment.contains(ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD))) {
                if (Widgets.isOpen()) {
                    Widgets.closeAll();
                }
                log("Equip whip and shield");
                Equipment.equip(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP);
                Equipment.equip(EquipmentSlot.SHIELD, ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD);
                return true;
            }

            return false;
        });
        setEquipmentLoadout(equipmentLoadout);
        setInventoryLoadout(inventoryLoadout);
        setSimpleName("Moons gear up");
    }
}
