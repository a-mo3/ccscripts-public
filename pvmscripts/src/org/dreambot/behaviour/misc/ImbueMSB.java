package org.dreambot.behaviour.misc;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class ImbueMSB extends Fractal {
    public ImbueMSB(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Imbue MSB");

        inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.MAGIC_SHORTBOW)
                .addItem(ItemID.MAGIC_SHORTBOW_SCROLL);
    }

    public ImbueMSB() {
        super(() -> Bank.isCached() && Skill.RANGED.getLevel() >= 50 && !OwnedItems.contains(ItemID.MAGIC_SHORTBOW_I));
        setSimpleName("Imbue MSB");

        inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.MAGIC_SHORTBOW)
                .addItem(ItemID.MAGIC_SHORTBOW_SCROLL);
    }


    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            log("Solve yes dialogue");
            Dialog.solve("Yes");
            return ReactionGenerator.getNormal();
        }
        if (Widgets.isOpen()) Widgets.closeAll();

        Inventory.combine(ItemID.MAGIC_SHORTBOW, ItemID.MAGIC_SHORTBOW_SCROLL);
        return ReactionGenerator.getNormal();
    }
}
