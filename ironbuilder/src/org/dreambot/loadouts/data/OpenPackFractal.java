package org.dreambot.loadouts.data;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.GenericItemUse;
import org.dreambot.utility.OwnedItems;

public class OpenPackFractal extends IronFractal {

    /**
     *
     * @param packId item id for the pack we're opening
     * @param store  should be a transact at store fractal to get the pack, with a coin method attached
     */
    public OpenPackFractal(int packId, IronFractal store, int result, int resultQuantity) {
        super(() -> OwnedItems.count(result) < resultQuantity);
        addChildren(
                new GenericItemUse(packId),
                store
        );

        setSimpleName("Open pack " + new Item(packId, 0).getName());
    }
}
