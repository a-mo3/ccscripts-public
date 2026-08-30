package org.dreambot.behaviour.eaglespeak;


import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public class GoldPuzzleSolver extends Fractal {
    public GoldPuzzleSolver(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.paintArraySupplier = () -> new String[]{
                "Gold Puzzle State: " + PlayerSettings.getBitValue(PUZZLE_STATE_VARBIT)
        };
        setSimpleName("Gold Puzzle");
    }

    public static final int PUZZLE_STATE_VARBIT = 3089;
    final Area PUZZLE_ENTRANCE = new Area(2019, 4983, 2022, 4981, 3);

    @Override
    public int onLoop() {
        // im not even gonna try and be smart i wanna play some csgo rn

        // enter puzzle instance
        if (!Client.isDynamicRegion()) {
            // enter the cave from buying from asyff
            if (CaveBranch.enterCave.get()) return ReactionGenerator.getNormal();

            if (!PUZZLE_ENTRANCE.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(PUZZLE_ENTRANCE.getCenter());
                return ReactionGenerator.getNormal();
            }

            GameObject tunnel = GameObjects.closest("Tunnel");
            if (tunnel != null && tunnel.interact("Enter")) {
                Sleep.sleepUntil(Client::isDynamicRegion, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.contains(ItemID.ODD_BIRD_SEED)) {
            GameObject feedHolder = GameObjects.closest("Birdseed holder");
            // dont care this is based.
            if (feedHolder != null) {
                for (int i = 0; i < 12; i++) {
                    feedHolder.interact("Take-from");
                    Sleep.sleepTicks(2);
                }
            }
            return ReactionGenerator.getNormal();
        }

        switch (PlayerSettings.getBitValue(PUZZLE_STATE_VARBIT)) {
            // im not gonna use ObjectId here because im gonna copy paste this to dreambot
            // 🧙
            case 0:
            case 396:
                solveLever(19948);
                break;
            case 4:
                solveFeeder(19939);
                break;
            case 260:
                solveFeeder(19938);
                break;
            case 388:
                solveLever(19949);
                break;
            case 392:
            case 411:
                solveFeeder(19937);
                break;
            case 424:
                solveLever(19946);
                break;
            case 425:
                solveFeeder(19936);
                break;
            case 441:
                solveLever(19947);
                break;
            case 443:
                solveFeeder(19941);
                break;
            case 475:
                GameObject pedestal = GameObjects.closest(19950);
                if (pedestal != null) {
                    if (pedestal.distance() > 4) {
                        if (Walking.shouldWalk()) Walking.walk(pedestal.getSurroundingArea(3).getRandomTile());
                        return ReactionGenerator.getNormal();
                    }

                    pedestal.interact("Take-from");
                    Sleep.sleepUntil(() -> Inventory.contains(ItemID.GOLDEN_FEATHER), 2400);
                }
        }

        return ReactionGenerator.getNormal();
    }

    void solveLever(int leverId) {
        GameObject lever = GameObjects.closest(leverId);
        if (lever == null) {
            Logger.info("Could not find lever with id " + leverId);
            return;
        }

        if (lever.distance() > 3) {
            Logger.info("Walking to lever: " + leverId + " - " + PlayerSettings.getBitValue(PUZZLE_STATE_VARBIT));
            if (Walking.shouldWalk()) Walking.walk(lever.getSurroundingArea(2).getRandomTile());
            return;
        }

        Logger.info("Actions " + Arrays.toString(lever.getActions()));
        lever.interact(Arrays.stream(lever.getActions()).filter(Objects::nonNull).findFirst().orElse(""));
        Sleep.sleep(4400); // 🧙 cant be fucked.
    }

    void solveFeeder(int feederId) {
        GameObject feeder = GameObjects.closest(feederId);
        if (feeder == null) {
            Logger.info("Could not find feeder with id " + feederId);
            return;
        }

        if (feeder.distance() > 3) {
            Logger.info("Walking to feeder: " + feeder + " - " + PlayerSettings.getBitValue(PUZZLE_STATE_VARBIT));
            if (Walking.shouldWalk(6)) Walking.walk(feeder.getSurroundingArea(2).getRandomTile());
            return;
        }

        Item seed = Inventory.get(ItemID.ODD_BIRD_SEED);
        Logger.info("Seed: " + seed);
        if (seed != null) {
            Logger.info("Using seed on feeder: " + feeder);
            seed.useOn(feeder);
        }
        Sleep.sleep(4400); // 🧙 cant be fucked.
    }
}
