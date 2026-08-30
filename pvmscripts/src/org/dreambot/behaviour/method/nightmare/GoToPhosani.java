package org.dreambot.behaviour.method.nightmare;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.Fractal;
import org.dreambot.scriptdata.PhosaniSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GoToPhosani extends Fractal {
    public static final Area PNM_ENTRANCE = new Tile(3809, 9779, 1).getArea(2);

    public GoToPhosani(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.inventoryLoadout = SettingsRepository.findInstanceOf(new PhosaniSettings()).loadout.getLoadout();
        this.equipmentLoadout = SettingsRepository.findInstanceOf(new PhosaniSettings()).loadout.getEquipmentLoadout();
    }

    @Override
    public int onLoop() {
        // todo something for recharing at ferox
        if (!PNM_ENTRANCE.contains(Players.getLocal())) {
            if (Walking.getDestination() == null || Walking.getDestinationDistance() < 3) Walking.walk(PNM_ENTRANCE);
            return ReactionGenerator.getNormal();
        }
        // todo enter phosani
        return ReactionGenerator.getNormal();
    }
}
