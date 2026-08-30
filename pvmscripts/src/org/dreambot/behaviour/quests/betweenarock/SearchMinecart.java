package org.dreambot.behaviour.quests.betweenarock;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class SearchMinecart extends Fractal implements ChatListener {


    public SearchMinecart(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Search minecarts");
        Client.getInstance().addEventListener(this);
    }

    List<Tile> minecartLocations = Arrays.asList(
            new Tile(3017, 9846),
            new Tile(3017, 9845),
            new Tile(3020, 9817),
            new Tile(3020, 9819),
            new Tile(3025, 9821),
            new Tile(3041, 9820),
            new Tile(3041, 9819)
    );

    int counter = 0;

    @Override
    public int onLoop() {
        if (!Bank.isCached()) {
            if (Walking.shouldWalk()) {
                if (Bank.open()) Bank.updateCache();
            }
            return ReactionGenerator.getNormal();
        }

        Tile currentCart = minecartLocations.get(counter % minecartLocations.size());
        if (currentCart.distance() > 5) {
            log("Walk to next cart " + currentCart);
            if (Walking.shouldWalk()) Walking.walk(currentCart);
            return ReactionGenerator.getNormal();
        }

        GameObject cart = GameObjects.getTopObjectOnTile(currentCart);
        if (cart == null) {
            log("Cant find object on tile " + currentCart);
            return ReactionGenerator.getNormal();
        }

        cart.interact("Search");
        Sleep.sleep(2500);

        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().contains("search the cart but find not")) {
            log("Empty cart");
            counter++;
        }
    }
}
