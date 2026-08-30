package org.dreambot.behaviour.method.spindel.melee;

import lombok.Getter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.ProjectileListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.spindel.*;
import org.dreambot.behaviour.method.spindel.range.RangeSpindelBranch;
import org.dreambot.fractals.Fractal;

import java.util.Arrays;
import java.util.function.Supplier;

public class MeleeSpindelBranch extends Fractal implements AnimationListener, SpawnListener, ProjectileListener {
    @Getter
    static SpindelPhase phase = SpindelPhase.RANGE_SPECIAL;
    // count 4 attacks per phase
    Timer spindelAtkTimer = new Timer(1000); // just to make sure i dont get fucked by fucked listener behaviour
    static int atkCounter = 0;
    static Tile webTile = null;


    public MeleeSpindelBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
        this.paintArraySupplier = () -> {
            NPC spindel = NPCs.closest(SpindelData.SPINDEL_ID);
            return new String[]{
                    "Phase: " + phase,
                    "Counter: " + atkCounter,
                    "Distance: " + (spindel == null ? "-" : String.valueOf(spindel.distance())),
                    "Web tile: " + webTile
            };
        };

        addChildren(
                new SpindelEat(() -> Combat.getHealthPercent() < 75).setSimpleName("Eat"),
                new SpindelDrinkPrayer(() -> Skills.getBoostedLevel(Skill.PRAYER) < 10).setSimpleName("Drink Prayer"),
                new LootSpindel().setSimpleName("Loot"),
                new SpindelBoostPot(() -> Skills.getBoostedLevel(Skill.STRENGTH) < Skills.getRealLevel(Skill.STRENGTH) + 4)
                        .setSimpleName("Boost"),
//                new GetDistanceFromSpindel().setSimpleName("Get distance"),
                new AntiCrashWildyBosses(),
                new MeleeKillSpiderlings(() -> NPCs.closest(SpindelData.SPIDERLING_ID) != null).setSimpleName("Kill spiderlings"),

                new MeleeAvoidWeb(() -> {
                    // lower atk counter to when this activates so we preempt
                    if (phase == SpindelPhase.MAGE_SPECIAL && (atkCounter == 1 || atkCounter == 2)) return true;

                    Projectile webShot = Projectiles.closest(x -> x.getId() == SpindelData.WEB_PROJECTILE);
                    if (webShot != null && webShot.getTargetDistance() < 4) {
                        return true;
                    }

                    GameObjects.setIncludeNullNames(true);
                    Player lp = Players.getLocal();
                    return lp.getTile() != null
                            && Arrays.stream(GameObjects.getObjectsOnTile(lp.getTile()))
                            .anyMatch(x -> x != null && SpindelData.isWeb(x.getId()));
//                    return Client.getGraphicsObjects().stream().anyMatch(x -> x.getId() == SpindelData.WEB_OBJ_ID && x.getTile().equals(myTile));
                }).setSimpleName("Avoid web"),

                new MeleeAttackSpindel().setSimpleName("Attack")
        );
        setSimpleName("Melee Spindel");
    }

    // todo track movement, cant rely on range and mage attacks to ensure phase because we only get melee hit
    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (npc.getId() != SpindelData.SPINDEL_ID) return;
        Logger.info(String.format("%s animated %d with %d delay", npc.getName(), animation, animationDelay));
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
    public void onNpcSpawn(NPC npc) {
        if (npc.getId() == SpindelData.SPINDEL_ID) {
            phase = SpindelPhase.RANGE_SPECIAL;
            atkCounter = 0;
        }

        // maybe when the spiderlings spawn we set this as well
    }

    int lastId = -1;

    @Override
    public void onTargeted(Projectile projectile, Tile tile) {
//        if (projectile.getId() != lastId) {
//            lastId = projectile.getId();
//            Logger.info("PROJECTILE + " + projectile.getId());
//        }
        if (projectile.getId() == SpindelData.WEB_PROJECTILE) {
            Logger.info("Web projectile targeting " + tile);
            webTile = tile;

            if (phase != SpindelPhase.MAGE_SPECIAL) {
                phase = SpindelPhase.MAGE_SPECIAL;
                atkCounter = 2;
            }
        }
    }

    @Override
    public void onNpcDespawn(NPC npc) {
        if (!Combat.isInWild()) RangeSpindelBranch.killsThisTrip = 0;
        if (npc.getHealthPercent() == 0 && npc.getId() == SpindelData.SPINDEL_ID) {
            Logger.info("Spindel kill");
            RangeSpindelBranch.totalKills++;
            RangeSpindelBranch.killsThisTrip++;
        }
    }

    public static void prayCorrectly() {
        NPC spindel = NPCs.closest(SpindelData.SPINDEL_ID);
        if (spindel != null && spindel.distance() < 5) {
            if (!Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        } else {
            if (MeleeSpindelBranch.getPhase() == SpindelPhase.RANGE_BENIGN || MeleeSpindelBranch.getPhase() == SpindelPhase.RANGE_SPECIAL) {
                if (!Prayers.isActive(Prayer.PROTECT_FROM_MISSILES)) Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
            } else {
                if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            }
        }
    }
}
