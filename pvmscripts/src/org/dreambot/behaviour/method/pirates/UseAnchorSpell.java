package org.dreambot.behaviour.method.pirates;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class UseAnchorSpell extends Fractal {
    public UseAnchorSpell() {
        super(() -> !hasUsedAnchorSpell());
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.TELEPORT_ANCHORING_SCROLL);
    }

    @Override
    public int onLoop() {
        if (Widgets.isOpen()) Widgets.closeAll();

        if (Dialogues.inDialogue()) {
            log("Solving dialogue");
            Dialog.solve("Become immune");
            return ReactionGenerator.getNormal();
        }

        Inventory.interact(ItemID.TELEPORT_ANCHORING_SCROLL);
        Sleep.sleepUntil(Dialogues::inDialogue, 4400);
        return ReactionGenerator.getNormal();
    }

    private static boolean hasUsedAnchorSpell() {
        return ((PlayerSettings.getConfig(4203) >> 3) & 0b1) > 0;
    }
}
