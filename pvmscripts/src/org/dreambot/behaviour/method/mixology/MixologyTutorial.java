package org.dreambot.behaviour.method.mixology;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.NPCUtil;
import org.dreambot.settings.timing.ReactionGenerator;

public class MixologyTutorial extends Fractal {
    public static final Area MIXOLOGY_AREA = new Area(1390, 9317, 1400, 9308);

    public MixologyTutorial() {
        super(() -> PlayerSettings.getBitValue(11336) == 0);
    }

    @Override
    public int onLoop() {
        if (!MIXOLOGY_AREA.contains(Players.getLocal())) {
            log("Get to mixology area");
            if (Inventory.count(ItemID.COINS_995) < 5000 && Players.getLocal().getY() < 5000) {
                log("Getting coins for boat - " + new WithdrawLoadoutEvent(new InventoryLoadout()
                        .addItem(ItemID.COINS_995, 10_000), null)
                        .executed());
            }
            if (Walking.shouldWalk()) Walking.walk(MIXOLOGY_AREA);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("can work it out");
            return ReactionGenerator.getNormal();
        }

        log("Talk to jaunt");
        NPCUtil.interact("Supervisor Lalo", "Talk-to");

        return ReactionGenerator.getNormal();
    }
}
