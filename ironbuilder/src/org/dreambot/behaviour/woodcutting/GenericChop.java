package org.dreambot.behaviour.woodcutting;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Accessors(chain = true)
public class GenericChop extends IronFractal {
    // use something flexible like a filter or supplier so you can overload constructor and handle edge cases
    final Filter<GameObject> treeFiler;
    final Area area;

    @Setter
    Filter<Item> logFilter = x -> x.getName().contains("log"); // or w/e

    @Setter
    boolean shouldBank;

    public GenericChop(BooleanSupplier acceptCondition, String treeName, Area area) {
        super(acceptCondition);
        this.treeFiler = tree -> tree.getName().equals(treeName);
        this.area = area;
    }

    @Override
    protected int onLoop() {
        if (Inventory.isFull()) {
            if (!shouldBank) {
                log("Should not bank - dropping items");
                Inventory.dropAll(logFilter);
                return 600;
            }

            if (!Bank.isOpen()) {
                log("Going to bank");
                if (Walking.shouldWalk()) Bank.open(); // this handles walking as well btw
                return 600;
            }

            log("In bank deposit all");
            Bank.depositAll(logFilter);
            return 600;
        }

        if (!area.contains(Players.getLocal())) {
            log("Go to tree area");
            if (Walking.shouldWalk()) Walking.walk(area);
            return 600;
        }

        GameObject o = GameObjects.closest(treeFiler);
        if (o != null && !Players.getLocal().isAnimating()) {
            log("Chop " + o);
            o.interact();
        }
        return 600; // replace this with whatever
    }
}
