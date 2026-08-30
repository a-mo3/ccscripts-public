package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class BankAtEnclave extends Fractal {
    public static final Area FEROX = new Area(3124, 3639, 3153, 3625);
    public static final Area REJUV = new Area(3125, 3639, 3131, 3633);
    boolean lock;

    @Override
    public boolean isValid() {
        return Skills.getBoostedLevel(Skill.PRAYER) < 3 || Inventory.isFull() || lock;
    }

    @Override
    public int onLoop() {
        if (!Equipment.contains(ItemVariants.RING_OF_DUELING.getIds())) {
            Logger.warn("NO RING OF DUELING EQUIPPED");
        }

        if (isPrayerFull() && !Inventory.contains(ItemID.RED_SPIDERS_EGGS)) {
            lock = false;
            return ReactionGenerator.getNormal();
        }

        if (!isPrayerFull()) {
            if (!REJUV.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(REJUV);
                return ReactionGenerator.getNormal();
            }

            GameObject pool = GameObjects.closest(x -> x.getName().toLowerCase().contains("pool"));
            if (pool != null && pool.interact()) {
                Sleep.sleepUntil(this::isPrayerFull, 3400);
            }

            return ReactionGenerator.getNormal();
        }


        if (!Bank.isOpen()) {
            if (Walking.shouldWalk()) Bank.open(BankLocation.FEROX_ENCLAVE);
            return ReactionGenerator.getNormal();
        }

        Bank.depositAllItems();
        return ReactionGenerator.getNormal();
    }

    private boolean isPrayerFull() {
        return Skills.getBoostedLevel(Skill.PRAYER) == Skills.getRealLevel(Skill.PRAYER);
    }
}
