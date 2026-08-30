package org.dreambot.behaviour;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class Druids extends Fractal {
    public static final Area DRUIDS_WILDY = new Area(
            new Tile(3103, 9944, 0),
            new Tile(3108, 9944, 0),
            new Tile(3112, 9941, 0),
            new Tile(3112, 9936, 0),
            new Tile(3115, 9936, 0),
            new Tile(3117, 9933, 0),
            new Tile(3119, 9933, 0),
            new Tile(3121, 9931, 0),
            new Tile(3123, 9931, 0),
            new Tile(3123, 9927, 0),
            new Tile(3120, 9923, 0),
            new Tile(3112, 9923, 0),
            new Tile(3112, 9924, 0),
            new Tile(3109, 9926, 0),
            new Tile(3109, 9929, 0),
            new Tile(3107, 9932, 0),
            new Tile(3103, 9936, 0));

    public Druids(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.LOBSTER, 1, 8)
        ;
        this.equipmentLoadout = CombatLoadouts.SCIMITAR_LOADOUT_P2P;
    }

    @Override
    public int onLoop() {
        if (!DRUIDS_WILDY.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(DRUIDS_WILDY);
            return ReactionGenerator.getQuick();
        }

        return ReactionGenerator.getQuick();
    }
}
