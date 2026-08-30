package org.dreambot.behaviour.tutorial.combattutorial;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class RangeRatLeaf extends Fractal {
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        switch (MyVarps.getTutVarp()) {
            case 470: // talk to combat instructor but you need to leave the pit first cant just interact
                NPC combatTrainer = NPCs.closest("Combat Instructor");
                if (combatTrainer == null) {
                    Logger.info("null combat instructor");
                    return ReactionGenerator.getNormal();
                }

                if (!combatTrainer.canReach()) {
                    if (Walking.shouldWalk(6)) Walking.walk(combatTrainer.getTile());
                    return ReactionGenerator.getNormal();
                }

                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }

                if (combatTrainer.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 5000);
                }
                break;
            case 480: // kill rat.
            case 490:
                // YOLO!
                Inventory.interact("Shortbow", "Wield");
                Inventory.interact("Bronze arrow", "Wield");
                // maybe check if u have bow here
                if (!Players.getLocal().isInCombat()) {
                    if (Players.getLocal().distance(new Tile(3104, 9509)) < 3) {
                        if (Walking.shouldWalk(6)) Walking.walk(Players.getLocal().getTile().translate(6, 0));
                        return ReactionGenerator.getNormal();
                    }
                    NPC rat = NPCs.closest(x -> x.getName().equals("Giant rat") && x.canAttack());
                    if (rat != null && !rat.isInCombat()) {
                        rat.interact("Attack");
                    }
                }
                break;
        }
        return ReactionGenerator.getNormal();
    }
}
