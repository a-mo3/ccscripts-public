package org.dreambot.behaviour.eaglespeak;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.HashMap;
import java.util.function.Supplier;

public class BronzePuzzleSolver extends Fractal {
    public BronzePuzzleSolver(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        setSimpleName("Bronze Puzzle");
    }

    final Area PUZZLE_ENTRANCE = new Area(1987, 4948, 1988, 4952, 3);

    // varbit, itemid
    HashMap<Integer, Integer> winchMap = new HashMap<Integer, Integer>() {{
        put(3101, 19976);
        put(3102, 19977);
        put(3103, 19978);
        put(3104, 19979);
    }};

    private final int STONE_PEDESTAL_19984 = 19984;
    private final int PEDESTAL_19980 = 19980;

    @Override
    public int onLoop() {
        GameObject featherPed = GameObjects.closest(STONE_PEDESTAL_19984);
        if (featherPed != null && featherPed.interact("Take-from")) {
            Sleep.sleepUntil(() -> Inventory.contains("Bronze feather"), 2400);
            return ReactionGenerator.getNormal();
        }

        // enter puzzle instance
        if (!Client.isDynamicRegion()) {
            // enter the cave from buying from asyff
            if (CaveBranch.enterCave.get()) return ReactionGenerator.getLong();

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


        // check you arent in the silver leaf instance
        GameObject stonePedestal = GameObjects.closest("Stone pedestal");
        if (stonePedestal != null) {
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

        GameObject ped = GameObjects.closest(PEDESTAL_19980);
        if (ped != null && ped.interact("Take-from")) {
            Sleep.sleep(3400); // 🧙
            return ReactionGenerator.getNormal();
        }

        for (java.util.Map.Entry<Integer, Integer> winch : winchMap.entrySet()) {
            if (PlayerSettings.getBitValue(winch.getKey()) == 0) {
                GameObject winchObj = GameObjects.closest(winch.getValue());
                if (winchObj != null && winchObj.interact("Operate")) {
                    Sleep.sleepUntil(() -> PlayerSettings.getBitValue(winch.getKey()) != 0, 2400);
                }
                return ReactionGenerator.getNormal();
            }
        }
        return ReactionGenerator.getNormal();
    }
}
