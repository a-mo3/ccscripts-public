package org.dreambot.behaviour.impcatcher;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.NpcID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class ImpCatcher extends Fractal {
    public static final Tile START_TILE = new Tile(3104, 3162, 2);
    public static final Area START_AREA = START_TILE.getArea(3);


    public ImpCatcher() {
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.RED_BEAD)
                .addItem(ItemID.YELLOW_BEAD)
                .addItem(ItemID.BLACK_BEAD)
                .addItem(ItemID.WHITE_BEAD)
                .setStrict(true)
        ;
    }

    @Override
    public boolean isValid() {
        return !FreeQuest.IMP_CATCHER.isFinished() && Skills.getRealLevel(Skill.MAGIC) < 7;
    }

    @Override
    public int onLoop() {
        if (!START_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(START_TILE);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("Yes.", "Give me a quest please.");
            return ReactionGenerator.getNormal();
        }

        NPC mizgog = NPCs.closest(NpcID.WIZARD_MIZGOG);
        if (mizgog != null && mizgog.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 5000);
        }
        return ReactionGenerator.getNormal();
    }
}
