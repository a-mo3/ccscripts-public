package org.dreambot.behaviour.method.orbers;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class AirOrb extends Fractal {
    final Area AIR_OBELISK = new Area(3082, 3575, 3092, 3565);

    public AirOrb() {
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(8)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
        ;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.UNPOWERED_ORB, 1, 26).setRefill(1200)
                .addItem(ItemID.COSMIC_RUNE, 75, 75).setRefill(2000)
                .setEnabledCondition(() -> !Combat.isInWild())
        ;
    }

    @Override
    public int onLoop() {
        if (!AIR_OBELISK.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(AIR_OBELISK);
            return ReactionGenerator.getNormal();
        }

        if (ItemProcessing.isOpen()) {
            ItemProcessing.makeAll(ItemID.AIR_ORB);
            Sleep.sleepUntil(() -> !Inventory.contains(ItemID.UNPOWERED_ORB),
                    () -> Players.getLocal().isAnimating(),
                    1800,
                    100
            );
            return ReactionGenerator.getNormal();
        }

        GameObject obelisk = GameObjects.closest("Obelisk of Air");
        if (obelisk != null) {
            Magic.castSpellOn(Normal.CHARGE_AIR_ORB, obelisk);
            Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
        }
        return ReactionGenerator.getNormal();

    }
}
