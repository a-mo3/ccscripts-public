package org.dreambot.behaviour.method.barrows.killbrothers;

import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.behaviour.method.barrows.BarrowsBrother;
import org.dreambot.behaviour.method.barrows.BarrowsKillBrothersBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.function.Supplier;

public class BarrowsGoToBrother extends Fractal {
    public BarrowsGoToBrother(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        BarrowsBrother brother = Arrays.stream(BarrowsBrother.values())
                .filter(x -> !x.hasKilled() && !x.equals(BarrowsKillBrothersBranch.tunnelBrother))
                .findFirst()
                .orElse(null);
        if (brother == null) {
            log("Failed to find appropriate brother");
            return ReactionGenerator.getNormal();
        }

        if (Players.getLocal().getZ() == 0 && Dialogues.inDialogue()) {
            String dialogue = Dialogues.getNPCDialogue();
            Logger.info(dialogue + "");
            if (Dialogues.canEnterInput()) {
                Keyboard.type("1", true);
                return ReactionGenerator.getNormal();
            }

            if (dialogue != null && dialogue.contains("But we talk first.")) {
                BarrowsFirstTimeDialogue.hasToDisableWarning = true;
            } else {
                Dialog.solve("I'll be back soon.");
            }
            return ReactionGenerator.getQuick();
        }

        if (!brother.tombArea.contains(Players.getLocal())) {
            PrayerUtils.disable(Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_MAGIC, Prayer.PROTECT_FROM_MISSILES);
            if (!Players.getLocal().getTile().equals(brother.digTile)) {
                log("Get onto dig tile");
                if (Walking.shouldWalk()) Walking.walkExact(brother.digTile);
                return ReactionGenerator.getNormal();
            }

            log("Dig into barrows tomb");
            Inventory.interact(ItemID.SPADE, "Dig");
            Sleep.sleepUntil(() -> Players.getLocal().getZ() != 0, 1200);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
