package org.dreambot.behaviour.method.blastfurnace;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * fill the bf coffer with 75k coins
 */
public class BlastFurnacePayFee extends Fractal {
    public static boolean mustPayFee = false;

    public BlastFurnacePayFee() {
        super(() -> mustPayFee
                && Skills.getRealLevel(Skill.SMITHING) < 60
                && BlastFurnaceUtil.BLAST_FURNACE_AREA.contains(Players.getLocal()));
        setSimpleName("Pay fee");
    }

    @Override
    public int onLoop() {
        String npc = Dialogues.getNPCDialogue();
        if (Dialogues.inDialogue() && npc != null && npc.contains("you can use the furnace")) {
            log("no longer need to pay fee");
            mustPayFee = false;
            return ReactionGenerator.getNormal();
        }

        if (Inventory.count(ItemID.COINS_995) < 2500) {
            log("Getting 2.5k coins out");
            new WithdrawLoadoutEvent(new InventoryLoadout().addItem(ItemID.COINS_995, 2500), null)
                    .executed();
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {

            log("Dialogue to pay fee");
            Dialog.solve("Yes", "Okay"); // okay is for the blast furnace captain telling you to work
            return ReactionGenerator.getNormal();
        }

        NPC foreman = NPCs.closest("Blast Furnace Foreman");
        if (foreman == null) {
            log("Cant find foreman...");
            return ReactionGenerator.getNormal();
        }

        foreman.interact("Pay");
        return ReactionGenerator.getNormal();
    }
}
