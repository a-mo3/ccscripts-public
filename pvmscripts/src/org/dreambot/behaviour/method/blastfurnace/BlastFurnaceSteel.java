package org.dreambot.behaviour.method.blastfurnace;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class BlastFurnaceSteel extends Fractal {
    public BlastFurnaceSteel(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Blast furnace Steel");
    }

    @Override
    public int onLoop() {
        if (BlastFurnaceUtil.staminaUp()) return ReactionGenerator.getNormal();
        // assume you are in blast furnace and have set up the money coffer & fee

        // take bars out
        if ((!Inventory.isFull() && !Inventory.contains(ItemID.IRON_ORE)) && BarsOres.STEEL_BAR.getValue() > 0) {
            if (Inventory.contains(ItemID.IRON_ORE)) {
                log("Bank iron to withdraw bars");
                new BankAllInventoryEvent().execute();
            }
            // take the bars out
            log("Take bars out of blast furnace");
            BlastFurnaceUtil.takeBars();
            return ReactionGenerator.getNormal();
        }

        // assume you own enough ores to be crafting this much
        if (BarsOres.COAL.getValue() >= 100) {
            log("Fixing excess coal");
            if (Inventory.count(ItemID.IRON_ORE) < 27 && OwnedItems.count(ItemID.IRON_ORE) >= 27) {
                log("Withdrawing iron");
                if (!Bank.isOpen()) {
                    if (Walking.shouldWalk()) Bank.open(BankLocation.BLAST_FURNACE);
                    Sleep.sleepUntil(Bank::isOpen, 1800);
                    return ReactionGenerator.getNormal();
                }

                if (Inventory.contains(x -> x.getId() != ItemID.IRON_ORE)) {
                    Bank.depositAllItems();
                }

                Bank.withdrawAll(ItemID.IRON_ORE);
                return ReactionGenerator.getNormal();
            }

            BlastFurnaceUtil.putOreOnBelt();
            return ReactionGenerator.getNormal();
        }


        // if the quantity of coal is < 27 putting iron in will make iron bars not steel.
        if (BarsOres.COAL.getValue() < 27 && OwnedItems.count(ItemID.COAL) >= 27
                && !(Inventory.contains(x -> x.getName().toLowerCase().contains("coal bag")
                && CoalBag.getStock() > 0))) {
            log("Stocking up on coal");
            // todo put a full inventory of coal into the jaunt
            if (Inventory.count(ItemID.COAL) < 27) {
                log("Withdrawing coal");
                if (!Bank.isOpen()) {
                    if (Walking.shouldWalk()) Bank.open(BankLocation.BLAST_FURNACE);
                    Sleep.sleepUntil(Bank::isOpen, 1800);
                    return ReactionGenerator.getNormal();
                }

                if (Inventory.contains(x -> x.getId() != ItemID.COAL)) {
                    Bank.depositAllItems();
                }

                Bank.withdrawAll(ItemID.COAL);
                return ReactionGenerator.getNormal();
            }

            BlastFurnaceUtil.putOreOnBelt();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.IRON_ORE)) {
            BlastFurnaceUtil.putOreOnBelt();
            return ReactionGenerator.getNormal();
        }

        // todo check you even own a coal bag
        if (!Bank.isOpen() && CoalBag.getStock() > 0 || Inventory.contains(ItemID.COAL)) {
            log("Putting in coal");
            if (CoalBag.getStock() > 0 && !Inventory.isFull()) {
                log("Empty coal bag");
                Inventory.interact("Coal bag", "Empty");
                return ReactionGenerator.getNormal();
            }

            BlastFurnaceUtil.putOreOnBelt();
            return ReactionGenerator.getQuick();
        }

        // Deposit steel, Stamina up, withdraw iron, fill coal bag
        if (!Bank.isOpen()) {
            log("Opening bank");
            if (Walking.shouldWalk(8)) Bank.open(BankLocation.BLAST_FURNACE);
            return ReactionGenerator.getNormal();
        }

        // todo stamina up

        // deposit all
        if (Inventory.contains(x -> !x.getName().toLowerCase().contains("coal bag") && x.getId() != ItemID.IRON_ORE)) {
            log("Deposit all");
            Bank.depositAllItems();
            return ReactionGenerator.getNormal();
        }

        // withdraw iron & coal bag + fill abg
        // todo check you own it & should use it
        if (ItemVariants.COAL_BAG.getItem() == null) {
            log("withdraw coal bag");
            Bank.withdraw("Coal bag");
            return ReactionGenerator.getNormal();
        }

        if (CoalBag.getStock() < 27) {
            BlastFurnaceUtil.fillCoalBag();
            Sleep.sleepUntil(() -> CoalBag.getStock() >= 27, 4400);
            return ReactionGenerator.getNormal();
        }

        Bank.withdrawAll(ItemID.IRON_ORE);
        return ReactionGenerator.getNormal();
    }
}
