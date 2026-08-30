package org.dreambot.behaviour.training.thieving;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class StealFromFruitStall extends Fractal {
    public StealFromFruitStall(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }


    Tile[] STALL_TILES = new Tile[]{
            new Tile(1800, 3608, 0),
            new Tile(1796, 3608, 0),

    };
    int random = Calculations.random(0, 2);

    Timer competition = new Timer(15_000);

    @Override
    public int onLoop() {
        if (Inventory.contains("Enchanted scroll")) {
            log("Enchanted scroll in inv, bank all");
            new BankAllInventoryEvent().execute();
        }

        if (Inventory.isFull()) {
            if (Inventory.contains(x -> x.getAmount() * x.getLivePrice() < 1500)) {
                log("Drop all Fruit");
                Inventory.dropAll(x -> x.getAmount() * x.getLivePrice() < 1500);
                return ReactionGenerator.getNormal();
            }
            log("No cheap items in inventory, banking all");
            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }

//        if (Dialogues.inDialogue()) {
//            Dialog.solve("Goodbye", "");
//            return ReactionGenerator.getNormal();
//        }

        if (!STALL_TILES[random].equals(Players.getLocal().getTile())) {
            log("Walk to tea stall");
            if (Walking.shouldWalk()) Walking.walkExact(STALL_TILES[random]);
            return ReactionGenerator.getNormal();
        }

        if (Players.closest(x -> !x.equals(Players.getLocal()) && x.distance() <= 1) != null) {
            if (competition.finished()) {
                log("Someone has been next to us for 15 seconds, hopping world");
                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isMembers() && x.isNormal() && x.getWorld() != 401 && x.getMinimumLevel() < Skills.getTotalLevel()));
                return 15_000;
            }
        } else {
            competition.reset();
        }

        GameObject stall = GameObjects.closest(x -> x.distance() < 2 && x.hasAction("Steal-from"));
        if (stall != null) {
            stall.interact("Steal-from");
            Sleep.sleep(600);
        }
        return ReactionGenerator.getQuick();
    }
}
