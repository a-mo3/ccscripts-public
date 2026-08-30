package org.dreambot.behaviour.dragons;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.function.Supplier;

@Data
@AllArgsConstructor
public class DragonLocation {
    Tile safeTile;
    Supplier<NPC> dragonSupplier;
    String name;
}
