package org.dreambot.behaviour.tutorial.smithingtutorial;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.behaviour.tutorial.TutorialIDs;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class MineLeaf extends Fractal {
    Area area = new Area(3077, 9510, 3086, 9496);


    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 330;
    }

    @Override
    public int onLoop() {
        switch (MyVarps.getTutVarp()) {
            case 250:
            case 260: // go talk to mine instuctor for pickaxe
            case 270:
                if (!area.contains(Players.getLocal())) {
                    if (Players.getLocal().getY() < 9000) {
                        GameObject ladder = GameObjects.closest("Ladder");
                        if (ladder != null && ladder.interact("Climb-down")) {
                            Sleep.sleepUntil(() -> Players.getLocal().getY() > 9000, 6000);
                        }
                        return ReactionGenerator.getNormal();
                    }

                    if (Walking.shouldWalk(4) && (Walking.getDestination() == null || !area.contains(Walking.getDestination()))) {
                        if (Walking.shouldWalk(6)) Walking.walk(area.getCenter());
                    }
                    return ReactionGenerator.getNormal();
                }

                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }

                NPC miningGuy = NPCs.closest("Mining Instructor");
                if (miningGuy != null && miningGuy.interact("Talk-to")) {
                    return ReactionGenerator.getNormal();
                }
                break;
            case 300: // mine tin
                GameObject tinRock = GameObjects.closest(r -> r.getName().contains("rocks") && r.getId() == 10080);
                if (tinRock != null && tinRock.interact("Mine")) {
                    Sleep.sleepUntil(() -> Inventory.contains(TutorialIDs.TIN_ORE), 8000);
                    return ReactionGenerator.getNormal();
                }
                break;
            case 310: // mine copper
                GameObject copperRock = GameObjects.closest(r -> r.getName().contains("rocks") && r.getId() == 10079);
                if (copperRock != null && copperRock.interact("Mine")) {
                    Sleep.sleepUntil(() -> Inventory.contains(TutorialIDs.COPPER_ORE), 8000);
                    return ReactionGenerator.getNormal();
                }
                break;
            case 320: // smelt bar
                GameObject furnace = GameObjects.closest("Furnace");
                if (furnace != null && furnace.interact("Use")) {
                    Sleep.sleepUntil(() -> !Inventory.contains(TutorialIDs.COPPER_ORE), 5000);
                }
                break;
        }
        return ReactionGenerator.getNormal();
    }
}
