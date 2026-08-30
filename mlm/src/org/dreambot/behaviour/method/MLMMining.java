package org.dreambot.behaviour.method;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

public class MLMMining extends Fractal {
    Area MINING_WEST = new Area(3728, 5671, 3738, 5656);
    Area MINING_SOUTH = new Area(3734, 5655, 3758, 5646);
    Area SOUTH_WEST_CORNER = new Area(3713, 5648, 3723, 5633);
    Area SOUTH_EAst_CORNER = new Area(3759, 5648, 3775, 5634);
    Area LARGER_INNER = new Area(3711, 5681, 3787, 5632);

    Area[] areas = new Area[]{
            MINING_SOUTH,
            MINING_WEST,
            SOUTH_WEST_CORNER,
            SOUTH_EAst_CORNER
    };

    Area selected = areas[ShuffleFractal.getLoginValue() % 2];


    public static final Area MLM_INNER = new Area(
            new Tile(3736, 5680, 0),
            new Tile(3732, 5676, 0),
            new Tile(3729, 5673, 0),
            new Tile(3729, 5660, 0),
            new Tile(3729, 5653, 0),
            new Tile(3733, 5650, 0),
            new Tile(3740, 5645, 0),
            new Tile(3753, 5646, 0),
            new Tile(3759, 5650, 0),
            new Tile(3760, 5656, 0),
            new Tile(3762, 5666, 0),
            new Tile(3760, 5673, 0),
            new Tile(3745, 5682, 0));
    public static final int ORE_IN_SACK_VARBIT = 5558;

    public MLMMining() {
        this.paintArraySupplier = () -> new String[]{
                "Ore in sack " + PlayerSettings.getBitValue(ORE_IN_SACK_VARBIT)
        };
    }

    @Override
    public boolean isValid() {
        return LARGER_INNER.contains(Players.getLocal());
    }

    @Override
    public int onLoop() {
        int oreInSack = PlayerSettings.getBitValue(ORE_IN_SACK_VARBIT);
        if (oreInSack > 54 && MLMMining.MLM_INNER.contains(Players.getLocal())) {
            Log.info("Empty event: " + new EmptyBagEvent().executed());
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isFull()) {
            Log.info("Deposit event: " + new DepositPaydirtEvent().executed());
            return ReactionGenerator.getNormal();
        }

        if (!selected.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(selected);
            return ReactionGenerator.getQuick();
        }

        GameObject vein = GameObjects.closest(x -> x.getName().equals("Ore vein") && x.canReach() && selected.contains(x));
        if (vein != null && vein.interact("Mine")) {
            Sleep.sleepUntil(Inventory::isFull, () -> Players.getLocal().isAnimating(), 4400, 100);
        }

        return ReactionGenerator.getNormal();
    }
}
