package org.dreambot.behaviour.method.scurrius;

import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GoToScurrius extends Fractal {
    // loadout is more like mode at this stage
    public GoToScurrius(Supplier<Boolean> acceptCondition, ScurriusMode scurriusLoadout) {
        super(acceptCondition);
        setSimpleName("Scurris gear up");

        this.prependLogic = () -> {
            // turn off all prayers here
            PrayerUtils.disable(Prayer.values());
            return false;
        };
        this.inventoryLoadout = scurriusLoadout.inventoryLoadout;
        this.equipmentLoadout = scurriusLoadout.equipmentLoadout;
    }

    public static final Area SCURRIUS_GRATE = new Area(3275, 9871, 3277, 9869);

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            if (Dialogues.canEnterInput()) {
                log("Input");
                Keyboard.type("1 ", true);
            }
            Dialog.solve("Yes,", "Yes", "");
            return ReactionGenerator.getNormal();
        }

        if (!SCURRIUS_GRATE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(SCURRIUS_GRATE);
            return ReactionGenerator.getNormal();
        }

        GameObject bars = GameObjects.closest(x -> x.hasAction("Climb-through (private)"));
        if (bars != null) {
            log("Enter priv scurrius");
            bars.interact("Climb-through (private)");
            Sleep.sleepUntil(Client::isDynamicRegion, 5400);
        }

        return ReactionGenerator.getNormal();
    }
}
