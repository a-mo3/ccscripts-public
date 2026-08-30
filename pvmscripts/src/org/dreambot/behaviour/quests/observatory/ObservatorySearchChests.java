package org.dreambot.behaviour.quests.observatory;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class ObservatorySearchChests extends Fractal implements ChatListener {
    public ObservatorySearchChests(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
    }

    final Tile[] chests = new Tile[]{
            new Tile(2351, 9361, 0),
            new Tile(2360, 9366, 0),
            new Tile(2364, 9355, 0),
            new Tile(2348, 9383, 0),
            new Tile(2356, 9380, 0),
            new Tile(2359, 9376, 0),
            new Tile(2335, 9374, 0),
            new Tile(2333, 9405, 0),
    };

    int chestCounter = 0;

    @Override
    public int onLoop() {
        // deal with being poisoned
        if (Combat.isPoisoned()) {
            Item superAP = ItemVariants.SUPER_ANTI_POISON.getItem();
            if (superAP != null) superAP.interact();
        }

        Tile chestTile = chests[chestCounter];

        GameObject chest = GameObjects.closest(x -> x.getTile().equals(chestTile));
        if (chest == null) {
            log("Walk to chest");
            if (Walking.shouldWalk()) Walking.walk(chestTile);
            return ReactionGenerator.getNormal();
        }

        log("Interact with chest");
        chest.interact();
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onGameMessage(Message message) {
        if (message.getMessage().contains("a poisonous spider")) {
            chestCounter++;
        }

    }
}
