package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.ArrayList;
import java.util.List;

public class SleepWalkers extends Fractal implements SpawnListener, AnimationListener {
    public SleepWalkers() {
        setSimpleName("Sleep Walkers");
    }

    private static final int SLEEP_WALKER_SACRIFICE_ANIMATION = 8571;
    private final List<NPC> sleepWalkers = new ArrayList<>();

    @Override
    public boolean isValid() {
        NPC nightmare = NPCs.closest("Phosani's Nightmare");
        return !sleepWalkers.isEmpty() && nightmare != null && nightmare.getAnimation() != 9103;
    }

    @Override
    public int onLoop() {
        // todo ensure blowpipe or darts or w/e

        // find the closest sleep walker to nightmare and shoot him in his shit
        NPC walker = null;
        NPC nightmare = NPCs.closest("Phosani's Nightmare");

        if (nightmare == null) {
            Logger.error("Can't find nightamre in sleep walkers");
            return ReactionGenerator.getQuick();
        }


        for (int i = 0; i < sleepWalkers.size(); i++) {
            NPC loopWalker = sleepWalkers.get(i);
            if (loopWalker == null || loopWalker.getHealthPercent() == 0) {
                sleepWalkers.remove(loopWalker);
                continue;
            }

            if (walker == null || loopWalker.distance() < walker.distance()) walker = loopWalker;
        }

//        Log.info("Attacking walker " + walker);
        if (walker != null && !walker.equals(Players.getLocal().getInteractingCharacter())) walker.interact("Attack");
        return ReactionGenerator.getQuick();
    }

    @Override
    public void onPlayerAnimation(Player player, int animation, int animationDelay) {
        if (animation == 5061) {
            if (sleepWalkers.removeIf(x -> x.equals(Players.getLocal().getInteractingCharacter()))) {
                Log.info("Removed sleepwalker after attack");
            }
        }
    }

    @Override
    public void onNpcSpawn(NPC npc) {
        if ("Sleepwalker".equals(npc.getName())) {
            sleepWalkers.add(npc);
        }
    }

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (animation == SLEEP_WALKER_SACRIFICE_ANIMATION) {
            sleepWalkers.removeIf(x -> x.equals(npc));
        }
    }
}
