package org.dreambot.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetLoadout extends Fractal implements ChatListener {
    Area BARROWS_TP_DESTINATION = new Area(3556, 3319, 3575, 3305);

    public GetLoadout(Supplier<Boolean> acceptCondition) {
        super(() -> acceptCondition.get() || finished);
        this.eventBreakCondition = () -> !Worlds.getCurrent().isMembers();
        Client.getInstance().addEventListener(this);
        this.inventoryLoadout = BarrowSettings.START_INV.setStrict(true);
        this.equipmentLoadout = BarrowSettings.START_EQUIPMENT;
        this.appendLogic = () -> {
            if ((ItemVariants.RING_OF_DUELING.getItem() != null  || Equipment.contains(ItemVariants.RING_OF_DUELING.getIds()))
                    && !BankLocation.FEROX_ENCLAVE.getArea(30).contains(Players.getLocal())
                    && !BankLocation.GRAND_EXCHANGE.getArea(50).contains(Players.getLocal())) {
                if (Inventory.contains(ItemVariants.RING_OF_DUELING.getIds()) && !Equipment.contains(ItemVariants.RING_OF_DUELING.getIds())) {
                    Logger.info("Equipping dueling ring");
                    Equipment.equip(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING.getIds());
                    return true;
                }

                Walking.walk(BankLocation.FEROX_ENCLAVE);
                return true;
            }
            return false;
        };
    }

    final Area FEROX_POOL = new Area(3127, 3638, 3130, 3633);
    public static boolean finished;


    @Override
    public int onLoop() {
        if (Bank.isOpen()) {
            Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
        }

        // go recharge stats
        if (Skills.getBoostedLevel(Skill.PRAYER) < Skills.getRealLevel(Skill.PRAYER)) {
            if (!FEROX_POOL.contains(Players.getLocal())) {
                if (HandleCrypt.BARROWS_CRYPT.contains(Players.getLocal())) {
                    if (Inventory.contains(ItemVariants.RING_OF_DUELING.getIds()) && !Equipment.contains(ItemVariants.RING_OF_DUELING.getIds())) {
                        Logger.info("Equipping dueling ring");
                        Equipment.equip(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING.getIds());
                        return ReactionGenerator.getQuick();
                    }
                }
                if (Walking.shouldWalk(8)) Walking.walk(FEROX_POOL.getCenter());
                return ReactionGenerator.getQuick();
            }

            GameObject pool = GameObjects.closest(x -> x.getName().toLowerCase().contains("pool"));
            if (pool != null && pool.interact()) {
                Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) == Skills.getRealLevel(Skill.PRAYER), 3400);
            }
            return ReactionGenerator.getLong() + 1000;
        }

        if (BankLocation.GRAND_EXCHANGE.getArea(40).contains(Players.getLocal()) || FEROX_POOL.contains(Players.getLocal())) {
            finished = false;
        }

        if (finished) {
            Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getNormal();
        }

        if (Players.getLocal().isAnimating()) {
            return ReactionGenerator.getQuick();
        }

        Inventory.interact(ItemID.BARROWS_TELEPORT, "Break");
        Sleep.sleepUntil(() -> BARROWS_TP_DESTINATION.contains(Players.getLocal()), 6400);
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().equals("The chest is empty.")) {
            Logger.info(message + " " + message.getType());
            finished = true;
        }

        if (message.getMessage().toLowerCase().contains("you have died")) {
            Logger.info("died");
            finished = true;
        }
    }
}
