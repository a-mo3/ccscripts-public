package org.dreambot.behaviour.training.mining;


import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

@Accessors(chain = true)
public class GenericMineLeaf extends Fractal {
    public static final Supplier<Integer> appropriatePickaxe = () -> {
        int mineLvl = Skills.getRealLevel(Skill.MINING);
        if (mineLvl >= 41) return ItemID.RUNE_PICKAXE;
        if (mineLvl >= 21) return ItemID.MITHRIL_PICKAXE;
        return ItemID.BRONZE_PICKAXE;
    };

    public static final InventoryLoadout PICKAXE_LOADOUT = new InventoryLoadout()
//            .addItem(ItemVariant.RING_OF_WEALTH, 1).setStrict(false)
            .addItem(appropriatePickaxe, 1);

    final String rock;
    final Area area;
    final InventoryLoadout loadout = new InventoryLoadout()
            .addItem(appropriatePickaxe, 1)
            .setStrict(true);
    final Supplier<GameObject> rockSupplier;

    @Setter
    boolean shouldBank = false;

    public GenericMineLeaf(Supplier<Boolean> acceptCondition, String rock, Area area) {
        super(acceptCondition);
        this.rock = rock;
        this.area = area;
        // different loadout used in super fractal because super will mule request
        this.rockSupplier = null;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(appropriatePickaxe, 1)
                .strictIgnore(ItemID.COPPER_ORE, ItemID.IRON_ORE, ItemID.CLAY, ItemID.COAL, ItemID.UNCUT_EMERALD, ItemID.UNCUT_RUBY)
                .setStrictSupplier(Inventory::isFull);
    }

    public GenericMineLeaf(Supplier<Boolean> acceptCondition, Supplier<GameObject> rockSupplier, Area area) {
        super(acceptCondition);
        this.rock = "Coal rocks";
        this.area = area;
        this.rockSupplier = rockSupplier;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(appropriatePickaxe, 1);
    }


    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            log("Full inv");
            if (!shouldBank) {
                Inventory.dropAll(x -> x.getName().contains("ore") || x.getName().contains("uncut"));
                Inventory.dropAll(ItemID.COAL);
                return ReactionGenerator.getNormal();
            }

            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }

        if (!area.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(area.getCenter());
            return ReactionGenerator.getNormal();
        }

        Logger.info("Finding rock " + rock);
        GameObject rockObj = GameObjects.closest(rock);
        if (rockSupplier != null) rockObj = rockSupplier.get();
        Logger.info("rock " + rockObj);
        if (rockObj != null && area.contains(rockObj) && !Players.getLocal().isAnimating()) {
            rockObj.interact("Mine");
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 1300);
        }
        return ReactionGenerator.getNormal();
    }
}
