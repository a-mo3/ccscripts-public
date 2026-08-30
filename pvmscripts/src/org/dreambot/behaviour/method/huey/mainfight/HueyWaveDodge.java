package org.dreambot.behaviour.method.huey.mainfight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.graphics.SpotAnimation;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.util.Direction;
import org.dreambot.behaviour.method.huey.HueyData;
import org.dreambot.behaviour.method.huey.HueyLoadout;
import org.dreambot.fractals.TickDecision;

public class HueyWaveDodge extends TickDecision implements AnimationListener, SpawnListener {
    final HueyLoadout mode;

    public HueyWaveDodge(HueyLoadout mode) {
        this.mode = mode;
        Client.getInstance().addEventListener(this);
//        GameObjects.setIncludeNullNames(true);
    }

    // nextwave is used for magic only dodging, magic only dodging you stand in a safespot, wait for the wave to expire,
    // then go to the other side to prepare for next wave
    public static Direction nextWave = Direction.EAST; // first wave comes from west -> east
    public static int magicMoveByTick = -1 * Integer.MAX_VALUE; // the tick we expect the wave to have expired by, so we move to the other side
    // shouldnt matter much we'll check for graphics but just so it has time to spawn at least

    @Override
    public boolean evaluate() {
        if (mode.getMode() == Skill.MAGIC) {
            NPC tail = NPCs.closest("Hueycoatl tail");
            if (tail == null) tail = NPCs.closest("Hueycoatl tail (broken)");
            if (tail != null) {
                if (tail.getX() < Players.getLocal().getX()) {
                    // if the npc is to the east, then the wave is east to west, and we call that the west wave, so the next one will be east
                    if (nextWave != Direction.EAST) {
                        log("Next east");
                        nextWave = Direction.EAST;
                        magicMoveByTick = Client.getGameTick();
                    }
                } else {
                    if (nextWave != Direction.WEST) {
                        log("Next west");
                        nextWave = Direction.WEST;
                        magicMoveByTick = Client.getGameTick();
                    }
                }
            }

            if (GraphicsObjects.closest(HueyData.LIGHTNING_GRAPHICS_OBJ_ID) != null) return false;
            if (!HueyData.getMagicSafespot(nextWave).equals(Players.getLocal().getServerTile()) && Client.getGameTick() >= magicMoveByTick) {
                log("Moving to next safespot");
                Walking.walkExact(HueyData.getMagicSafespot(nextWave));
                return true;
            }
            return false;
        }

        GraphicsObject waveUnderUs = GraphicsObjects.closest(x -> x.getTile().equals(Players.getLocal().getServerTile()));
        // todo check huey hp to make sure we're in the 2nd part of the fight
        if (waveUnderUs != null && NPCs.closest("Hueycoatl tail") == null) {
            log("We're on a graphic object");
            if (waveUnderUs.getId() == HueyData.WAVE_HEADING_WEST_GRAPHIC) {
                log("West wave");
                GraphicsObject waveBeside = GraphicsObjects.closest(x -> x.getTile().equals(HueyData.getEasternWaveDodge()));
                if (waveBeside == null) {
                    log("No west wave beside us, dodge");
                    Walking.walkExact(HueyData.getEasternWaveDodge());
                    return true;
                }
                return false;
            }

            if (waveUnderUs.getId() == HueyData.WAVE_HEADING_EAST_GRAPHIC) {
                log("East wave");
                GraphicsObject waveBeside = GraphicsObjects.closest(x -> x.getTile().equals(HueyData.getWesternWaveDodge()));
                if (waveBeside == null) {
                    log("dodge");
                    Walking.walkExact(HueyData.getWesternWaveDodge());
                    return true;
                }
                return false;
            }

        }

        return false;
    }

    // huey animate 11676 during tail phase, 4 ticks later the tail slams and spawns the wave,
    // which is the correct time to attack it assuming you are on one of the correct tiles, beside the arrow tile

    public static int moveTick = Integer.MAX_VALUE;

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (animation == HueyData.TAIL_SLAM_ANIMATION) {
            moveTick = Client.getGameTick() + 1;
            return;
        }

        if (npc.getName().equals("Hueycoatl tail") && animation == 11721) {
            log("Slam ani");
            moveTick = Client.getGameTick();
        }

        if (animation != HueyData.HUEY_TAIL_ANIMATION) {
            return;
        }

        log("Tail Animation");
        moveTick = Client.getGameTick() + 5;
    }

}
