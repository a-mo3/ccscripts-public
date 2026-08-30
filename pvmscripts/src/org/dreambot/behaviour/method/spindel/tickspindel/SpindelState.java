package org.dreambot.behaviour.method.spindel.tickspindel;

import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.SpindelData;
import org.dreambot.behaviour.method.spindel.SpindelPhase;

public class SpindelState implements AnimationListener, SpawnListener {
    private static SpindelState instance;
    private SpindelState() {
        Client.getInstance().addEventListener(this);
    }

    public static SpindelState getInstance() {
        if (instance == null) instance = new SpindelState();
        return instance;
    }

    public static SpindelPhase getCurrentPhase() {
        return getInstance().phase;
    }

    public static int getCounter() {
        return getInstance().atkCounter;
    }


    // spindel does 4 range, special, 4 range, special, 4 magic, special, 4 magic
    // spindel special is spawning 2 spiders or shooting web
    int atkCounter = 0;
    Timer spindelAtkTimer = new Timer(1500);
    @Setter
    SpindelPhase phase = SpindelPhase.RANGE_BENIGN;

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (npc.getId() != SpindelData.SPINDEL_ID) return;
        if (!spindelAtkTimer.finished()) {
            Logger.info("Atk too spoon, ignored");
            return;
        }
        if (animation == SpindelData.RANGE_ATK_ANI || animation == SpindelData.MAGE_ATK_ANI || animation == SpindelData.MELEE_ATK_ANI) {
            spindelAtkTimer.reset();
            atkCounter++;
        }

        if (phase == SpindelPhase.RANGE_SPECIAL || phase == SpindelPhase.RANGE_BENIGN) {
            if (animation == SpindelData.MAGE_ATK_ANI) {
                phase = SpindelPhase.MAGE_SPECIAL;
                atkCounter = 1;
            }
        }

        if (phase == SpindelPhase.MAGE_SPECIAL || phase == SpindelPhase.MAGE_BENIGN) {
            if (animation == SpindelData.RANGE_ATK_ANI) {
                phase = SpindelPhase.RANGE_SPECIAL;
                atkCounter = 1;
            }
        }

        if (atkCounter == 4) {
            atkCounter = 0;
            phase = phase.getNext();
        }
    }

    @Override
    public void onProjectileSpawn(Projectile projectile) {
        if (SpindelData.WEB_PROJECTILE == projectile.getId()) {
            Logger.info("Web attack");
            phase = SpindelPhase.MAGE_SPECIAL;
            atkCounter = 3;
        }
    }

    @Override
    public void onNpcSpawn(NPC npc) {
        if (npc.getId() == SpindelData.SPINDEL_ID) {
            Logger.info("Range special spiderling spawn");
            phase = SpindelPhase.RANGE_SPECIAL;
            atkCounter = 0;
        }
    }
}
