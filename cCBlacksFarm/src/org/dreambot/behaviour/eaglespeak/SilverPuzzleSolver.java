package org.dreambot.behaviour.eaglespeak;


import org.dreambot.api.Client;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class SilverPuzzleSolver extends Fractal {
    public SilverPuzzleSolver(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.paintArraySupplier = () -> new String[]{
                "Silver puzzle state: " + PlayerSettings.getBitValue(3099)
        };
        setSimpleName("Silver puzzle");
    }

    final Area PUZZLE_ENTRANCE = new Area(1986, 4973, 1989, 4971, 3);
    final int SILVER_PUZZLE_VARBIT = 3099;

    @Override
    public int onLoop() {
        // enter puzzle instance
        if (!Client.isDynamicRegion()) {
            // enter the cave from buying from asyff
            if (CaveBranch.enterCave.get()) return ReactionGenerator.getNormal();

            if (!PUZZLE_ENTRANCE.contains(Players.getLocal())) {
                if (Walking.shouldWalk(6)) Walking.walk(PUZZLE_ENTRANCE.getCenter());
                return ReactionGenerator.getNormal();
            }

            GameObject tunnel = GameObjects.closest("Tunnel");
            if (tunnel != null && tunnel.interact("Enter")) {
                Sleep.sleepUntil(Client::isDynamicRegion, 2400);
            }
            return ReactionGenerator.getNormal();
        }


        // check you arent in the gold leaf instance
        GameObject feedHolder = GameObjects.closest("Birdseed holder");
        if (feedHolder != null) {
            GameObject tunnel = GameObjects.closest("Tunnel");
            if (tunnel != null) {
                if (tunnel.distance() > 5) {
                    if (Walking.shouldWalk(6)) Walking.walk(tunnel.getSurroundingArea(3).getRandomTile());
                    return ReactionGenerator.getNormal();
                }

                tunnel.interact("Enter");
                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 2400);
            }
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("Taunt");
            return ReactionGenerator.getNormal();
        }

        switch (PlayerSettings.getBitValue(SILVER_PUZZLE_VARBIT)) {
            case 0:
                search(() -> GameObjects.closest("Stone pedestal"), "Inspect");
                break;
            case 1:
                search(() -> GameObjects.closest(19458), "Inspect"); // 🧙
                break;
            case 2:
                search(() -> GameObjects.closest(19461), "Inspect"); // 🧙
                break;
            case 3:
                search(() -> GameObjects.closest("Opening"), "Inspect");
                break;
            case 4:
                search(() -> NPCs.closest("Kebbit"), "Threaten");
                break;
            case 5:
                search(() -> GroundItems.closest("Silver feather"), "Take");
        }
        return ReactionGenerator.getNormal();
    }

    void search(Supplier<Entity> entitySupplier, String action) {
        Entity entity = entitySupplier.get();
        if (entity != null) {
            log("Entity found " + entity);
            if (entity.distance() > 5) {
                log("Entity walk");
                if (Walking.shouldWalk(6)) Walking.walk(entity.getSurroundingArea(3).getRandomTile());
                return;
            }
            log("Entity interact");
            entity.interact(action);
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        log("Failed to find entity");
    }
}
