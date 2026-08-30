package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import static org.dreambot.behaviour.method.nightmare.PhosaniBranch.NIGHTMARE_CHARGE;

public class PhosaniAvoidTackle extends Fractal implements AnimationListener {
    public PhosaniAvoidTackle() {
        Client.getInstance().addEventListener(this);
        setSimpleName("Avoid tackle");
    }

    boolean nightmareCharging = false;
    Timer chargeTimer = new Timer(600 * 5);

//    private static final Set<Tile> PHOSANIS_MIDDLE_LOCATIONS = ImmutableSet.of(
//            new Tile(6208, 8128),
//            new Tile(6208, 7104),
//            new Tile(7232, 7104));


    @Override
    public boolean isValid() {
        return nightmareCharging && !chargeTimer.finished();
    }

    @Override
    public int onLoop() {
        // todo this is totally different then whats on lost so needs testing
        NPC phosani = NPCs.closest("Phosani's Nightmare");
        if (phosani == null) {
            Logger.info("Couldnt find nightmare to avoid tackle");
            return ReactionGenerator.getQuick();
        }
        int orientation = phosani.getOrientation();
        // south - 0
        // west - 512
        // north 1024
        // east - 1536

        // todo improve the run away to consider if its faster to run up or down / east or west
//        if (orientation == 512 || orientation == 1536) {
        // if you have a y distance of < 3
        int yDist = Math.abs(Players.getLocal().getServerTile().getY() - phosani.getY());
        if (yDist < 5) {
            Logger.info("Run north");
            Walking.walk(Players.getLocal().getServerTile().translate(0, 5));
            return ReactionGenerator.getQuick();
        }

//        }

        int xDist = Math.abs(Players.getLocal().getServerTile().getX() - phosani.getX());
        if (xDist < 5) {
            Logger.info("Run east");
            Walking.walk(Players.getLocal().getServerTile().translate(5, 0));
            return ReactionGenerator.getQuick();
        }

        Logger.info("Safe");
        return ReactionGenerator.getQuick();
    }

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (animation == NIGHTMARE_CHARGE) {
            nightmareCharging = true;
            chargeTimer.reset();
        }

        if (nightmareCharging && animation != -1 && animation != NIGHTMARE_CHARGE) {
            nightmareCharging = false;
        }
    }
}
