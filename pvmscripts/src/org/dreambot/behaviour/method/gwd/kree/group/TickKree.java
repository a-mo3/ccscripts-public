package org.dreambot.behaviour.method.gwd.kree.group;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.ProjectileListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PrayerUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TickKree extends TickDecision implements AnimationListener, SpawnListener, ItemContainerListener, ProjectileListener {
    Tile SAFE = new Tile(2824, 5296, 2);
    final String KREE = "Kree'arra";
    final String MELEE_GUARD = "Flight Kilisa";
    final String MAGIC_GUARD = "Wingman Skree";
    final String RANGED_GUARD = "Flockleader Geerin";

    int meleeGuardTiming = -1;
    int rangeGuardTiming = -1;
    int magicGuardTiming = -1;
    int kreeTiming = -1;

    final Area KREE_BOSS_ROOM = new Area(2823, 5309, 2842, 5296, 2);

    List<Integer> food = Arrays.asList(
            ItemID.MUSHROOM_POTATO,
            ItemID.MANTA_RAY
    );

    List<Integer> equips = Arrays.asList(
            ItemID.DRAGON_CROSSBOW,
            ItemID.DIAMOND_BOLTS_E,
            ItemID.DIAMOND_DRAGON_BOLTS_E
    );

    @Override
    public boolean evaluate() {
        PrayerUtils.toggle(true, appropriateProtectionPrayer());

        if (ItemVariants.SARADOMIN_BREW.getItem() == null || ItemVariants.SUPER_RESTORE.getItem() == null) {
            log("Go bank out of potions");
            // should just tp out
            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return true;
        }

        // attack kree
        Character target = Players.getLocal().getInteractingCharacter();
        NPC kree = NPCs.closest(KREE);
        if (kree == null) {
            log("No kree");
            // loot
            GroundItem loot = GroundItems.all(KREE_BOSS_ROOM::contains)
                    .stream()
                    // if inventory is full still
                    .filter(x -> !Inventory.isFull() || (x.getItem().isStackable() && Inventory.contains(x.getId())))
                    // take summer pies and eat them for run
                    .filter(x -> food.contains(x.getId()) || (x.getItem().getLivePrice() + 1) * x.getAmount() > 1500)
                    .max(Comparator.comparingInt(x -> x.getAmount() * (x.getItem().getLivePrice() + 1)))
                    .orElse(null);
            if (loot != null) {
                log("Take loot");
                loot.interact("Take");
                return false;
            }
        }

        // get onto safe tile
        if (!SAFE.equals(Players.getLocal().getTile())) {
            log("Get onto safe");
            Walking.walkExact(SAFE);
            return true;
        }

        if (!Combat.isAutoRetaliateOn()) {
            log("Turn on auto retal");
            Combat.toggleAutoRetaliate(true);
        }

        if (target == null || !target.equals(kree)) {
            log("Attack Kree");
            if (kree != null && kree.distance() <= 7) kree.interact("Attack");
            return false;
        }
        Item equip = Inventory.get(x -> equips.contains(x.getId()));
        if (equip != null) {
            log("Equipping weapons " + equip.getName());
            equips.forEach(Inventory::interact);
        }

        log("Do nothing just auto");
        return false;
    }

    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        String npcName = npc.getName();
        if (npcName.equals(MELEE_GUARD) && animation == 6957) {
            log("Melee guard attacked");
            meleeGuardTiming = Client.getGameTick() % 5;
            return;
        }

        if (npcName.equals(MAGIC_GUARD) && animation == 6955) {
            log("Magic guard attacked");
            magicGuardTiming = Client.getGameTick() % 5;
            return;
        }

        if (npcName.equals(RANGED_GUARD) && animation == 6956) {
            log("Range guard attacked");
            rangeGuardTiming = Client.getGameTick() % 5;
            return;
        }
    }

    @Override
    public void onProjectileSpawn(Projectile projectile) {
        if (projectile == null) return;
        int id = projectile.getId();
        if (id == 1199 || id == 1200) {
            kreeTiming = Client.getGameTick() % 3;
        }
    }

    private Prayer appropriateProtectionPrayer() {
        int fiveTick = Client.getGameTick() % 5;
        int threeTick = Client.getGameTick() % 3;

        if (kreeTiming == threeTick) return Prayer.PROTECT_FROM_MISSILES;
        if (rangeGuardTiming == fiveTick) return Prayer.PROTECT_FROM_MISSILES;
        if (magicGuardTiming == fiveTick) return Prayer.PROTECT_FROM_MAGIC;
        if (meleeGuardTiming == fiveTick) return Prayer.PROTECT_FROM_MELEE;

        return Prayer.PROTECT_FROM_MISSILES;
    }
}
