package org.dreambot.behaviour.training.crafting;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GenericSmelting extends Fractal {
    final static Area EDGEVILLE_FURNACE = new Area(3089, 3504, 3111, 3494);
    private final int productId;
    private final int supplyId; // the item to sleep until you have none of


    public GenericSmelting(Supplier<Boolean> acceptCondition, int productId, int supplyId) {
        super(acceptCondition);
        this.productId = productId;
        this.supplyId = supplyId;
    }

    public GenericSmelting(int startLevel, int productId, int supplyId) {
        super(() -> Skills.getRealLevel(Skill.CRAFTING) >= startLevel);
        this.productId = productId;
        this.supplyId = supplyId;
    }

    private static final int SMITHING_WIDGET_PARENT = 446;

    @Override
    public int onLoop() {
        if (!EDGEVILLE_FURNACE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(EDGEVILLE_FURNACE);
            return ReactionGenerator.getNormal();
        }

        WidgetChild item = Widgets.get(SMITHING_WIDGET_PARENT, x -> x.getItemId() == productId);
        log("WIdget " + item);
        if (item != null && item.isVisible()) {
            log("Interact with widget");
            item.interact();
            Sleep.sleepUntil(() -> !Inventory.contains(supplyId), () -> Players.getLocal().isAnimating(), 2600, 100);
            return ReactionGenerator.getNormal();
        }


        GameObject furnace = GameObjects.closest("Furnace");
        if (furnace != null) {
            furnace.interact("Smelt");
            Sleep.sleepUntil(ItemProcessing::isOpen, () -> Players.getLocal().isMoving(), 1600, 100);
        }
        return ReactionGenerator.getNormal();
    }
}
