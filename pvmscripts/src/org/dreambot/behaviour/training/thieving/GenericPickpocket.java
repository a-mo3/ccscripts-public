package org.dreambot.behaviour.training.thieving;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Accessors(chain = true)
public class GenericPickpocket extends Fractal {
    final Supplier<NPC> npcSupplier;
    final Area area;
    @Setter
    int sleepTime = 1200;
    List<Integer> food = Arrays.asList(ItemID.SHARK);
    @Setter
    boolean hopWhenNoTarget;

    public GenericPickpocket setFood(Integer... food) {
        this.food = Arrays.asList(food);
        return this;
    }

    public GenericPickpocket(Supplier<Boolean> acceptCondition, Supplier<NPC> npcSupplier, Area area) {
        super(acceptCondition);
        this.npcSupplier = npcSupplier;
        this.area = area;
    }

    @Override
    public int onLoop() {
        // safe guard for being attacked by a guard
        if (Players.getLocal().isInCombat()) {
            Character attack = Players.getLocal().getCharacterInteractingWithMe();
            if (attack != null && attack.getName().toLowerCase().contains("guard")) {
                log("Attacked by guard");
                if (Walking.shouldWalk()) {
                    Walking.walk(BankLocation.DRAYNOR);
                }
                return ReactionGenerator.getNormal();
            }
        }

        if (!area.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(area);
            return ReactionGenerator.getNormal();
        }

        NPC target = npcSupplier.get();
        if (hopWhenNoTarget && target == null) {
            log("No target, hopping world");
            WorldHopper.hopWorld(Worlds.getRandomWorld(GetOff330.MEMBERS_WORLD_FILTER));
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isItemSelected()) Inventory.deselect();
        if (target != null && target.interact("Pickpocket")) {
            Sleep.sleepUntil(() -> !Players.getLocal().isMoving(), 2400);
            Sleep.sleep(sleepTime);
        }
        return ReactionGenerator.getNormal();
    }
}
