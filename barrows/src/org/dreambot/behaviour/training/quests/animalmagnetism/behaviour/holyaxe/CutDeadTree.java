package org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.holyaxe;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.training.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class CutDeadTree extends Fractal {

    final Area DEAD_TREE = new Area(3107, 3350, 3113, 3344);

    @Override
    public boolean isValid() {
        int state = PaidQuest.ANIMAL_MAGNETISM.getConfigValue();
        return state == 150 || state == 180;
    }

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.UNDEAD_TWIGS)) {
            if (!SpecialWalker.enterAvasRoom()) return ReactionGenerator.getNormal();

            if (Dialogues.inDialogue()) {
                Dialog.solve();
                return ReactionGenerator.getNormal();
            }

            NPC ava = NPCs.closest("Ava");
            if (Widgets.isOpen()) Widgets.closeAll();
            if (ava != null && ava.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        if (!SpecialWalker.leaveAvasRoom()) return ReactionGenerator.getNormal();

        if (!DEAD_TREE.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(DEAD_TREE.getCenter());
            return ReactionGenerator.getNormal();
        }

        NPC undeadTree = NPCs.closest("Undead tree");
        if (undeadTree != null && undeadTree.interact("Chop")) {
            Sleep.sleep(600);
        }
        return ReactionGenerator.getNormal();
    }
}
