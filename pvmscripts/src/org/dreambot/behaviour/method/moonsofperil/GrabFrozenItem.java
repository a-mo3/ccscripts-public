package org.dreambot.behaviour.method.moonsofperil;

import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.NPCUtil;
import org.dreambot.settings.timing.ReactionGenerator;

public class GrabFrozenItem extends Fractal implements ChatListener {
    @Setter
    static boolean needsToRestock = false;

    public GrabFrozenItem() {
        super(() -> needsToRestock);
        setSimpleName("Frozen item");
        Client.getInstance().addEventListener(this);
    }

    public static final Area REWARDS = new Area(1521, 9585, 1526, 9575);

    @Override
    public int onLoop() {
        if (!REWARDS.contains(Players.getLocal())) {
            log("Go to rewards");
            if (Walking.shouldWalk()) Walking.walk(REWARDS);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isFull()) {
            log("Make a spot for weapon");
            Inventory.drop(ItemID.COOKED_BREAM);
        }

        if (Dialogues.inDialogue()) {
            if (!Dialog.solve("Blue Moon")) {
                log("Failed to find the blue moon option, going back to bossing");
                needsToRestock = false;
            }
            return ReactionGenerator.getNormal();
        }

//        NPC eyat = NPCs.closest("Eyatlalli");
        NPCUtil.interact("Eyatlalli");
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().contains("Eyatlalli returns your lost weapon")) needsToRestock = false;
    }
}
