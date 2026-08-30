package org.dreambot.behaviour.method.spindel.tickspindel;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.SpindelData;
import org.dreambot.behaviour.method.spindel.SpindelLoadout;
import org.dreambot.behaviour.method.spindel.SpindelPhase;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PVMUtil;

import java.util.Arrays;
import java.util.Comparator;

public class TickSpindelWebDodge extends TickDecision implements SpawnListener {
    public static Timer webExpiry = new Timer(28_000);
    public static Tile lastWebCenter = null;

    public TickSpindelWebDodge() {
        Client.getInstance().addEventListener(this);
    }

    @Override
    public boolean evaluate() {
        // if you're standing on the web, get off it
        if (!webExpiry.finished() && lastWebCenter != null && lastWebCenter.getArea(2).contains(Players.getLocal())) {
            log("We're in the web and need to leave ");
            NPC spindel = NPCs.closest("Spindel");
            if (spindel == null) {
                log("Cant find spindel");
                return false;
            }
            Tile best = Arrays.stream(PVMUtil.attackableTiles(spindel, 3))
                    .filter(x -> !lastWebCenter.getArea(2).contains(x))
                    .min(Comparator.comparingDouble(Tile::distance))
                    .orElse(null);
            if (best == null) {
                log("No best tile found");
                return false;
            }

            Walking.walkExact(best);
            return true;
        }

        // if its time for the web to be shot, go stand on the entrance
        // might need to be >= 2
        Projectile webShot = Projectiles.closest(SpindelData.WEB_PROJECTILE);
        if (webShot == null
                && SpindelState.getCurrentPhase() == SpindelPhase.MAGE_SPECIAL
                && SpindelState.getCounter() >= 1
                && SpindelState.getCounter() < 3) {
            // todo auto retaliate needs to be off here
            log("Setting up for web attack");
            if (!SpindelData.SPINDEL_WEB_PLACEMENT.equals(Players.getLocal().getServerTile())
                    && !SpindelData.SPINDEL_WEB_PLACEMENT.equals(Players.getLocal().getServerTile())) {
                log("Needs to walk onto entrance");
                Walking.walkExact(SpindelData.SPINDEL_WEB_PLACEMENT);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onProjectileSpawn(Projectile projectile) {
        if (SpindelData.WEB_PROJECTILE == projectile.getId()) {
            Tile us = projectile.getTargetTile();
            log("Web projectile spawned " + us);
            lastWebCenter = us;
            webExpiry.reset();
        }
    }
}
