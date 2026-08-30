package org.dreambot.behaviour.training.magic;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class AlchSomething extends Fractal {
    public AlchSomething(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }


    @Override
    public int onLoop() {
        Item alchable = Inventory.get(ItemID.RUNE_ARROW);
        if (alchable != null) {
            Logger.info("Alching " + alchable);
            if (Widgets.isOpen()) {
                Widgets.closeAll();
            }

            if (alchable.getSlot() != 16 && Inventory.isSlotEmpty(16)) {
                // caused a bug im so sorry hashtag you where right.
                Inventory.drag(alchable, 16);
                return ReactionGenerator.getNormal();
            }

            Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, alchable);
            return 2600;
        }
        return ReactionGenerator.getNormal();
    }
}
