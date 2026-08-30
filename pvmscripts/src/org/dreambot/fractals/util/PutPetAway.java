package org.dreambot.fractals.util;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class PutPetAway extends Fractal {
    static List<String> petNames = Arrays.asList(
            "Huberte",
            "Rock Golem",
            "Rocky",
            "baby chinchompa",
            "Scurry",
            "Vet'ion Jr.",
            "Smolcano"
    );

    public PutPetAway() {
        super(() -> Players.getLocal().getCharactersInteractingWithMe().stream().anyMatch(x -> petNames.contains(x.getName())));
        setSimpleName("Pick up pet");
    }

    @Override
    public int onLoop() {
        log("Picking up pet");
        if (Inventory.isFull()) {
            // we will bank all here because we cant know that dropping something cheap will be okay, can be done anywhere
            log("Inventory is full bank it all");
            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }
        Character pet = Players.getLocal().getCharactersInteractingWithMe().stream().filter(x -> petNames.contains(x.getName()))
                .findAny()
                .orElse(null);
        if (pet == null) {
            log("Somehow pet is null");
            return ReactionGenerator.getNormal();
        }
        pet.interact("Pick-up");
        return ReactionGenerator.getNormal();
    }
}
