package com.ccscripts.cballs.untrained;

import com.ccscripts.PaintButton;
import com.ccscripts.cballs.framework.ItemID;
import com.ccscripts.cballs.framework.ScriptNode;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;

import java.awt.*;
import java.util.List;

public class GetAmmoMouldOut extends ScriptNode {
    @Override
    public boolean isValid() {
        return !Inventory.contains(ItemID.AMMO_MOULD) || Inventory.contains(ItemID.COINS_995) || Inventory.contains(Item::isNoted);
    }

    @Override
    public int fallBack() {
        if (!Bank.isOpen()) {
            Logger.info("Withdraw mould open bank");
            if (Walking.shouldWalk()) Bank.open();
            return 600;
        }


        if (!Inventory.isEmpty()) {
            Bank.depositAllItems();
            return 600;
        }

        if (Bank.isCached() && !Bank.contains(ItemID.AMMO_MOULD) && !Inventory.contains(ItemID.AMMO_MOULD)) {
            Logger.info("No ammo mould stop");
            ScriptManager.getScriptManager().stop();
            return 500;
        }
        Bank.withdraw(ItemID.AMMO_MOULD);
        return 600;
    }

    @Override
    public String getIdentifier() {
        return "AmmoMould";
    }

    @Override
    public String getExpectedNextState() {
        return "penisbutt";
    }

    @Override
    public List<Rectangle> trainingHighlights() {
        return List.of();
    }

    @Override
    protected boolean shouldConfigureReproducers() {
        return false;
    }
}
