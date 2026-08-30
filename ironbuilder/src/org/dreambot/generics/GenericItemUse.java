package org.dreambot.generics;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Handles using an item, eating, drinking, equipping
 * or using item on an entity
 * <p>
 * todo consider combing this and inventory combination
 */
@Setter
@Accessors(chain = true)
public class GenericItemUse extends IronFractal {
    final Supplier<Entity> entitySupplier;
    final Supplier<Item> itemSupplier;

    Area location = null;
    Condition sleepCondition = () -> true;
    Condition resetCondition = () -> false;
    int sleepTime = 2400;
    int polling = 200;


    public GenericItemUse(BooleanSupplier acceptCondition, Supplier<Entity> entitySupplier, String itemName) {
        super(acceptCondition);
        setSimpleName("Use on");
        this.entitySupplier = entitySupplier;
        this.itemSupplier = () -> Inventory.get(itemName);
    }

    public GenericItemUse(BooleanSupplier acceptCondition, Supplier<Entity> entitySupplier, int itemId) {
        super(acceptCondition);
        setSimpleName("Use on");
        this.entitySupplier = entitySupplier;
        this.itemSupplier = () -> Inventory.get(itemId);
    }

    /**
     * a null entity supplier will just use the item
     *
     * @param acceptCondition accept condition
     * @param itemName        the item to use, equip, drink, eat w/e its default action
     */
    public GenericItemUse(BooleanSupplier acceptCondition, String itemName) {
        super(acceptCondition);
        setSimpleName("Use on");
        this.entitySupplier = null;
        this.itemSupplier = () -> Inventory.get(itemName);
    }

    /**
     * Use an item if you have it, accept condition is defaulted to inventory contains
     *
     * @param itemName the item to use, equip, drink, eat w/e its default action
     */
    public GenericItemUse(String itemName) {
        super(() -> Inventory.contains(itemName));
        setSimpleName("Use " + itemName);
        this.entitySupplier = null;
        this.itemSupplier = () -> Inventory.get(itemName);
    }

    public GenericItemUse(int itemId) {
        super(() -> Inventory.contains(itemId));
        setSimpleName("Use " + itemId);
        this.entitySupplier = null;
        this.itemSupplier = () -> Inventory.get(itemId);
    }

    @Override
    protected int onLoop() {
        if (location != null && !location.contains(Players.getLocal())) {
            log("Heading to location");
            if (Walking.shouldWalk()) Walking.walk(location);
            return sleep();
        }

        Item i = itemSupplier.get();
        if (entitySupplier == null) {
            log("Use only i " + i);
            if (i != null) Inventory.interact(i);
            return sleep();
        }

        Entity e = entitySupplier.get();
        log(i + " On " + e);
        if (i != null && e != null) {
            i.useOn(e);
            Sleep.sleepUntil(sleepCondition, resetCondition, sleepTime, polling);
        }
        return sleep();
    }
}
