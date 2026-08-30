package org.dreambot.behaviour.misc.tickcombat.decisions;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Accessors(chain = true)
public class GenericTickEat extends TickDecision {
    // Food & its heal
    Map<Integer, Integer> foodHealMap = new HashMap<>();
    // food that eaten AFTER (so not pizzas) sharks (so karam, and soon the hallibut)
    Map<Integer, Integer> comboFoodMap = new HashMap<>();
    @Setter
    int minMissingHP = 20;
    @Setter
    BooleanSupplier allowEat = () -> true;


    public GenericTickEat(Map<Integer, Integer> foodHealMap) {
        this.foodHealMap = foodHealMap;
    }

    public GenericTickEat() {
        this.foodHealMap.put(ItemID.SHARK, 20);
        this.foodHealMap.put(ItemID.LOBSTER, 14);
        this.foodHealMap.put(ItemID.SWORDFISH, 14);
        this.foodHealMap.put(ItemID.JUG_OF_WINE, 8);
        this.foodHealMap.put(ItemID.BLIGHTED_MANTA_RAY, 22);
        this.foodHealMap.put(ItemID.BLIGHTED_ANGLERFISH, 10);
        this.foodHealMap.put(ItemID.BLIGHTED_KARAMBWAN, 45);
        // todo add other food here i guess

        this.comboFoodMap.put(ItemID.COOKED_KARAMBWAN, 18);
        this.comboFoodMap.put(ItemID.BLIGHTED_KARAMBWAN, 18);
    }

    public static int lastAteTick = 0;

    @Override
    public boolean evaluate() {
        if (allowEat != null && !allowEat.getAsBoolean()) return false;
        if (lastAteTick != 0 && Client.getGameTick() - lastAteTick < 3) {
            log("cant eat, on delay");
        }
        int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (missingHP < minMissingHP) {
            return false;
        }

        Item food = Inventory.get(x -> foodHealMap.getOrDefault(x.getId(), 2000) <= missingHP);
        if (food == null) {
            log("No appropriate food in inv");
            return false;
        }

        if (Inventory.isItemSelected()) Inventory.deselect();

        Item combo = Inventory.get(x -> (comboFoodMap.getOrDefault(x.getId(), 2000) + foodHealMap.get(food.getId())) <= missingHP);
        if (combo != null) {
            log("Valid combo " + combo.getName());
            food.interact();
            Sleep.sleep(30, 70);
            combo.interact();
            lastAteTick = Client.getGameTick();
            return false;
        }

        food.interact();
        lastAteTick = Client.getGameTick();

        return false;
    }
}
