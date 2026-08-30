package org.dreambot.behaviour.method.barrows;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class EquipEvent extends AbstractResponseEvent<EquipEvent.Response> {

    final List<Integer> ids;
    int loops;
    final int MAX_LOOPS = 6;

    public EquipEvent(List<Integer> ids) {
        this.ids = ids;
    }

    enum Response {
        NO_MORE_EQUIPMENT,
        TOO_MANY_LOOPS,
        INVALID_INPUT
    }

    List<String> validActions = Arrays.asList(
            "Wear",
            "Wield",
            "Equip"
    );

    @Override
    public int onLoop() {
        if (ids.stream().anyMatch(x -> !Inventory.contains(x) && !Equipment.contains(x))) {
            setResponse(Response.INVALID_INPUT);
            return ReactionGenerator.getNormal();
        }

        loops++;
        boolean equipped = false;
        for (Integer id : ids) {
            if (Inventory.contains(id)) {
                Log.info("Equipping id");
                Item i = Inventory.get(id);
                String action = Arrays.stream(i.getActions()).filter(x -> validActions.contains(x)).findFirst().orElse(null);
                if (action == null) {
                    Logger.info("No valid action " + Arrays.toString(i.getActions()));
                }
                Inventory.interact(id);
                equipped = true;
            }
        }
        if (equipped) return ReactionGenerator.getNormal();

        if (loops > MAX_LOOPS) {
            setResponse(Response.TOO_MANY_LOOPS);
            return ReactionGenerator.getNormal();
        }

        setResponse(Response.NO_MORE_EQUIPMENT);
        return ReactionGenerator.getNormal();
    }
}
