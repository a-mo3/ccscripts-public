package org.dreambot.behaviour;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class KillImp extends Fractal {
    Area IMP_AREA_FALADOR = new Area(
            new Tile(3008, 3305, 0),
            new Tile(3013, 3306, 0),
            new Tile(3013, 3312, 0),
            new Tile(3020, 3312, 0),
            new Tile(3020, 3321, 0),
            new Tile(3008, 3320, 0),
            new Tile(3004, 3311, 0));

    public KillImp(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SALMON, 1, 6)
                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995) || Inventory.isFull());

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, CombatLoadouts.appropriateScimitar)
        ;
    }

    @Override
    public int onLoop() {
        if (!Combat.isAutoRetaliateOn()) {
            if (Widgets.isOpen()) {
                Logger.info("Closing widgets");
                Widgets.closeAll();
            }
            Logger.info("Toggle auto retaliate");
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getQuick();
        }

        if (Combat.getHealthPercent() < 50 && Inventory.interact(ItemID.SALMON)) {
            Logger.info("Eating");
            Sleep.sleepUntil(() -> Combat.getHealthPercent() > 50, 1400);
            return ReactionGenerator.getQuick();
        }

        GroundItem loot = GroundItems.closest(x -> IMP_AREA_FALADOR.contains(x) && LivePrices.get(x.getID()) >= 100 || x.getID() == ItemID.FIENDISH_ASHES);
        if (loot != null && loot.interact()) {
            Logger.info("Looting");
            Sleep.sleepUntil(() -> !loot.exists(), 2400);
            return ReactionGenerator.getQuick();
        }

        if (!IMP_AREA_FALADOR.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(IMP_AREA_FALADOR);
            return ReactionGenerator.getNormal();
        }

        if (Players.getLocal().isInCombat()) {
            Logger.info("In combat");
        }

        NPC imp = NPCs.closest(x -> x.getName().equals("Imp") && !x.isInCombat() && IMP_AREA_FALADOR.contains(x));
        if (imp != null) {
            Logger.info("Attack imp");
            imp.interact("Attack");
            Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 2400);
        } else {
            // todo consider hopping
        }
        return ReactionGenerator.getNormal();
    }
}
