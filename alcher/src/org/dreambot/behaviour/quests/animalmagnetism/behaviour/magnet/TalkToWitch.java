package org.dreambot.behaviour.quests.animalmagnetism.behaviour.magnet;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class TalkToWitch extends Fractal {
    final Area WITCH = new Area(3096, 3373, 3104, 3364);
    InventoryLoadout requirements = new InventoryLoadout()
            .addItem(ItemID.MITHRIL_AXE)
            .addItem(ItemID.IRON_BAR, 5)
//            .addItem(ItemID.GHOSTSPEAK_AMULET)
            .addItem(ItemID.HAMMER)
            .addItem(ItemID.HARD_LEATHER)
            .addItem(ItemID.HOLY_SYMBOL)
            .addItem(ItemID.POLISHED_BUTTONS);

    @Override
    public boolean isValid() {
        int state = PaidQuest.ANIMAL_MAGNETISM.getConfigValue();
        return state == 120 || state == 130;
    }

    @Override
    public int onLoop() {
        if (!SpecialWalker.leaveAvasRoom()) {
            return ReactionGenerator.getNormal();
        }

        if (!requirements.isFulfilled()) {
            new WithdrawLoadoutEvent(requirements, null)
                    .setBuyRemainder(true)
                    .executed();
            return ReactionGenerator.getNormal();
        }

        if (!WITCH.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(WITCH.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
            //Dialogues.solve();
            return ReactionGenerator.getNormal();
        }

        NPC witch = NPCs.closest("Witch");
        if (witch != null && witch.interact("Talk-to")) {
            if (!witch.canReach()) {
                if (Walking.shouldWalk(8)) Walking.walk(witch.getTile());
                return ReactionGenerator.getNormal();
            }
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        return ReactionGenerator.getNormal();
    }

}
