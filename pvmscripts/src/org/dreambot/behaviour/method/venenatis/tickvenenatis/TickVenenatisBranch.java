package org.dreambot.behaviour.method.venenatis.tickvenenatis;

import lombok.Getter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.venenatis.VenenatisData;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.VenenatisSettings;

import java.awt.*;
import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Venenatis fight but using tick decisions for more optimal gameplay.
 * track venenatis attack cycle here
 */
public class TickVenenatisBranch extends TickFractal implements AnimationListener, SpawnListener {
    public TickVenenatisBranch(Supplier<Boolean> acceptCondition, VenenatisSettings settings) {
        super(acceptCondition);

        HashMap<ItemVariant, BooleanSupplier> potions = new HashMap<>();
        potions.put(ItemVariants.BLIGHTED_SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 10);
        potions.put(ItemVariants.STAMINA_POTION, () -> Walking.getRunEnergy() < 10 && (!Walking.isStaminaActive() || ItemVariants.ENERGY_POTION.getItem() == null));
        potions.put(ItemVariants.ENERGY_POTION, () -> Walking.getRunEnergy() < 5);
        potions.put(ItemVariants.ANTI_DOTE_PP, () -> Combat.isPoisoned() || Combat.isEnvenomed());
        potions.put(ItemVariants.MAGIC_POTION, () -> Skill.MAGIC.getBoostedLevel() - Skill.MAGIC.getLevel() < 3);
        potions.put(ItemVariants.SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3);

        this.paintArraySupplier = () -> {
            NPC venenatis = NPCs.closest(VenenatisData.VENENATIS_NAME);
            String dist = "-";
            if (venenatis != null)
                dist = String.valueOf(venenatis.getServerTile().translate(2, 2).distance(Players.getLocal().getServerTile()));
            return new String[]{
                    "Style? " + venenatisAttackStyle,
                    "Last attack " + lastVenenatisAttack,
                    "counter " + venenatisAttackCounter,
                    "Dist " + dist,
                    "isMoving " + (venenatis == null ? "-" : venenatis.isMoving())
            };
        };

        setSimpleName("Venenatis");

        Client.getInstance().addEventListener(this);

        addChildren(
                new VenenatisTickPrayer(settings.flickPrayers),

                new TickDrinkPotions(potions).setSimpleName("Pot"),

                new GenericTickEat().setSimpleName("Eat"),

                new VenenatisWebPlacement().setSimpleName("Place web"),

                new TickVeneSpiderlings().setSimpleName("Spiderlings"),

                new VenenatisTickAttack().setSimpleName("Webless"),

                new VenenatisWebTickAttack().setSimpleName("Attack web"),

                new VenenatisTickLoot(settings).setSimpleName("Loot")
        );
    }

    // current attack style
    @Getter
    static Skill venenatisAttackStyle = Skill.RANGED;
    // melee -> anything you cant tell anything
    // range -> mage or mage -> would indicate a cycle of 1
    Skill lastVenenatisAttack = Skill.ATTACK;
    // 0-7, when flips over
    public static int venenatisAttackCounter = 0;

    private void incCounter() {
        venenatisAttackCounter++;
        if (venenatisAttackCounter > 7) {
            Logger.log(Color.ORANGE, "Counter reset " + venenatisAttackCounter + " | " + venenatisAttackStyle);
            venenatisAttackCounter = 0;
            if (venenatisAttackStyle == Skill.RANGED) {
                venenatisAttackStyle = Skill.MAGIC;
            } else {
                venenatisAttackStyle = Skill.RANGED;
            }
        }
    }

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (!VenenatisData.VENENATIS_NAME.equalsIgnoreCase(npc.getName())) return;
        log("Venenatis animation: " + animation + " | " + animationDelay);
        Logger.log(Color.ORANGE, "Venenatis animation: " + animation + " | " + animationDelay);
        if (animation == VenenatisData.MELEE_ATK_ANI) {
            Logger.log(Color.ORANGE, "Melee attack inc " + venenatisAttackCounter);
            incCounter();
            lastVenenatisAttack = Skill.ATTACK;
        }

        if (animation == VenenatisData.RANGE_ATK_ANI) {
            Logger.log(Color.ORANGE, "Range attack inc " + venenatisAttackCounter);
            incCounter();
            if (lastVenenatisAttack == Skill.MAGIC) {
                Logger.log(Color.ORANGE, "Magic after range " + venenatisAttackCounter);
                venenatisAttackCounter = 1;
            }
            venenatisAttackStyle = Skill.RANGED;
            lastVenenatisAttack = Skill.RANGED;
        }

        if (animation == VenenatisData.MAGE_ATK_ANI) {
            Logger.log(Color.ORANGE, "Mage attack inc " + venenatisAttackCounter);
            incCounter();
            if (lastVenenatisAttack == Skill.RANGED) {
                Logger.log(Color.ORANGE, "Range after magic " + venenatisAttackCounter);
                venenatisAttackCounter = 1;
            }
            venenatisAttackStyle = Skill.MAGIC;
            lastVenenatisAttack = Skill.MAGIC;
        }
    }

    @Override
    public void onProjectileSpawn(Projectile projectile) {
        if (VenenatisData.WEB_PROJECTILE != projectile.getId()) return;
        log("Web projectile");
        VenenatisData.setProspectiveWebs(projectile.getTargetTile());
        venenatisAttackStyle = Skill.MAGIC;
        lastVenenatisAttack = Skill.MAGIC;
        venenatisAttackCounter = 3;

    }
}
