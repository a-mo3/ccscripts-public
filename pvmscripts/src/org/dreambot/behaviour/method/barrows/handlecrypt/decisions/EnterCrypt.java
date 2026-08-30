package org.dreambot.behaviour.method.barrows.handlecrypt.decisions;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.barrows.BarrowsBrother;
import org.dreambot.behaviour.method.barrows.BarrowsKillBrothersBranch;
import org.dreambot.behaviour.method.barrows.BarrowsRestock;
import org.dreambot.behaviour.method.barrows.handlecrypt.HandleCryptBranch;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;

import java.util.Arrays;

public class EnterCrypt extends TickDecision {

    @Override
    public boolean evaluate() {
        if (HandleCryptBranch.BARROWS_CRYPT.contains(Players.getLocal())) {
            log("In crypt");
            return false;
        }

        // here we check if someone left barrows or died without a barrows teleport so they cant get back, if so force restock
        Player lp = Players.getLocal();
        if (!Inventory.contains(ItemID.BARROWS_TELEPORT) && lp.getZ() == 0 && lp.getX() < 3500) {
            log("Unexpected position going to restock");
            BarrowsRestock.forceRestock = true;
            return true;
        }

        BarrowsBrother currentlyIn = Arrays.stream(BarrowsBrother.values())
                .filter(x -> x.tombArea.contains(Players.getLocal()))
                .findAny()
                .orElse(null);
        if (currentlyIn == null) {
            log("Not currently in a tomb");
            if (BarrowsKillBrothersBranch.tunnelBrother == null) {
                log("Tunnel brother is null unexpectedly, will set to last alive brother.");
                BarrowsKillBrothersBranch.tunnelBrother = Arrays.stream(BarrowsBrother.values())
                        .filter(x -> !x.hasKilled())
                        .findFirst().orElse(null);
                if (BarrowsKillBrothersBranch.tunnelBrother == null) {
                    if (Players.getLocal().getX() < 3500) {
                        // if we're in the ge restock and go dig to reset state
                        log("Didn't work, forcing restock");
                        BarrowsRestock.forceRestock = true;
                    } else {
                        // if we are actually in barrows it means all brothers and kiled and the tunnel brother is unknown
                        // stupid solution is just to try them until we find it
                        BarrowsKillBrothersBranch.tunnelBrother = BarrowsBrother.values()[Calculations.random(0, 6)];
                    }
                }
                return true;
            }


            if (!BarrowsKillBrothersBranch.tunnelBrother.tombArea.contains(Players.getLocal())) {
                if (!Players.getLocal().getTile().equals(BarrowsKillBrothersBranch.tunnelBrother.digTile)) {
                    log("Get onto tunnel dig tile");
                    if (Walking.shouldWalk()) Walking.walk(BarrowsKillBrothersBranch.tunnelBrother.digTile);
                    return true;
                }

                log("Dig into barrows tomb");
                Inventory.interact(ItemID.SPADE, "Dig");
                return true;
            }

            return false;
        }

        if (currentlyIn == BarrowsKillBrothersBranch.tunnelBrother) {
            log("In tunnel brother.");
            if (Dialogues.inDialogue()) {
                log("Solve dialogue");
                Dialog.solve("Yes,", "yes", " ");
                return true;
            }

            GameObject sarc = GameObjects.closest("Sarcophagus");
            if (sarc != null && sarc.interact("Search")) {
                if (!Sleep.sleepUntil(Dialogues::inDialogue, 3400)
                        && BarrowsBrother.killedBrothersCount() == 6) {
                    log("Didn't open dialogue, this is not the right tunnel brother");
                    BarrowsKillBrothersBranch.tunnelBrother = BarrowsBrother.values()[Calculations.random(0, 6)];
                }
            }
            return true;
        }

        // exit crypt
        log("In wrong crypt " + currentlyIn + " Should be in: " + BarrowsKillBrothersBranch.tunnelBrother);
        GameObject stairs = GameObjects.closest("Staircase");
        if (stairs != null && stairs.interact("Climb-up")) {
            Sleep.sleepUntil(() -> Players.getLocal().getZ() != 3, 2400);
        }
        return true;
    }
}
