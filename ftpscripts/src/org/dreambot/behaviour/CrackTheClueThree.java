package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CrackTheClueThree extends Fractal {
    Tile DIG_TILE = new Tile(3190, 3165, 0);

    public CrackTheClueThree(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.prependLogic = () -> {
            if (true) {
                Logger.info(Worlds.all(x -> x.isNormal() && x.isF2P()).size());
                Logger.info(Worlds.all(World::isF2P).size());
            }
            return true;
        };

        // todo strict equipment
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SPADE)
                .addItem(ItemID.CHAOS_RUNE)
                .addItem(ItemID.FISH_FOOD)
                .addItem(ItemID.MIND_TIARA)
                .addItem(ItemID.PURPLE_DYE)
                .addItem(ItemID.RED_BEAD)
                .addItem(ItemID.STEEL_ARROW)
                .addItem(ItemID.AMULET_OF_DEFENCE)
                .addItem(ItemID.EMERALD_AMULET)
                .addItem(ItemID.HAMMER)
                .addItem(ItemID.PIE_SHELL)
                .addItem(ItemID.RAW_BEEF)
                .addItem(ItemID.REDBERRIES)
                .addItem(ItemID.STEEL_SCIMITAR)
                .addItem(ItemID.BLUE_DYE)
                .addItem(ItemID.FEATHER)
                .addItem(ItemID.IRON_CHAINBODY)
                .addItem(ItemID.POISONED_FISH_FOOD) // todo i have to make this
                .addItem(ItemID.RAW_RAT_MEAT)
                .addItem(ItemID.REDBERRY_PIE)
                .addItem(ItemID.TIN_ORE)
                .addItem(ItemID.BOWL)
                .addItem(ItemID.FIRE_TIARA)
                .addItem(ItemID.LEATHER_COWL)
                .addItem(ItemID.POTATO)
                .addItem(ItemID.RAW_SARDINE)
                .addItem(ItemID.SHRIMPS)
                .addItem(ItemID.WATER_RUNE)
        ;
    }

    @Override
    public int onLoop() {


        if (!DIG_TILE.equals(Players.getLocal().getTile())) {
            if (Walking.shouldWalk()) Walking.walkExact(DIG_TILE);
            return ReactionGenerator.getNormal();
        }

        Inventory.interact(ItemID.SPADE, "Dig");
        return ReactionGenerator.getNormal();
    }
}
