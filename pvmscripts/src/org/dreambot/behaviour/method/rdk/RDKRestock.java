package org.dreambot.behaviour.method.rdk;

import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.scriptdata.RedDragonSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class RDKRestock extends Fractal {
    RedDragonSettings settings;

    public RDKRestock(Supplier<Boolean> acceptCondition, RedDragonSettings settings) {
        super(acceptCondition);
        this.settings = settings;
        this.equipmentLoadout = settings.loadout.equipmentLoadout;
        this.inventoryLoadout = settings.loadout.inventoryLoadout;
        this.acceptCondition = () -> acceptCondition.get() || !equipmentFulfilledCached();
    }

    Timer cache = new Timer(5000);
    Boolean isFulfilled = null;

    @Override
    public int onLoop() {
        log("Equipment " + equipmentLoadout.isFulfilled());
        return ReactionGenerator.getNormal();
    }

    private boolean equipmentFulfilledCached() {
        if (isFulfilled == null) {
            isFulfilled = this.equipmentLoadout.isFulfilled();
        }

        if (cache.finished()) {
            cache.reset();
            isFulfilled = this.equipmentLoadout.isFulfilled();
        }

        return isFulfilled;
    }
}
