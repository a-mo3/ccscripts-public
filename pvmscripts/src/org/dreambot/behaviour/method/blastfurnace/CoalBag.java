package org.dreambot.behaviour.method.blastfurnace;

import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.data.ItemID;

public class CoalBag implements ChatListener {
    private static CoalBag instance;

    private CoalBag() {
        Client.getInstance().addEventListener(this);
    }

    public static int getStock() {
        if (instance == null) instance = new CoalBag();
        if (!Inventory.contains(ItemID.COAL_BAG_12019)) return 0;
        return stock;
    }

    @Setter
    private static int stock = 0;

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        String msg = message.getMessage();
        if (msg == null || msg.isEmpty()) return;
        if ("The coal bag is now empty.".equals(msg)) {
            Logger.info("Coal bag is empty");
            stock = 0;
            return;
        }

        if ("The coal bag is empty.".equals(msg)) {
            Logger.info("Coal bag is empty");
            stock = 0;
            return;
        }

        if (msg.contains("The coal bag can hold only 27")) {
            stock = 27;
            return;
        }

        if (msg.contains("The coal bag contains")) {
            String stockString = msg.replaceAll("[^0-9]", "");
            Logger.info("New coal bag stock " + stockString);
            stock = Integer.parseInt(stockString);
        }
    }
}
