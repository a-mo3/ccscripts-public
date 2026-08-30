package org.dreambot.behaviour.method.huey;

import org.dreambot.api.Client;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.world.Location;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.method.huey.comms.HueyCommsClient;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * when the leader of a team restocks, and then creates a new instance, the team members need to leave the current and join the new
 * here we check we arent the leader and if in instance, we bounce, then go back to normal operation
 */
public class HueyRegroup extends Fractal {
    boolean isInInstance = false;

    @Override
    public boolean isValid() {
        if (HueyCommsClient.currentTeam == null) {
            return false;
        }

        if (!Client.isDynamicRegion()) isInInstance = false;

        if (HueyCommsClient.currentTeam.getTeamLeader().equals(Players.getLocal().getName())) {
            // if we are the leader, and we where not in an instance and then get in one, we need to broadcast the regroup message to make
            // all the members rejoin
            if (Client.isDynamicRegion() && !isInInstance) {
                HueyCommsClient.getInstance(5, Location.GERMANY).orderRegroup();
                isInInstance = true;
            }
        }

        // now we accept if we are not the leader, and should be regrouping
        return (HueyCommsClient.needsToRegroup && !Players.getLocal().getName().equals(HueyCommsClient.currentTeam.getTeamLeader()));
    }

    @Override
    public int onLoop() {

        if (!Client.isDynamicRegion()) {
            log("We are out of instance");
            HueyCommsClient.needsToRegroup = false;
            return ReactionGenerator.getNormal();
        }

        // leave fight if need be
        if (HueyData.isInHueyFight()) {
            log("In fight, leave");
            HueyData.leaveFight();
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            log("Solve");
            Dialog.solve("Leave");
            return ReactionGenerator.getNormal();
        }

        // if not in fight, exit via flag
        GameObject flag = GameObjects.closest(x -> x.hasAction("Leave-group"));
        if (flag == null) {
            log("Failed to find meeting flag, logging out");
            Client.setIdleTime(300_000);
            return ReactionGenerator.getNormal();
        }

        log("Leave group");
        flag.interact("Leave-group");
        Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 4000);

        return ReactionGenerator.getNormal();
    }
}
