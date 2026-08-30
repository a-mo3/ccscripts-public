package task.impl;

import config.Pickaxe;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import task.AbstractTask;

public class BankNode extends AbstractTask {
    @Override
    public boolean accept() {
        return config.isRunning() && config.shouldBank() && Inventory.isFull();
    }

    @Override
    public int execute() {
        config.setStatus("Banking...");
        if (!config.isCustomBank()) {
            // use closest bank
            if (Bank.open()) {
                if (upgrade()) {
                    Bank.depositAllExcept(pick -> pick.getName().contains("pickaxe"));
                    Sleep.sleepUntil(() -> !Inventory.isFull(), 3000);
                    Bank.close();
                }
            }
            Sleep.sleepUntil(() -> Walking.shouldWalk(3), Calculations.random(3700, 4500));
        } else {
            // for custom bank location
            if (Bank.open(config.getBankLocation())) {
                if (upgrade()) {
                    Bank.depositAllExcept(pick -> pick.getName().contains("pickaxe"));
                    Sleep.sleepUntil(() -> !Inventory.isFull(), 3000);
                    Bank.close();
                }
            }
            Sleep.sleepUntil(() -> Walking.shouldWalk(3), Calculations.random(3700, 4500));
        }
        if (!Walking.isRunEnabled()) {
            if (Walking.getRunEnergy() > 13) {
                Walking.toggleRun();
            }
        }
        return config.getSleep();
    }

    private boolean upgrade() {
        if (!config.isProgression()) {
            return true;
        }

        // assume bank is already open
        // find current pick
        Pickaxe currentPick = null;
        for (Pickaxe p : Pickaxe.values()) {
            if (p.getID() == getUsedPickaxe().getID()) {
                currentPick = p;
                break;
            }
        }
        Pickaxe highestPick = null;

        for (Pickaxe pick : Pickaxe.values()) {
            if (Skills.getRealLevel(Skill.MINING) >= pick.REQ) {
                if (highestPick != null) {
                    if (highestPick.REQ < pick.getREQ()) {
                        highestPick = pick;
                    }
                } else {
                    highestPick = pick;
                }
            }
        }

        if (currentPick != null && highestPick != null) {
            if (highestPick.getREQ() > currentPick.getREQ()) {
                if (Inventory.isFull()) {
                    Bank.depositAllItems();
                }
                Bank.deposit(x -> x.getName().contains("pickaxe"));
                Sleep.sleepUntil(() -> !Inventory.contains(x -> x.getName().contains("pickaxe")), 3000);
                Logger.log(highestPick);
                Bank.withdraw(highestPick.getID());
                // needs to be final for lambda
                Pickaxe finalHighestPick = highestPick;
                Sleep.sleepUntil(() -> Inventory.contains(finalHighestPick.getID()), 2000);

            }
        }
        return true;
    }

    private Item getUsedPickaxe() {
        Item equippedPickaxe = Equipment.get(x -> x.getName().contains("pickaxe"));
        Item inventoryPickaxe = Inventory.get(x -> x.getName().contains("pickaxe"));
        if (equippedPickaxe != null && inventoryPickaxe != null) {
            Logger.log("NO PICKAXE FOUND");
        } else if (equippedPickaxe != null) {
            return equippedPickaxe;
        } else return inventoryPickaxe;
        return null;
    }
}
