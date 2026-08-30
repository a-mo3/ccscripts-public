package org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.magnet;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.training.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class MakeMagnet extends Fractal {
    final Area RIMMINGTON_MINE = new Area(2972, 3241, 2976, 3235);

    @Override
    public boolean isValid() {
        return PaidQuest.ANIMAL_MAGNETISM.getConfigValue() == 140;
    }

    @Override
    public int onLoop() {
        if (!Inventory.contains(ItemID.BAR_MAGNET)) {
            if (!RIMMINGTON_MINE.contains(Players.getLocal())) {
                if (Walking.shouldWalk(8)) Walking.walk(RIMMINGTON_MINE.getCenter());
                return ReactionGenerator.getNormal();
            }
            // you have to face north to make the magnet
            // todo is not facing north, idk what orientation is
            if (Players.getLocal().getOrientation() != 1024) {
                if (Walking.shouldWalk(8)) Walking.walk(Players.getLocal().getTile().translate(0, 1));
                return ReactionGenerator.getNormal();
            }

            Item iron = Inventory.get(ItemID.SELECTED_IRON);
            Item hammer = Inventory.get(ItemID.HAMMER);
            if (hammer.useOn(iron)) {
                Sleep.sleepUntil(() -> !Inventory.contains(ItemID.SELECTED_IRON), 2400);
            }
            return ReactionGenerator.getNormal();
        }

        if (!SpecialWalker.enterAvasRoom()) return ReactionGenerator.getNormal();

        if (Dialogues.inDialogue()) {
            Dialog.solve();
            //Dialogues.solve();
            return ReactionGenerator.getNormal();
        }

        NPC ava = NPCs.closest("Ava");
        if (ava != null && ava.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
