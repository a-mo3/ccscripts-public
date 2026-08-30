package org.dreambot.behaviour.method.revs.behaviour;

import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.method.spindel.GoToSpindel;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.scriptdata.RevenantSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GoToRevs extends Fractal {
    public GoToRevs(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.eventBreakCondition = Combat::isInWild;

        this.loadoutCondition = () -> !Combat.isInWild();
        this.inventoryLoadout = SettingsRepository.findInstanceOf(new RevenantSettings()).revenantInventoryLoadout.loadout.setStrict(true);
        this.equipmentLoadout = SettingsRepository.findInstanceOf(new RevenantSettings()).revenantEquipmentLoadout.loadout;
    }

    Area FEROX_POOL = new Area(3127, 3636, 3130, 3633);

    @Override
    public int onLoop() {
        if (Dialogues.canEnterInput()) {
            log("enter input");
            if (Widgets.isOpen()) Widgets.closeAll();
            Keyboard.type(" 1", true);
            return ReactionGenerator.getQuick();
        }

        if (Dialogues.inDialogue()) {
            log("Dialogue");
            if (Widgets.isOpen()) {
                log("close widgets");
                Widgets.closeAll();
                return ReactionGenerator.getQuick();
            }

            Dialog.solve("don't ask again", "Yes", "teleport");
            return ReactionGenerator.getQuick();
        }

        // restore stats at ferox pool
        if (!areStatsFull() && !Combat.isInWild()) {
            if (!FEROX_POOL.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(FEROX_POOL);
                return ReactionGenerator.getNormal();
            }

            GameObject rejPool = GameObjects.closest("Pool of Refreshment");
            if (rejPool != null && !Players.getLocal().isMoving()) {
                rejPool.interact("Drink");
                Sleep.sleepUntil(GoToSpindel::areStatusFull, 2300);
            }
            return ReactionGenerator.getQuick();
        }

        if (Walking.shouldWalk()) {
            Walking.walk(SettingsRepository.findInstanceOf(new RevenantSettings()).targetRevenant.getArea());
        }
        return ReactionGenerator.getNormal();
    }

    public static boolean areStatsFull() {
        if (Combat.isPoisoned() || Combat.isEnvenomed()) return false;
        if (Skills.getBoostedLevel(Skill.HITPOINTS) < Skills.getRealLevel(Skill.HITPOINTS)) return false;
        if (Skills.getBoostedLevel(Skill.PRAYER) < Skills.getRealLevel(Skill.PRAYER)) return false;
        return Walking.getRunEnergy() > 60;
    }
}
