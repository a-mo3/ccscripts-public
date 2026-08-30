package org.dreambot.behaviour.method.gwd.zammy;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

// nvm needs while guthix sleeps.
public class MakeScorchingBow extends Fractal {
    public MakeScorchingBow(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Make scorching bow");

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.MAGIC_LONGBOW_U)
                .addItem(ItemID.TORMENTED_SYNAPSE)
        ;
    }

    @Override
    public int onLoop() {

        if (Widgets.isOpen()) {
            log("Close widgets");
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }


        log("Make bow");
        Inventory.combine(ItemID.MAGIC_LONGBOW_U, ItemID.TORMENTED_SYNAPSE);
        return ReactionGenerator.getNormal();
    }
}
