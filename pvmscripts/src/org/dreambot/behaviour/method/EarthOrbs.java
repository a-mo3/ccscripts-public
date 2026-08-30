package org.dreambot.behaviour.method;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.text.DecimalFormat;
import java.util.function.Supplier;

public class EarthOrbs extends Fractal implements ItemContainerListener {
    final Tile EARTH_OBLISK = new Tile(3086, 9931);
    final Timer runtime = new Timer();
    int orbsMade = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    public EarthOrbs(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(8)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_EARTH)
        ;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.UNPOWERED_ORB, 1, 26).setRefill(1200)
                .addItem(ItemID.COSMIC_RUNE, 3, 75).setRefill(2000)
        ;
    }

    Timer eatTimer = new Timer(1200);
    Timer energyTimer = new Timer(1800);
    Timer runEnable = new Timer(800);

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.COINS_995)) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Bank.depositAll(ItemID.COINS_995);
            return ReactionGenerator.getNormal();
        }

        if (eatTimer.finished() && Skills.getBoostedLevel(Skill.HITPOINTS) <= 20 && !Bank.isOpen()) {
            if (Inventory.interact(ItemID.SALMON, "Eat")) eatTimer.reset();
        }

        if (energyTimer.finished() && Walking.getRunEnergy() < 30) {
            if (ItemVariants.ENERGY_POTION.interact("Drink")) energyTimer.reset();
        }

        if ((Walking.getRunEnergy() >= 5 && runEnable.finished()) && Players.getLocal().isInCombat()) {
            if (!Walking.isRunEnabled()) {
                Walking.toggleRun();
                runEnable.reset();
            }
        }

        if (!EARTH_OBLISK.equals(Players.getLocal().getTile())) {
            Walking.setObstacleSleeping(false);
            if (Walking.shouldWalk(6)) Walking.walkExact(EARTH_OBLISK);
            return ReactionGenerator.getQuick();
        }

        if (ItemProcessing.isOpen()) {
            ItemProcessing.makeAll(ItemID.EARTH_ORB);
            Antiban.sleepUntil(() -> !Inventory.contains(ItemID.UNPOWERED_ORB),
                    () -> Players.getLocal().isAnimating(),
                    1800,
                    100
            );
            return ReactionGenerator.getQuick();
        }

        GameObject obelisk = GameObjects.closest("Obelisk of Earth");
        if (obelisk != null) {
            Magic.castSpellOn(Normal.CHARGE_EARTH_ORB, obelisk);
            Antiban.sleepUntil(ItemProcessing::isOpen, 2400);
        }
        return ReactionGenerator.getQuick();
    }

    private boolean isAntiPoisoned() {
        return PlayerSettings.getConfig(102) < -7;
    }
}
