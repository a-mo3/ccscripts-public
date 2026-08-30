package org.dreambot.behaviour.foundry;

import org.dreambot.behaviour.foundry.leafs.DoFoundryLeaf;
import org.dreambot.behaviour.foundry.leafs.FoundryReqsLeaf;
import org.dreambot.behaviour.foundry.leafs.GetPreformLeaf;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class FoundryBranch extends Fractal {
    // todo add paint with all the info for the minigame, heat, sword type etc...
    public FoundryBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        addChildren(
                new FoundryReqsLeaf().setSimpleName("Get reqs"),
                new GetPreformLeaf().setSimpleName("Get preform"),
                new DoFoundryLeaf().setSimpleName("doing foundry")
        );
    }
}
