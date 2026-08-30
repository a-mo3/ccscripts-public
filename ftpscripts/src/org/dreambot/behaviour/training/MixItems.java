package org.dreambot.behaviour.training;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class MixItems extends Fractal {
    private final int productId;
    private final int supplyId; // the item to sleep until you have none of
    private final int mixWith; // the item to sleep until you have none of


    public MixItems(Supplier<Boolean> acceptCondition, int productId, int supplyId, int mixWith) {
        super(acceptCondition);
        this.productId = productId;
        this.supplyId = supplyId;
        this.mixWith = mixWith;
    }

    public MixItems(Skill skill, int startLevel, int productId, int supplyId, int mixWith) {
        super(() -> Skills.getRealLevel(skill) >= startLevel);
        this.productId = productId;
        this.supplyId = supplyId;
        this.mixWith = mixWith;
    }

    @Override
    public int onLoop() {
        if (ItemProcessing.isOpen()) {
            ItemProcessing.makeAll(productId);
            Sleep.sleepUntil(() -> !Inventory.contains(supplyId), () -> Players.getLocal().isAnimating(), 1600, 100);
            return ReactionGenerator.getNormal();
        }

        if (Widgets.isOpen()) Widgets.closeAll();
        Inventory.combine(mixWith, supplyId);
        Sleep.sleepUntil(ItemProcessing::isOpen, 2000);
        return ReactionGenerator.getQuick();
    }
}
