package org.dreambot.fractals.util;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.general.ItemContainer;
import org.dreambot.api.methods.container.general.ItemContainerId;
import org.dreambot.api.methods.container.general.ItemContainers;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.ArrayList;
import java.util.List;

// i dont remember why i wont just use the loot bag listener but i trust i had a reason
public class LootingBag implements ChatListener {
    private static LootingBag listeners = null;
    public static List<Item> lootingBagCache = new ArrayList<>();
    public static Timer lastCacheTimer = new Timer(60 * 1000 * 1);

    public LootingBag() {
        Client.getInstance().addEventListener(this);
    }

    private static void register() {
        if (listeners == null) listeners = new LootingBag();
    }

    /**
     * updates looting bag cache with whatever is in your bag, if you dont have a bag clear cache
     *
     * @return true if you have a recent cache or if you dont have a looting bag in inv
     */
    public static boolean refreshLootBagCache() {
        register();

        if (!lastCacheTimer.finished()) {
            return true;
        }

        Item bag = ItemVariants.LOOTING_BAG.getItem();
        if (bag == null) {
            Logger.info("No bag cache reset");
            lastCacheTimer.reset();
            lootingBagCache.clear();
            return true;
        }

        ItemContainer bagContainer = ItemContainers.getContainer(ItemContainerId.LOOTING_BAG.getId());
        if (bagContainer == null) {
            if (Widgets.isVisible(81)) {
                Logger.info("Bag empty");
                lastCacheTimer.reset();
                lootingBagCache.clear();
                return false;
            }

            Logger.info("Checking bag to get new cache");
            bag.interact("Check");
            return false;
        }

        lastCacheTimer.reset();
        lootingBagCache = bagContainer.getItems();
        return true;
    }

    public static void close() {
        WidgetChild closeBag = Widgets.get(81, 2);
        if (closeBag != null) closeBag.interact("Close");
    }

    public static int value() {
        register();
        if (ItemVariants.LOOTING_BAG.getItem() == null) return 0;
        return lootingBagCache.stream().mapToInt(x -> x.getLivePrice() * x.getAmount()).sum();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().toLowerCase().contains("you are dead"))
            LootingBag.lootingBagCache.clear();
    }
}
