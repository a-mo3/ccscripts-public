package org.dreambot.behaviour.training.hunter;

import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.NpcID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class EnsureLeftFalconry extends Fractal {
    static Area FALCONRY_ARENA = new Area(
            new Tile(2370, 3575, 0),
            new Tile(2371, 3572, 0),
            new Tile(2378, 3573, 0),
            new Tile(2381, 3575, 0),
            new Tile(2382, 3580, 0),
            new Tile(2385, 3583, 0),
            new Tile(2387, 3588, 0),
            new Tile(2389, 3588, 0),
            new Tile(2391, 3585, 0),
            new Tile(2394, 3586, 0),
            new Tile(2395, 3589, 0),
            new Tile(2393, 3595, 0),
            new Tile(2390, 3603, 0),
            new Tile(2386, 3605, 0),
            new Tile(2384, 3605, 0),
            new Tile(2385, 3610, 0),
            new Tile(2377, 3622, 0),
            new Tile(2367, 3621, 0),
            new Tile(2367, 3604, 0),
            new Tile(2364, 3601, 0),
            new Tile(2364, 3593, 0),
            new Tile(2366, 3592, 0),
            new Tile(2364, 3585, 0),
            new Tile(2363, 3575, 0));
    Area FALCONRY_GUY = new Area(2368, 3613, 2376, 3604);

    public EnsureLeftFalconry() {
        super(() -> FALCONRY_ARENA.contains(Players.getLocal()));
    }

    @Override
    public int onLoop() {
        // todo if you have the falcon give it back to matthias
        if (Equipment.contains(ItemID.FALCONERS_GLOVE_10024, ItemID.FALCONERS_GLOVE)) {
            log("We have falcon, quick falcon it away");
            NPC falconryGuy = NPCs.closest("Matthias");
            if (falconryGuy != null) {
                log("Quick falcon");
                falconryGuy.interact("Quick-falcon");
                return ReactionGenerator.getLong();
            } else {
                log("Get to matthias");
                if (Walking.shouldWalk()) Walking.walk(FALCONRY_GUY);
            }
            return ReactionGenerator.getNormal();
        }

        if (Walking.shouldWalk()) Walking.walk(BankLocation.LUMBRIDGE);
        return ReactionGenerator.getNormal();
    }
}
