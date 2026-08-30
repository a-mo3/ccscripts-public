package org.dreambot.behaviour.eaglespeak;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class SaveNickolaus extends Fractal {
    // you can likely make this part cleaner by making lostwalker contributes but i am doing this for dreambot so im gonna do fuck nigga shit
    public SaveNickolaus(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Save Nick");
    }

    @Override
    public int onLoop() {
        // wouldnt do this if i wasnt porting to dreambot after
        if (!Equipment.containsAll(ItemID.EAGLE_CAPE, ItemID.FAKE_BEAK)) {
            Inventory.interact(ItemID.EAGLE_CAPE, "Wear");
            Inventory.interact(ItemID.FAKE_BEAK, "Wear");
            return ReactionGenerator.getNormal();
        }

        GameObject stoneDoor = GameObjects.closest("Stone door");
        if (stoneDoor != null && stoneDoor.getX() >= Players.getLocal().getX()) {
            stoneDoor.interact("Open");
            Sleep.sleepUntil(() -> stoneDoor.getX() < Players.getLocal().getX(), 2400);
            return ReactionGenerator.getNormal();
        }

        NPC eagle = NPCs.closest("Eagle");
        if (eagle != null && eagle.getY() > Players.getLocal().getY()) {
            eagle.interact("Walk-past");
            Sleep.sleepUntil(() -> eagle.getY() < Players.getLocal().getY(), 2400);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        NPC nick = NPCs.closest("Nickolaus");
        if (nick != null && nick.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
