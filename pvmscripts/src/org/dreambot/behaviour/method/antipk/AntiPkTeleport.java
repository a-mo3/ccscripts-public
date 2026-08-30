package org.dreambot.behaviour.method.antipk;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class AntiPkTeleport extends Fractal {
    public AntiPkTeleport(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        if (Equipment.contains(ItemVariants.AMULET_OF_GLORY.getIds())) {
            Logger.info("Equipment tp glory");
            Equipment.interact(EquipmentSlot.AMULET, "Edgeville");
        }

        Item glory = ItemVariants.AMULET_OF_GLORY.getItem();
        if (glory != null) {
            Logger.info("Inventory glory equip");
            glory.interact("Equip");
            return ReactionGenerator.getQuick();
        }

        Item rod = Equipment.get(x -> ItemVariants.RING_OF_DUELING.contains(x.getId()));
        if (rod != null && Combat.getWildernessLevel() <= 20) {
            log("TP to ferox");
            rod.interact("Ferox Enclave");
            return ReactionGenerator.getQuick();
        }


        return AntiPkLeaveBosses.leaveBosses();
    }
}
