package org.dreambot.behaviour.training.fletching;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * Used for the fletching script only
 */
public class FletchLogs extends Fractal {
    final int logId;
    final int targetId;
    final int quantity;
    boolean lock;

    public FletchLogs(int logId, int targetId, int quantity, int maxLvl, boolean fletchFromLogs) {
        // todo
        acceptCondition = (() -> {
            if (!fletchFromLogs) return false;
            if (Skills.getRealLevel(Skill.FLETCHING) > maxLvl) return false;

            if (lock) {
                if (OwnedItems.count(targetId) >= quantity) {
                    lock = false;
                    return false;
                }
                return true;
            }

            if (OwnedItems.count(targetId) <= 14) {
                lock = true;
                return true;
            }
            return false;
        });
        this.logId = logId;
        this.targetId = targetId;
        this.quantity = quantity;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.KNIFE)
                .addItem(logId, 1, 27).setRefill(quantity)
                .setStrict(true)
                .strictIgnore(targetId)
        ;
    }


    public FletchLogs(int logId, int targetId, int quantity, int maxLvl, boolean fletchFromLogs, Boolean isMagics) {
        acceptCondition = (() -> {
            if (fletchFromLogs) return false;
            if (Skills.getRealLevel(Skill.FLETCHING) > maxLvl) return false;
            if (isMagics) return false;
            if (Skills.getRealLevel(Skill.FLETCHING) < 85 && isMagics) return false;

            if (lock) {
                if (OwnedItems.count(targetId) >= quantity) {
                    lock = false;
                    return false;
                }
                return true;
            }

            if (!OwnedItems.contains(targetId)) {
                lock = true;
                return true;
            }
            return false;
        });
        this.logId = logId;
        this.targetId = targetId;
        this.quantity = quantity;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(logId, 1, 27).setRefill(quantity)
                .addItem(ItemID.KNIFE)
                .setStrict(true)
                .strictIgnore(targetId)
        ;
    }


    @Override
    public int onLoop() {
        if (Bank.isOpen() || GrandExchange.isOpen()) {
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.canContinue()) {
            Logger.info("Dialogue solve");
            Dialog.solve();
        }

        if (ItemProcessing.isOpen()) {
            ItemProcessing.makeAll(targetId);
            Sleep.sleepUntil(() -> !Inventory.contains(logId) || Dialogues.canContinue(),
                    () -> Players.getLocal().isAnimating(),
                    1600, 100);
            return ReactionGenerator.getNormal();
        }

        Item log = Inventory.get(logId);
        Item knife = Inventory.get(ItemID.KNIFE);
        log.useOn(knife);
        Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        return ReactionGenerator.getNormal();
    }
}
