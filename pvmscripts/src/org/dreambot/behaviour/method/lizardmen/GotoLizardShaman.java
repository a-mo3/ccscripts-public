package org.dreambot.behaviour.method.lizardmen;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.LizardmenSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.function.Supplier;

public class GotoLizardShaman extends Fractal {
    final Area room;
    Area TEMPLE = new Area(1279, 10111, 1342, 10051);

    public GotoLizardShaman(Supplier<Boolean> acceptCondition, LizardmenSettings settings) {
        super(acceptCondition);

        this.room = settings.room.area;
        this.inventoryLoadout = settings.loadout.inventoryLoadout;
        this.equipmentLoadout = settings.loadout.equipmentLoadout;
        this.loadoutCondition = () -> settings.room.area.contains(Players.getLocal()) || !TEMPLE.contains(Players.getLocal());

        this.prependLogic = () -> {
            if (!Arrays.stream(LizardRoom.values()).anyMatch(x -> x.area.contains(Players.getLocal()))) {
                for (Prayer value : Prayer.values()) {
                    Prayers.toggle(false, value);
                }
            }
            return false;
        };

        Walking.setObstacleSleeping(false);

        setSimpleName("Go to lizards");
    }

    @Override
    public int onLoop() {
        // todo pray when in danger
        if (Arrays.stream(LizardRoom.values()).anyMatch(x -> x.area.contains(Players.getLocal()))) {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
        } else {
            if (room.distance(Players.getLocal().getTile()) < 10) {
                Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
            } else {
                Prayers.toggle(false, Prayer.PROTECT_FROM_MISSILES);
            }
        }

        if (!TEMPLE.contains(Players.getLocal()) && shouldFerox()) {
            log("Go recharge at ferox");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.FEROX_ENCLAVE);
            return ReactionGenerator.getNormal();
        }

        if (Combat.getHealthPercent() < 60) {
            log("Eat shark");
            Inventory.interact(ItemID.SHARK);
        }

        if (Combat.isEnvenomed() || Combat.isPoisoned()) {
            Item antidote = ItemVariants.ANTI_DOTE_PP.getItem();
            if (antidote != null) {
                log("Sip antidote");
                antidote.interact();
            }
        }

        if (!this.room.contains(Players.getLocal())) {
            log("Go to lizard room");
            if (Walking.shouldWalk()) Walking.walk(this.room);
        }
        return ReactionGenerator.getNormal();
    }

    private boolean shouldFerox() {
        return Skills.getBoostedLevel(Skill.PRAYER) < Skills.getRealLevel(Skill.PRAYER);
    }
}
