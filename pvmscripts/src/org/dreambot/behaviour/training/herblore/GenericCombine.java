package org.dreambot.behaviour.training.herblore;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

@Accessors(chain = true)
public class GenericCombine extends Fractal {
    final int itemA;
    final int itemB;
    @Setter
    int result;
    Condition sleepUntil;
    Condition resetCondition;
    int timeout = 2400;
    int reset = 100;

    public GenericCombine setSleepUntil(Condition sleepUntil, Condition resetCondition, int timeout, int reset) {
        this.sleepUntil = sleepUntil;
        this.resetCondition = resetCondition;
        this.timeout = timeout;
        this.reset = reset;
        return this;
    }

    public GenericCombine(int targetLvl, int itemA, int itemB, int itemAQuantity, int itemBQuantity, int itemARestock, int itemBRestock) {
        super(() -> Skills.getRealLevel(Skill.HERBLORE) < targetLvl);
        this.itemA = itemA;
        this.itemB = itemB;
        sleepUntil = () -> !Inventory.contains(itemB, itemA);
        resetCondition = () -> Players.getLocal().isAnimating();

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(itemA, 1, itemAQuantity)
                .setRefill(itemARestock)
                .addItem(itemB, 1, itemBQuantity)
                .setRefill(itemBRestock)
        ;
    }

    @Override
    public int onLoop() {
        Item a = Inventory.get(itemA);
        Item b = Inventory.get(itemB);

        if (ItemProcessing.isOpen()) {
            ItemProcessing.makeAll(result);
            Sleep.sleepUntil(sleepUntil, resetCondition, timeout, reset);
            return ReactionGenerator.getNormal();
        }

        if (a != null && b != null) {
            if (Widgets.isOpen()) {
                Widgets.closeAll();
                return ReactionGenerator.getQuick();
            }
            a.useOn(b);
            Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
