package org.dreambot.behaviour.quests.animalmagnetism.behaviour.device;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class MakeDeviceFinish extends Fractal {
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.POLISHED_BUTTONS)) {
            Item buttons = Inventory.get(ItemID.POLISHED_BUTTONS);
            Item pattern = Inventory.get(ItemID.A_PATTERN);
            if (buttons.useOn(pattern)) {
                Sleep.sleepUntil(() -> !Inventory.contains(ItemID.POLISHED_BUTTONS), 2400);
            }
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
            //Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        NPC ava = NPCs.closest("Ava");
        if (ava != null && ava.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
