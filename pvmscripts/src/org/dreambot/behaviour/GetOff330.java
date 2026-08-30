package org.dreambot.behaviour;

import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.fractals.Fractal;

public class GetOff330 extends Fractal {
    private final Filter<World> worldFilter;

    public static final Filter<World> MEMBERS_WORLD_FILTER = x -> x.isNormal()
            && x.getWorld() != 330
            && x.getWorld() != 401
            && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel();

    public GetOff330(Filter<World> worldFilter) {
        this.worldFilter = worldFilter;
        setSimpleName("Get off w330");
    }


    // you cant be on 330 if you arent members, why ever do anything other than members?
    public GetOff330() {
        this.worldFilter = MEMBERS_WORLD_FILTER;
        setSimpleName("Get off w330");
    }

    @Override
    public boolean isValid() {
        return Worlds.getCurrentWorld() == 330;
    }

    @Override
    public int onLoop() {
        if (Widgets.isOpen()) {
            Widgets.closeAll();
        }

        int world = Worlds.getRandomWorld(worldFilter).getWorld();
        log("Hop world " + world);
        WorldHopper.hopWorld(world);
        return super.onLoop();
    }
}
