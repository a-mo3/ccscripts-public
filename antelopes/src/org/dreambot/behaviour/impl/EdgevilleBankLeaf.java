package org.dreambot.behaviour.impl;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.map.Area;
import org.dreambot.behaviour.BehaviourUtils;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

public class EdgevilleBankLeaf extends Fractal {
    public static final Area EDGEVILLE_BANK = new Area(3095, 3495, 3097, 3494);
    @Override
    public boolean isValid() {
        return Inventory.count("Black chinchompa") >= ScriptSettings.getSettingsData().chinMax;
    }

    @Override
    public int onLoop() {
        if (Combat.isInWild()) {
            BehaviourUtils.stdWalk(EDGEVILLE_BANK);
            return ReactionGenerator.getQuick();
        }

        if (Bank.open()) {
            Bank.depositAll(ItemID.BLACK_CHINCHOMPA);
        }
        return ReactionGenerator.getQuick();
    }
}
