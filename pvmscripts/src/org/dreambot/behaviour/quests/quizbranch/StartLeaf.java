package org.dreambot.behaviour.quests.quizbranch;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class StartLeaf extends Fractal {
    public StartLeaf() {
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.VARROCK_TELEPORT, 1, 5);
    }

    @Override
    public boolean isValid() {
        return PlayerSettings.getBitValue(3671) != 1 && Skills.getRealLevel(Skill.HUNTER) < 9;
    }

    private static final Area QUIZ_ENTRY = new Area(3253, 3447, 3258, 3455, 0);
    public static final Area QUIZ_AREA = new Area(1714, 4994, 1805, 4923);


    @Override
    public int onLoop() {
        if (QUIZ_AREA.contains(Players.getLocal())) {
            if (Dialogues.inDialogue()) {
                Dialog.solve("Sure thing");
                Logger.info("Started quiz by talking to Orlando Smith");
                return ReactionGenerator.getNormal();
            }

            NPC orlando = NPCs.closest("Orlando Smith");
            if (orlando != null) {
                orlando.interact("Talk-to");
                Sleep.sleepUntil(Dialogues::inDialogue, 3000);
            } else {
                if (Walking.shouldWalk(6)) Walking.walk(QUIZ_ENTRY.getCenter());
            }
            return ReactionGenerator.getNormal();
        }

        if (!QUIZ_ENTRY.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(QUIZ_ENTRY.getCenter());
            return ReactionGenerator.getNormal();
        }

        GameObject stairs = GameObjects.closest("Stairs");
        if (stairs != null) {
            stairs.interact("Walk-down");
            Sleep.sleepUntil(() -> QUIZ_AREA.contains(Players.getLocal()), 2000);
        }
        return ReactionGenerator.getNormal();
    }
}
