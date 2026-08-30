package org.dreambot.behaviour.method.artio;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.fractals.util.UtilProvider;
import org.dreambot.settings.timing.ReactionGenerator;

public class LeaveArtio extends Fractal implements HitSplatListener {
    /**
     * after attacking spindel, there is 3 ticks you cannot tp out
     * todo ignore this logic if you have hard diary
     */
    public static final Timer teleportCooldown = new Timer(1800);

    @Override
    public boolean isValid() {
        return Combat.isInWild() && CombatUtil.getThreat() != null;
    }

    @Override
    public int onLoop() {
        return leaveArtio();
    }

    public static int leaveArtio() {
        Logger.info("Leave Artio");
        UtilProvider.staminaUp();
        // exit
        GameObject exit = GameObjects.closest(x -> x.hasAction("Exit"));
        if (exit != null) {
            Logger.info("Exit");
            exit.interact();
            return ReactionGenerator.getQuick();
        }

        // if you are teleblocked hop worlds or run south
        // since CombatUtil exists now we dont have to manage teleblock state
        // combatutil will turn off teleport nodes when blocked / unblocked
        if (CombatUtil.get().isTeleblocked()) {
            if (!CombatUtil.get().isInCombat()) {
                Logger.info("Hop worlds to remove tp timer");
                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isMembers()
                        && x.getMinimumLevel() < Skills.getTotalLevel()
                        && x.isNormal() && x.getWorld() != 401));
                return ReactionGenerator.getNormal();
            }
        }

        // run to ferox because we dont use glory
        Walking.walk(BankLocation.FEROX_ENCLAVE);
        return ReactionGenerator.getQuick();
    }
}
