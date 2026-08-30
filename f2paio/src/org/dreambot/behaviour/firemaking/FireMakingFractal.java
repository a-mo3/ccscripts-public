package org.dreambot.behaviour.firemaking;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class FireMakingFractal extends Fractal implements ChatListener {
    private final int logType; //todo make loadout for this
    private final Area RESET_AREA = new Area(3177, 3510, 3179, 3499);
    private boolean move;
    public static final int TINDERBOX = 590;

    public FireMakingFractal(Supplier<Boolean> acceptCondition, int logType) {
        super(acceptCondition);
        this.logType = logType;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(TINDERBOX, 1)
                .addItem(logType, 1, 27).setRefill(500);
    }

    @Override
    public int onLoop() {
        if (move) {
            if (RESET_AREA.contains(Players.getLocal())) {
                move = false;
                return ReactionGenerator.getLong();
            }
            if (Walking.shouldWalk()) Walking.walk(RESET_AREA);
            return ReactionGenerator.getLong();
        }

        Tile myTile = Players.getLocal().getTile();
        if (Inventory.combine(TINDERBOX, logType)) {
            Sleep.sleepUntil(() -> !myTile.equals(Players.getLocal().getTile()), 2800);
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (isValid()) {
            if (message.getMessage().equals("You can't light a fire here.")) {
                Logger.info("need to move");
                move = true;
            }
        }
    }
}
