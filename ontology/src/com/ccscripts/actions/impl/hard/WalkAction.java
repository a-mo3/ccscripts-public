package com.ccscripts.actions.impl.hard;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.ActionType;
import com.ccscripts.model.TileWrapper;
import lombok.Getter;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;

public class WalkAction extends AbstractAction {
    @Getter
    final TileWrapper destination;
    @Getter
    final TileWrapper current;
    final boolean isRunning;

    public WalkAction(Tile destination, Tile current) {
        super(ActionType.WALK);
        this.destination = new TileWrapper(destination);
        this.current = new TileWrapper(current);
        isRunning = Walking.isRunEnabled();
    }

    public Tile destToTile() {
        return destination.unwrap();
    }
}
