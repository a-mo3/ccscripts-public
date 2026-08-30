package org.dreambot.behaviour.antelopes.sunfire;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class BaitSunlight extends Fractal {
    public BaitSunlight() {
    }

    @Override
    public boolean isValid() {
        return GameObjects.closest("Spiked pit") != null && Inventory.contains(ItemID.LOGS);
    }

    @Override
    public int onLoop() {
        Character antelope = Players.getLocal().getCharacterInteractingWithMe();

        if (antelope == null || antelope.distance() > 5) {
            // todo change the tile when i add new spots
            NPC antelopeNpc = NPCs.closest(x -> x.getName().equals("Sunlight antelope") && x.getInteractingCharacter() == null, new Tile(1740, 3001));
            Logger.info("Interacting with " + antelopeNpc);
            if (antelopeNpc != null && antelopeNpc.interact("Tease")) {
                Sleep.sleepUntil(() -> Players.getLocal().getCharacterInteractingWithMe() != null, 1600);
                return ReactionGenerator.getNormal() + 300;
            }
            return ReactionGenerator.getNormal() + 300;
        }

        Item stamina = ItemVariants.STAMINA_POTION.getItem();
        if (Walking.getRunEnergy() < 10 && stamina != null && stamina.interact("Drink")) {
            Sleep.sleepUntil(() -> Walking.getRunEnergy() > 20, 2400);
        }

        if (antelope.hasAction("Dismiss")) {
            antelope.interact("Dismiss");
            Logger.info("Dismissing random");
            Sleep.sleepUntil(() -> !antelope.exists(), 2400);
            return ReactionGenerator.getNormal();
        }

        if (antelope instanceof Player) {
            Logger.warn("We are being fucked with");
            return ReactionGenerator.getNormal();
        }

        GameObject trap = GameObjects.closest("Spiked pit");
        if (trap != null && trap.interact("Jump")) {
            Sleep.sleepUntil(() -> GameObjects.closest("Collapsed trap") != null, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
