package org.dreambot.behaviour.method.gwd.nex.frozendoor;

import org.dreambot.api.script.frameworks.treebranch.Branch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.OwnedItems;

import java.util.function.Supplier;

public class FrozenDoorBranch extends Fractal {
    public FrozenDoorBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        setSimpleName("Frozen door");
        addChildren(
                new UnlockFrozenDoor(() -> OwnedItems.contains(ItemID.FROZEN_KEY_26356)).setSimpleName("Open door"),
                new MakeFrozenKey().setSimpleName("Make Frozen key"),
                new KillAllBossesBranch(() -> true)
        );
    }
}
