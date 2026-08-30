package org.dreambot.behaviour.method.huey.mainfight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.huey.HueyData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.PVMUtil;

public class HueyLoot extends TickDecision {
    @Override
    public boolean evaluate() {
        // 14012 is huey when he cant be attacked, dead or during tail phase
        NPC hueyUnattackable = NPCs.closest(14012);
        if (hueyUnattackable == null) {
            return false;
        }
        log("Huey loot check");

        GroundItem loot = GroundItems.closest(x -> (Client.isDynamicRegion() || HueyData.HUEY_MAIN_AREA.contains(x.getTile()))
                && (x.getAmount() * x.getItem().getLivePrice()) >= 500);
        if (loot != null) {
            log("Loot on ground " + loot.getName());
            if (Inventory.isFull()) {
                // todo consider if this can cause dropping something lootable and then picking up that up as well, shouldn't really based off context
                log("Drop");
                PVMUtil.dropCheapest();
            }
            loot.interact("Take");
            Sleep.sleep(1200);
            return true;
        }

        // leave huey to restock if we dont have enough supplies to wait for another kc
        Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
//        if (prayerPot == null || Inventory.count(ItemID.SHARK) < 6) {
        if (Dialogues.inDialogue()) {
            log("Solve dialogue");
            Dialog.solve("Yes");
            return true;

        }

        log("Leave");
        GameObject slide = GameObjects.closest(x -> x.hasAction("Quick-slide"));
        if (slide == null) {
            log("Failed to find slide");
            return true;
        }

        slide.interact("Quick-slide");
        Sleep.sleepUntil(() -> !HueyData.isInHueyFight(), 4000);
        return true;
//        }
//        return false;
    }
}
