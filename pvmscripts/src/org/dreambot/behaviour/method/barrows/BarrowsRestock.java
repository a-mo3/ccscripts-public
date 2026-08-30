package org.dreambot.behaviour.method.barrows;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.method.barrows.handlecrypt.HandleCryptBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class BarrowsRestock extends Fractal implements ChatListener {

    public BarrowsRestock(Supplier<Boolean> acceptCondition, BarrowsLoadout barrowsLoadout) {
        super(() -> acceptCondition.get() || forceRestock);
        Client.getInstance().addEventListener(this);
        this.prependLogic = () -> {
            PrayerUtils.disable(Prayer.values());
            return false;
        };

        this.inventoryLoadout = barrowsLoadout.inventoryLoadout;
        this.equipmentLoadout = barrowsLoadout.equipmentLoadout;
    }

    final Area FEROX_POOL = new Area(3127, 3638, 3130, 3633);
    public static final Area BARROWS = new Area(3544, 3318, 3583, 3268);
    public static boolean forceRestock = false;


    @Override
    public int onLoop() {
        if (Bank.isOpen()) {
            log("Close bank");
            Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            log("Solve dialogue");
            Dialog.solve();
        }

        // force restock, dig into a tomb to get your varbits reset
        if (BARROWS.contains(Players.getLocal())) {
            log("Force reset so going to go dig dharoks");
            BarrowsBrother brother = BarrowsBrother.DHAROK;
            if (!brother.tombArea.contains(Players.getLocal())) {
                if (!Players.getLocal().getTile().equals(brother.digTile)) {
                    log("Get onto dig tile");
                    if (Walking.shouldWalk()) Walking.walk(brother.digTile);
                    return ReactionGenerator.getNormal();
                }

                log("Dig into barrows tomb");
                Inventory.interact(ItemID.SPADE, "Dig");
                forceRestock = false;
                Sleep.sleepUntil(() -> Players.getLocal().getZ() != 0, 2400);
                return ReactionGenerator.getNormal();
            }
        }
        // go recharge stats

        if (!Inventory.contains(ItemID.BARROWS_TELEPORT)) {
            log("Go to barrows manually");
            if (Walking.shouldWalk()) Walking.walk(BARROWS);
            return ReactionGenerator.getNormal();
        }

        Inventory.interact(ItemID.BARROWS_TELEPORT, "Break");
        Sleep.sleepUntil(() -> BARROWS.contains(Players.getLocal()), 6400);
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().contains("The chest is empty")) {
            Logger.info(message + " " + message.getType());
            Logger.info("Chest looted, restock");
            forceRestock = true;
        }

        if (message.getMessage().toLowerCase().contains("you have died")) {
            Logger.info("died");
            forceRestock = true;
        }
    }
}
