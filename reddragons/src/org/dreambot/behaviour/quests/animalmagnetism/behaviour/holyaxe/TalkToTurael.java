package org.dreambot.behaviour.quests.animalmagnetism.behaviour.holyaxe;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class TalkToTurael extends Fractal {
    private static final Area TURAEL_HOUSE = new Area(2930, 3538, 2933, 3535);
    InventoryLoadout reqs = new InventoryLoadout()
            .addItem(ItemVariants.GAMES_NECKLACE)
            .addItem(ItemVariants.AMULET_OF_GLORY)
            .addItem(ItemID.HOLY_SYMBOL)
            .addItem(ItemID.MITHRIL_AXE)
            .addItem(ItemID.POLISHED_BUTTONS)
            .addItem(ItemID.HARD_LEATHER)
            .addItem(ItemID.HAMMER);

    @Override
    public boolean isValid() {
        int state = PaidQuest.ANIMAL_MAGNETISM.getConfigValue();
        return state == 160 || state == 170;
    }

    @Override
    public int onLoop() {
        if (!reqs.isFulfilled()) {
            new WithdrawLoadoutEvent(reqs, null)
                    .setBuyRemainder(true)
                    .executed();
            return ReactionGenerator.getNormal();
        }

        if (!TURAEL_HOUSE.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(TURAEL_HOUSE.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("I'm here about a quest.",
                    "Hello, I'm here about those trees again.",
                    "I'd love one, thanks.");
           /*
            Dialogues.solve(
                    "I'm here about a quest.",
                    "Hello, I'm here about those trees again.",
                    "I'd love one, thanks."

            );

            */
            return ReactionGenerator.getNormal();
        }

        NPC turael = NPCs.closest("Turael");
        if (turael != null && turael.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
