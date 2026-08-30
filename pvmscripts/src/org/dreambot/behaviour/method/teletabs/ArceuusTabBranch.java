package org.dreambot.behaviour.method.teletabs;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.behaviour.misc.GetArceuusSpellbook;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;

import java.util.function.Supplier;

public class ArceuusTabBranch extends Fractal {
    public ArceuusTabBranch(Supplier<Boolean> acceptCondition, ArceuusTeleTabOption option) {
        super(acceptCondition);
        setSimpleName("Make tabs " + option.name());
        addChildren(
                new GetArceuusSpellbook().setSimpleName("Set spellbook"),
                new MineArceuusEssence(() -> !Inventory.isFull())
                        .setSimpleName("Mine essence"),
                new MakeDarkEssence(() -> Inventory.contains(ItemID.DENSE_ESSENCE_BLOCK)),
                new MakeArceuusTeleTab(() -> true)
        );
    }
}
