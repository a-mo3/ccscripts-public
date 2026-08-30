package org.dreambot.behaviour.method.gwd.nex.enter;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.nex.NexLoadout;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.NPCUtil;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * enter the nex lobby
 * get onto the appropriate world
 * Get appropriate gear, we came assume we will own everything, well buy that before getting kc
 * Enter the nex world
 */
public class EnterNexFight extends Fractal {
    static final int NEX_KC_CONFIG = 3180;
    public static final Area NEX_BANK_ROOM = new Area(2899, 5211, 2908, 5194);
    final NexLoadout nexLoadout;

    public EnterNexFight(NexLoadout nexLoadout) {
        super(() -> getKc() >= 40 || NEX_BANK_ROOM.contains(Players.getLocal()));
        this.nexLoadout = nexLoadout;
        setSimpleName("Setup for fight");
    }

    @Override
    public int onLoop() {
        if (!NEX_BANK_ROOM.contains(Players.getLocal())) {
            log("Get into nex room");
            if (Walking.shouldWalk()) Walking.walk(NEX_BANK_ROOM);
            return ReactionGenerator.getNormal();
        }


        if (!Bank.isOpen()) {
            log("Open bank");

            // first time, need to talk to lady to unlock bank
            if (PlayerSettings.getBitValue(13182) < 2) {
                if (Dialogues.inDialogue()) {
                    log("Get through dialogue");
                    Dialog.solve("bank");
                    return ReactionGenerator.getNormal();
                }

                NPCUtil.interact("Ashuelot Reis");
                return ReactionGenerator.getNormal();
            }

            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }

        log("Withdrawing nex loadout");
        log("Withdraw nex loadout " +
                new WithdrawLoadoutEvent(nexLoadout.inventoryLoadout, nexLoadout.equipmentLoadout)
                        .executed()
        );
        return ReactionGenerator.getNormal();
    }

    static int getKc() {
        return PlayerSettings.getConfig(NEX_KC_CONFIG);
    }
}
