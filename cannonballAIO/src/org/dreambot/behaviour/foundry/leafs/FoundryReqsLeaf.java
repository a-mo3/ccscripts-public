package org.dreambot.behaviour.foundry.leafs;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;

import java.util.function.Supplier;

public class FoundryReqsLeaf extends Fractal {
    Supplier<Integer> totalCoins = () -> Inventory.count(ItemID.COINS_995) + Bank.count(ItemID.COINS_995);
//    Supplier<List<BuyItem>> buyItemList = () -> new ArrayList<>(){{
//       add(new BuyItem(ItemID.STEEL_BAR, Math.min(500, totalCoins.get() / 2 / LivePrices.getHigh(ItemID.STEEL_PLATEBODY))));
//       add(new BuyItem(ItemID.IRON_BAR, Math.min(250 ,totalCoins.get() / 3 / LivePrices.getHigh(ItemID.IRON_PLATEBODY))));
//       add(new BuyItem(ItemID.AMULET_OF_GLORY6, 1));
//    }};

    @Override
    public boolean isValid() {
        return (OwnedItems.count(ItemID.STEEL_BAR) < 19 || OwnedItems.count(ItemID.IRON_BAR) < 9)
                && !Equipment.contains("Preform") && PaidQuest.SLEEPING_GIANTS.isFinished();
    }

    @Override
    public int onLoop() {
        // exit giants foundry
        NPC kovac = NPCs.closest("Kovac");
        GameObject exit = GameObjects.closest("Exit");
        if (kovac != null && exit != null && exit.interact("Exit")) {
            return 3000;
        }

        setStatus("Buying scrap for foundry");
        Logger.info("Loadout - " + new WithdrawLoadoutEvent(
                new InventoryLoadout()
                        .addItem(ItemID.STEEL_BAR, 19).setRefill(500)
                        .addItem(ItemID.IRON_BAR, 9).setRefill(500), null
        ).executed());

        return 1000;
    }
}
