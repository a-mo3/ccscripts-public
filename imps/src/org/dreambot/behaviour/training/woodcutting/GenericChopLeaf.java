package org.dreambot.behaviour.training.woodcutting;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.LoadoutExecutor;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

@Accessors(chain = true)
public class GenericChopLeaf extends Fractal {
    private Area treeArea;
    private Filter<GameObject> treeFilter;
    // idk what the actual action is and if its always the same
    @Setter
    private String action = "Chop down";
    @Setter
    private boolean bankLogs = false; // if true bank if false drop all
    @Setter
    private String logName = "log";

    public GenericChopLeaf(Supplier<Boolean> acceptCondition, Area treeArea, Filter<GameObject> treeSupplier) {
        super(acceptCondition);
        this.treeArea = treeArea;
        this.treeFilter = treeSupplier;
    }

    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            if (bankLogs) {
                LoadoutExecutor.execInvLoadout(new InventoryLoadout().setStrict(true)
                );
            } else {
                Inventory.dropAll(x -> x.getName().toLowerCase().contains(logName));
            }
            return ReactionGenerator.getNormal();
        }

        if (!treeArea.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(treeArea.getRandomTile());
            return ReactionGenerator.getNormal();
        }

        GameObject tree = GameObjects.closest(x -> treeFilter.match(x) && treeArea.contains(x) && x.hasAction("Chop down"));
        Logger.info("Tree: " + tree);
        if (tree != null && tree.interact(action)) {
            Sleep.sleepUntil(() -> Inventory.isFull() || !Client.isLoggedIn(), () -> Players.getLocal().isAnimating(), 2400, 100);
        }
        return ReactionGenerator.getNormal();
    }
}
