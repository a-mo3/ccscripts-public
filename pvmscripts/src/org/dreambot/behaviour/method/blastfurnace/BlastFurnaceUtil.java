package org.dreambot.behaviour.method.blastfurnace;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.BlastFurnaceSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * provide couple common functions across all the blast furnace bar types
 */
public class BlastFurnaceUtil {
    public static final Tile BAR_DISPENSER_TILE = new Tile(1939, 4963, 0);
    public static final Area BLAST_FURNACE_AREA = new Area(1934, 4975, 1957, 4955);

    // fill coal bag and set full if theres no action
    public static void fillCoalBag() {
        Logger.info("Fill coal bag");
        if (Inventory.isFull()) {
            new BankAllInventoryEvent().execute();
        }
        Item cBag = Inventory.get("Coal bag");
        if (cBag != null && !cBag.interact("Fill")) {
            Logger.info("Did not have fill action");
            CoalBag.setStock(27);
            Sleep.sleep(600);
        }
    }

    public static void takeBars() {
        Logger.info("collecting bars");
        if (Inventory.isFull()) {
            new BankAllInventoryEvent().execute();
        }

        // todo water bucket consideration
        // theres a bucket on the ground that you can just drop and pick up each cycle
        GameObject dispenser = GameObjects.closest("Bar dispenser");
        if (dispenser == null) {
            Logger.info("Could not find bar dispenser");
            return;
        }

        if (ItemProcessing.isOpen()) {
            ItemProcessing.makeAll(x -> true);
            return;
        }

        if (!dispenser.hasAction("Take")) {
            if (Walking.shouldWalk()) Walking.walkExact(BAR_DISPENSER_TILE);
            return;
        }
        dispenser.interact();
        Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
    }

    public static void putOreOnBelt() {
        if (Widgets.get(x -> x.getText().contains("You must ask the foreman's")) != null) {
            Logger.info("need foreman's permission");
            BlastFurnacePayFee.mustPayFee = true;
        }

        if (Dialogues.inDialogue()) {
            Logger.info("Dont ask again dialogue");
            Dialog.solve("ask again");
        }

        if (!Bank.isOpen()
                && !Inventory.isEmpty()
                && (!Inventory.contains(x -> x.getId() == ItemID.COAL || x.getName().contains("ore")))) {
            Logger.info("Went to put ore on belt but you have no ore, banking everything.");
            new BankAllInventoryEvent().execute();
            return;
        }

        Logger.info("Put ore on belt");
        if (Widgets.isOpen()) Widgets.closeAll();
        GameObject belt = GameObjects.closest(x -> x.hasAction("Put-ore-on"));
        if (belt == null) {
            Logger.info("Cant find Conveyor belt");
            return;
        }

        belt.interact("Put-ore-on");
        Sleep.sleep(ReactionGenerator.getNormal());
    }

    /**
     * return true if the script should return after this execution
     */
    public static boolean staminaUp() {
        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 10) Walking.toggleRun();
        if (!SettingsRepository.findInstanceOf(new BlastFurnaceSettings()).useStaminas) return false;
        if (!OwnedItems.contains(ItemVariants.STAMINA_POTION)) return false;
        if (!Bank.isOpen()) return false;
        if (Inventory.isFull()) return false;
        if (Walking.getRunEnergy() > 20 || Walking.isStaminaActive()) return false;
        Item staminaInInv = ItemVariants.STAMINA_POTION.getItem();
        if (staminaInInv != null) {
            Logger.info("Drinking stamina");
            staminaInInv.interact("Drink");
            Sleep.sleepUntil(Walking::isStaminaActive, 2400);
            return true;
        }

        Logger.info("Withdrawing a stamina");
        Bank.withdraw(x -> ItemVariants.STAMINA_POTION.contains(x.getId()), 1);
        Sleep.sleepUntil(() -> ItemVariants.STAMINA_POTION.getItem() != null, 1400);
        return true; // script would deposit this after
    }
}
