package org.dreambot.behaviour.method.scurrius;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.NPCUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * give spine + 50k + weapon to Historian Aldo
 */
public class MakeBoneWeapon extends Fractal {
    public static final InventoryLoadout BONE_MACE_LOADOUT = new InventoryLoadout()
            .addItem(ItemID.RUNE_MACE)
            .addItem(ItemID.SCURRIUS_SPINE)
            .addItem(ItemID.COINS_995, 50_000);

    public static final InventoryLoadout BONE_BOW_LOADOUT = new InventoryLoadout()
            .addItem(ItemID.YEW_SHORTBOW)
            .addItem(ItemID.SCURRIUS_SPINE)
            .addItem(ItemID.COINS_995, 50_000);

    public static final InventoryLoadout BONE_STAFF_LOADOUT = new InventoryLoadout()
            .addItem(ItemID.BATTLESTAFF)
            .addItem(ItemID.CHAOS_RUNE, 1000)
            .addItem(ItemID.SCURRIUS_SPINE)
            .addItem(ItemID.COINS_995, 50_000);

    public MakeBoneWeapon(Supplier<Boolean> acceptCondition, InventoryLoadout inventoryLoadout) {
        super(acceptCondition);
        setSimpleName("Get bone weapon");
        this.setInventoryLoadout(inventoryLoadout);
    }

    @Override
    public int onLoop() {
        // walk to guy then talk to him about getting a bone weapon
        if (!GoToScurrius.SCURRIUS_GRATE.contains(Players.getLocal())) {
            log("Go to historian");
            if (Walking.shouldWalk()) Walking.walk(GoToScurrius.SCURRIUS_GRATE);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            log("Do bone dialogue");
            Dialog.solve("Can you make a bone", "tell me about Scurrius", "Weapon");
            return ReactionGenerator.getNormal();
        }

        NPCUtil.interact("Historian Aldo");
        return ReactionGenerator.getNormal();
    }
}
