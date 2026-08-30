package org.dreambot.behaviour.method.calvarion;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.spindel.GoToSpindel;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.fractals.util.UtilProvider;
import org.dreambot.settings.timing.ReactionGenerator;

public class LeaveCalvarion extends Fractal implements HitSplatListener {
    public static final Area SPINDEL_CHASM = new Area(1617, 11567, 1645, 11528, 2);

    /**
     * after attacking spindel, there is 3 ticks you cannot tp out
     * todo ignore this logic if you have hard diary
     */
    public static final Timer teleportCooldown = new Timer(1800);

    /**
     * -1 if not tb'd world number if you are, once you arent on that world you know ur tb is gone
     */
    public static int tellyBlockedWorld = -1;

    @Override
    public boolean isValid() {
        return Combat.isInWild() && CombatUtil.getThreat() != null;
    }

    @Override
    public int onLoop() {
        return leaveCalvarion();
    }

    public static int leaveCalvarion() {
        Logger.info("Leave calvarion");
        UtilProvider.staminaUp();
        // run out of spindel if you are in there

        // exit
        GameObject exit = GameObjects.closest(x -> x.hasAction("Exit"));
        if (exit != null) {
            Logger.info("Exit");
            exit.interact();
            return ReactionGenerator.getQuick();
        }

        // if you are teleblocked hop worlds or run south

        // tp out
        if (Equipment.contains(ItemVariants.AMULET_OF_GLORY.getIds())) {
            Player threat = CombatUtil.getThreat();
            if (threat != null) {
                GoToSpindel.shouldHop = true;
                Logger.info("TP out " + CombatUtil.getThreat().getName());
            } else {
                Logger.info("TP out");
            }
            Equipment.interact(EquipmentSlot.AMULET, "Edgeville");
            Sleep.sleepUntil(() -> !Combat.isInWild(), 1400);
            return ReactionGenerator.getQuick();
        }


        if (Equipment.contains(ItemVariants.COMBAT_BRACLET.getIds())) {
            Player threat = CombatUtil.getThreat();
            if (threat != null) {
                GoToSpindel.shouldHop = true;
                Logger.info("TP out " + CombatUtil.getThreat().getName());
            } else {
                Logger.info("TP out");
            }
            Equipment.interact(EquipmentSlot.HANDS, "Monastery");
            Sleep.sleepUntil(() -> !Combat.isInWild(), 1400);
        }
        return ReactionGenerator.getQuick();
    }
}
