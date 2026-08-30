package org.dreambot.fractals.generic;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

@Accessors(chain = true)
public class UseOnFractal extends Fractal {
    private Area area;
    private final Supplier<Item> itemSupplier;
    private final Supplier<Entity> entitySupplier;
    @Setter
    private Condition sleepCondition;
    @Setter
    private int sleepTimeout = 2400;
    String[] dialogueOptions = null;

    public UseOnFractal(Supplier<Boolean> acceptCondition, Supplier<Item> itemSupplier, Supplier<Entity> entitySupplier) {
        super(acceptCondition);
        this.itemSupplier = itemSupplier;
        this.entitySupplier = entitySupplier;
    }

    @Override
    public int onLoop() {
        if (area != null && !area.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(area.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (dialogueOptions != null && Dialogues.inDialogue()) {
            Dialog.solve(dialogueOptions);
        }

        Item i = itemSupplier.get();
        Entity e = entitySupplier.get();
        if (e != null && i != null) {
            i.useOn(e);
            if (sleepCondition != null) Sleep.sleepUntil(sleepCondition, sleepTimeout);
        }

        return ReactionGenerator.getNormal();
    }

    public UseOnFractal setArea(Area area) {
        this.area = area;
        return this;
    }

    public UseOnFractal setArea(Tile tile) {
        this.area = tile.getArea(1);
        return this;
    }

    public UseOnFractal setDialogueOptions(String... options) {
        this.dialogueOptions = options;
        return this;
    }
}
