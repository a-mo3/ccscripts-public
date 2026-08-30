package org.dreambot.behaviour.training.hunter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public class BoxTrapState {
    public enum Owner {
        UNKNOWN,
        ME,
        SOMEONE_ELSE
    }

    Tile tile;
    @Setter
    Owner owner;

    public GameObject getTrap() {
        return Arrays.stream(GameObjects.getObjectsOnTile(tile))
                .filter(x -> x.getName().toLowerCase().contains("box"))
                .findFirst()
                .orElse(null);
    }

    public boolean isMine() {
        return owner == Owner.ME;
    }
}
