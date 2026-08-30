package org.dreambot.behaviour.method.blastfurnace;

import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * fill the bf coffer with 75k coins
 */
public class BlastFurnaceFillCoffer extends Fractal {
    public BlastFurnaceFillCoffer() {
        super(() -> amountInCoffer() == 0 && BlastFurnaceUtil.BLAST_FURNACE_AREA.contains(Players.getLocal()));
        setSimpleName("Fill coffer");
    }

    @Override
    public int onLoop() {
        if (Widgets.get(x -> x.getText().contains("You must ask the foreman's")) != null) {
            Logger.info("need foreman's permission");
            BlastFurnacePayFee.mustPayFee = true;
        }

        if (Inventory.count(ItemID.COINS_995) < 5000) {
            log("Getting 75k coins out");
            new WithdrawLoadoutEvent(new InventoryLoadout().addItem(ItemID.COINS_995, 75_000), null)
                    .executed();
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.canEnterInput()) {
            log("Dialogue to deposit 75k");
            Keyboard.type("75k", true);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            log("Dialogue to deposit");
            Dialog.solve("Deposit", "Okay"); // okay is for the blast furnace captain telling you to work
            return ReactionGenerator.getNormal();
        }

        GameObject coffer = GameObjects.closest("Coffer");
        if (coffer == null) {
            log("Failed to find coffer");
            return ReactionGenerator.getNormal();
        }

        coffer.interact("Use");
        return ReactionGenerator.getNormal();
    }

    public static int amountInCoffer() {
        return PlayerSettings.getBitValue(5357);
    }
}
