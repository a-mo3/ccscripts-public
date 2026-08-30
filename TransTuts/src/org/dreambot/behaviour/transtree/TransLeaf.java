package org.dreambot.behaviour.transtree;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.framework.Leaf;

public class TransLeaf extends Leaf {
    Area transLeaf = new Area(3118, 3412, 3134, 3399);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        if (Equipment.contains(27145)) {
            return -1;
        }

        if (Inventory.contains("Flower crown")) {
            Inventory.interact("Flower crown", "Change");
            Sleep.sleep(1200);
            WidgetChild trannyCrown = Widgets.getMatchingWidget(x -> x.getText().contains("Transgender") && x.hasAction("Select"));
            Logger.log(trannyCrown + " HERE");
            trannyCrown.interact("Select");
        }

        if (Inventory.contains(27145)) {
            if (Inventory.interact(27145, "Wear")) {
                return 600;
            }
        }

        if (!transLeaf.contains(Players.getLocal()) && Walking.shouldWalk(2)) {
            Walking.walk(transLeaf.getCenter());
            return 300;
        }

        NPC gilbert = NPCs.closest("Gilbert");
        if (gilbert != null && gilbert.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 8000);
            Dialogues.chooseFirstOptionContaining("flower crown");
        }
        return 1200;
    }
}
