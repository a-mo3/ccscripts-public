package com.ccscripts.actions;

import com.ccscripts.model.TileWrapper;
import com.ccscripts.model.TimestampedPoint;
import lombok.Getter;
import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.map.Region;

/**
 * An abstract action should contain all the data to reproduce the action, this script records a history or these actions.
 */
@Getter
public abstract class AbstractAction {
    private final ActionType type;
    private final int region;
    private final long timestamp;
    private final int gameTick;
    // for context
    private final TileWrapper onTile;
    final TimestampedPoint mousePoint;

    protected AbstractAction(ActionType type) {
        this.type = type;
        this.onTile = new TileWrapper(Players.getLocal().getServerTile());
        this.region = Players.getLocal().getRegionId();
        this.timestamp = System.currentTimeMillis();
        this.gameTick = Client.getGameTick();
        this.mousePoint = new TimestampedPoint(Mouse.getPosition());
    }
}
