package org.dreambot.behaviour.method.blastfurnace;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class BlastFurnaceMithril extends Fractal {
    public BlastFurnaceMithril(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Blast furnace Mithril");
    }

    @Override
    public int onLoop() {
        if (BlastFurnaceUtil.staminaUp()) return ReactionGenerator.getNormal();
        // assume you are in blast furnace and have set up the money coffer & fee
        // assume you own enough ores to be crafting this much
        // failsafe if coal bag doesnt track correctly and we have excess ores
        if (BarsOres.MITHRIL_ORE.getValue() >= 13 && BarsOres.COAL.getValue() < 2) {
            log("Fixing excess ores");
            if (Inventory.count(ItemID.COAL) < Math.min(27, OwnedItems.count(ItemID.COAL))) {
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

        if (Inventory.contains(ItemID.MITHRIL_ORE)) {
            BlastFurnaceUtil.putOreOnBelt();
            return ReactionGenerator.getNormal();
        }

        // todo check you even own a coal bag
        if (!Bank.isOpen() && CoalBag.getStock() > 0 || Inventory.contains(ItemID.COAL)) {
            log("Putting in coal");
            if (CoalBag.getStock() > 0 && !Inventory.isFull()) {
                Inventory.interact("Coal bag", "Empty");
                return ReactionGenerator.getNormal();
            }

            BlastFurnaceUtil.putOreOnBelt();
            return ReactionGenerator.getQuick();
        }

        // take bars out
        if (!Inventory.isFull() && BarsOres.MITHRIL_BAR.getValue() > 0) {
            // take the bars out
            log("Take bars out of blast furnace");
            BlastFurnaceUtil.takeBars();
            return ReactionGenerator.getNormal();
        }


        // Deposit steel, Stamina up, withdraw iron, fill coal bag
        if (!Bank.isOpen()) {
            log("Opening bank");
            if (Walking.shouldWalk(8)) Bank.open(BankLocation.BLAST_FURNACE);
            return ReactionGenerator.getNormal();
        }

        // todo stamina up

        // deposit all
        if (Inventory.contains(x -> !x.getName().toLowerCase().contains("coal bag")
                && x.getId() != ItemID.COAL
                && x.getId() != ItemID.MITHRIL_ORE)) {
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


        if (Bank.contains(ItemID.COAL) && Inventory.count(ItemID.COAL) < 9) {
            Bank.withdraw(ItemID.COAL, 9);
            Sleep.sleepUntil(() -> Inventory.count(ItemID.COAL) == 9, 2000);
            Bank.withdrawAll(ItemID.MITHRIL_ORE);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
