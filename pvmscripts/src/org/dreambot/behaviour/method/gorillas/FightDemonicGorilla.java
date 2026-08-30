package org.dreambot.behaviour.method.gorillas;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.*;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;

import java.util.*;
import java.util.stream.Collectors;

public class FightDemonicGorilla extends TickDecision implements HitSplatListener, AnimationListener {
    // ways a gorilla can attack
    List<Skill> gorillaStyles = Arrays.asList(Skill.ATTACK, Skill.RANGED, Skill.MAGIC);
    // the styles you know it cant be, when it switches you know it cant be 1/3, after an attack you know the 2 it cant be
    public static List<Skill> negativeStyle = Arrays.asList();

    // the gorilla styles that are not in negative styles are the possible styles
    List<Skill> possibleStyles() {
        return gorillaStyles.stream().filter(x -> !negativeStyle.contains(x)).collect(Collectors.toList());
    }

    // the animation shows you the attack type, so when you hit the 3 miss switch you know what is impossible
    int lastGorillaAnimation = -1;
    // 3 misses (Hitsplat of damage 0) and the gorilla changes styles
    public static int missCounter = 0;

    // gorilla change their whole id for their overhead
    Map<Integer, Prayer> idToOverhead = new HashMap<>();
    //
    Map<Skill, Prayer> gorillaStyleToOverhead = new HashMap<>();
    final Area area;

    final boolean flickPrayers;

    public FightDemonicGorilla(Area area, boolean flickPrayer) {
        this.area = area;
        this.flickPrayers = flickPrayer;
        Client.getInstance().addEventListener(this);
        idToOverhead.put(7144, Prayer.PROTECT_FROM_MELEE);
        idToOverhead.put(7147, Prayer.PROTECT_FROM_MELEE);
        idToOverhead.put(7148, Prayer.PROTECT_FROM_MISSILES);
        idToOverhead.put(7146, Prayer.PROTECT_FROM_MAGIC);

        gorillaStyleToOverhead.put(Skill.ATTACK, Prayer.PROTECT_FROM_MELEE);
        gorillaStyleToOverhead.put(Skill.MAGIC, Prayer.PROTECT_FROM_MAGIC);
        gorillaStyleToOverhead.put(Skill.RANGED, Prayer.PROTECT_FROM_MISSILES);
    }

    List<Integer> meleeEquips = Arrays.asList(
            ItemID.FIRE_CAPE,
            ItemID.EMBERLIGHT,
            ItemID.ARCLIGHT,
            ItemID.SARADOMIN_SWORD,
            ItemID.DRAGON_DEFENDER,
            ItemID.ABYSSAL_WHIP
    );

    List<Integer> rangeEquips = Arrays.asList(
            ItemID.HUNTERS_SUNLIGHT_CROSSBOW,
            ItemID.AVAS_ATTRACTOR,
            ItemID.AVAS_ASSEMBLER,
            ItemID.AVAS_ACCUMULATOR,
            ItemID.TOXIC_BLOWPIPE
    );

    @Override
    public boolean evaluate() {
        Character target = Players.getLocal().getInteractingCharacter();
        if (target == null) {
            // todo hop from competition

            Character attackingMe = NPCs.closest(x -> x.isInteracting(Players.getLocal()));
            if (attackingMe != null) {
                log("Interact with gorilla attacking me");
                attackingMe.interact();
                return true;
            }

            log("Attack a gorilla");
            NPC n = NPCs.closest(x -> x.getName().equals("Demonic gorilla") && area.contains(x));
            if (n != null) {
                n.interact("Attack");
            }
            Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 2400);
            return true;
        }

        // dodge boulder
        Projectile p = Projectiles.closest(x -> x.getId() == 856 && x.getTargetTile().equals(Players.getLocal().getTile()));
        if (p != null) {
            log("Dodge rock");
            Tile[] ta = PVMUtil.attackableTiles((NPC) target, 2);
            Tile a = Arrays.stream(ta)
                    .filter(Locatable::canReach)
                    .max(Comparator.comparingDouble(Tile::distance))
                    .orElse(null);
            if (!Walking.isRunEnabled()) Walking.toggleRun();
            Walking.walkExact(a);
        }

        // leave area when out of prayer or food
        if (Skill.PRAYER.getBoostedLevel() < 5
                && ItemVariants.PRAYER_POTION.getItem() == null
                && ItemVariants.SUPER_RESTORE.getItem() == null) {
            log("Out of prayer go GE");
            Walking.walk(BankLocation.FEROX_ENCLAVE);
            return true;
        }

        if (Combat.getHealthPercent() < 60 && !Inventory.contains(ItemID.SHARK)) {
            log("Out of sharks go home");
            Walking.walk(BankLocation.FEROX_ENCLAVE);
            return true;
        }

        List<Skill> s = possibleStyles();
        log("Possible stlyes: " + s);
        Prayer ourOverhead = gorillaStyleToOverhead.get(s.get(0));
        log("Our overhead " + ourOverhead);
        log("Gorilla id " + target.getId());
        Prayer gorillaOverhead = idToOverhead.get(target.getId());
        if (gorillaOverhead == Prayer.PROTECT_FROM_MELEE) {
            log("Range mode");
            if (flickPrayers) {
                Prayers.toggle(false, ourOverhead);
                Prayers.toggle(false, PVMUtil.getBestRangePray());
                Sleep.sleep(50);
            }
            Prayers.toggle(true, ourOverhead);
            Prayers.toggle(true, PVMUtil.getBestRangePray());
            // switch to range
            Sleep.sleep(50);
            if (Inventory.contains(ItemID.TOXIC_BLOWPIPE)
                    && Inventory.isFull() && !Equipment.isSlotEmpty(EquipmentSlot.SHIELD)) {
                log("Drop for shield");
                PVMUtil.dropCheapest();
            }
            if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
            if (Combat.getSpecialPercentage() >= 50 && Equipment.contains(ItemID.TOXIC_BLOWPIPE)) Combat.toggleSpecialAttack(true);

            rangeEquips.forEach(Inventory::interact);
        } else {
            log("Melee mode");
            if (flickPrayers) {
                Prayers.toggle(false, ourOverhead);
                Prayers.toggle(false, PVMUtil.getBestMeleePray());
                Sleep.sleep(50);
            }
            Prayers.toggle(true, ourOverhead);
            Prayers.toggle(true, PVMUtil.getBestMeleePray());
            // switch to melee
            Sleep.sleep(50);
            if (Combat.getCombatStyle() != CombatStyle.STRENGTH) Combat.setCombatStyle(CombatStyle.STRENGTH);
            meleeEquips.forEach(Inventory::interact);
        }
        return false;
    }

    List<Integer> ani = Arrays.asList(
            7225,
            7226,
            7227
    );

    int lastAnimation = -1;

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (!Players.getLocal().getCharactersInteractingWithMe().contains(npc)) return;
        // todo check its a gorilla, and the animation is one we expect
        log("Character interacting with us animated " + animation + " " + animationDelay);
        lastGorillaAnimation = animation;
        if (ani.contains(animation) && lastAnimation != animation) {
            missCounter = 0;
            lastAnimation = animation;
        }

        /*
        7224 something it does before every attack?
        7225 magic
        7226 melee
        7227 range

        7228 beat chest? / special, idrk
         */
        if (animation == 7227) negativeStyle = Arrays.asList(Skill.MAGIC, Skill.ATTACK);
        if (animation == 7225) negativeStyle = Arrays.asList(Skill.RANGED, Skill.ATTACK);
        if (animation == 7226) negativeStyle = Arrays.asList(Skill.MAGIC, Skill.RANGED);
    }

    @Override
    public void onHitSplatAdded(Entity entity, int type, int damage, int id, int special, int gameCycle) {
        if (!entity.equals(Players.getLocal())) return;
        HitSplatListener.super.onHitSplatAdded(entity, type, damage, id, special, gameCycle);
        log("Hit splat Type: " + type + " dmg: " + damage + " id: " + id + " special: " + special + " gameCycle " + gameCycle);
        if (damage == 0) missCounter++;
        if (missCounter >= 3) {
            missCounter = 0;
            negativeStyle = gorillaStyles.stream().filter(x -> !negativeStyle.contains(x)).collect(Collectors.toList());
        }
    }
}
