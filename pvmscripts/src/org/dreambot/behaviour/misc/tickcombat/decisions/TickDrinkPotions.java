package org.dreambot.behaviour.misc.tickcombat.decisions;

import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.loadout.ItemVariant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class TickDrinkPotions extends TickDecision {
    final HashMap<ItemVariant, BooleanSupplier> potions;

    public TickDrinkPotions addPotion(ItemVariant potion, BooleanSupplier condition) {
        potions.put(potion, condition);
        return this;
    }

    public TickDrinkPotions() {
        super();
        this.potions = new HashMap<>();
        setSimpleName("Tick Drink Pot");
    }

    public TickDrinkPotions(HashMap<ItemVariant, BooleanSupplier> potions) {
        super();
        this.potions = potions;
        setSimpleName("Tick Drink Pot");
    }

    @Override
    public boolean evaluate() {
        if (potions == null || potions.isEmpty()) return false;
        for (Map.Entry<ItemVariant, BooleanSupplier> entry : potions.entrySet()) {
            Item potion = entry.getKey().getItem();
            if (entry.getValue().getAsBoolean()) {
                if (potion != null) {
                    log("Sip potion " + potion.getName());
                    if (Widgets.isOpen()) Widgets.closeAll();
                    potion.interact();
                    return false;
                } else {
                    log("Should sip but dont own one " + entry.getKey().getBaseId());
                }
            }
        }
        return false;
    }
}
