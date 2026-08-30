package org.dreambot.behaviour.method.lizardmen;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.graphics.SpotAnimation;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KillLizardmen extends TickDecision implements AnimationListener {
    final LizardRoom room;
    final int SPAWN_EXPLODE_ANI = 7159; // 7159 = exploding, -1 is idling and right after new spawn ani
    final int LIZARD_PRE_JUMP_ANI = 7152;

    public static Map<Tile, Timer> jumpTiles = new ConcurrentHashMap<>();


    public KillLizardmen(LizardRoom room) {
        this.room = room;
        setSimpleName("Kill lizardmen");
        Client.getInstance().addEventListener(this);
    }

    List<Integer> alchables = Arrays.asList(
            ItemID.RUNE_MED_HELM,
            ItemID.EARTH_BATTLESTAFF,
            ItemID.MYSTIC_EARTH_STAFF,
            ItemID.RUNE_WARHAMMER,
            ItemID.RUNE_CHAINBODY,
            ItemID.RED_DHIDE_VAMBRACES
    );

    @Override
    public boolean evaluate() {
        /*
        here we need to manage doding when jump, and when spawns are about to explode
        then attack a lizardman
         */
        Item cheapestInvItem = PVMUtil.getCheapestExcept(ItemID.NATURE_RUNE, ItemID.FIRE_RUNE);
        if (cheapestInvItem == null) return false; // this would mean empty inv which shouldnt be possible
        // loot
        GroundItem warHammer = GroundItems.closest(x -> x.getId() == ItemID.DRAGON_WARHAMMER && room.area.contains(x));
        if (warHammer != null) {
            if (Inventory.isFull()) {
                log("drop cheapest, inv full");
                Inventory.drop(cheapestInvItem.getId());
            }

            log("Take warhammer");
            warHammer.interact();
            return true;
        }

        GroundItem loot = GroundItems.closest(x -> x.canReach()
                && x.getItem().getId() != ItemID.RUNITE_BOLTS
                && (x.getItem().getLivePrice() * (x.getItem().isStackable() ? x.getAmount() : 1)) > Math.max(1200, LivePrices.get(cheapestInvItem)));
        if (loot != null) {
            if (Inventory.isFull()) {
                log("drop cheapest, inv full");
                Inventory.drop(ItemID.SHARK);
            }

            log("Take warhammer");
            loot.interact();
            return true;
        }

        Character attackingUs = Players.getLocal().getCharacterInteractingWithMe();
        if (attackingUs != null && attackingUs.getId() == 8565 && attackingUs.distance() <= 2) {
            log("We are too close to a shaman move to a safe tile");
            Tile safe = getSafe();
            if (safe == null) {
                log("Failed to find safe");
            } else {
                if (Walking.shouldWalk()) Walking.walkExact(getSafe());
            }
            return true;
        }

        // dodge jumps
        Iterator<Map.Entry<Tile, Timer>> it = jumpTiles.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Tile, Timer> jump = it.next();
            if (jump.getValue().finished()) {
                log("Clear jump");
                it.remove();
                continue;
            }

            if (jump.getKey().distance() <= 2) {
                log("We are too close to a jump tile, move to a safe tile");
                Tile safe = getSafe();
                if (safe == null) {
                    log("Failed to find safe");
                } else {
                    if (Walking.shouldWalk()) Walking.walkExact(getSafe());
                }
                return true;
            }
        }

        // dodge exploding spawns
        NPC spawn = NPCs.closest(x -> x.getAnimation() == SPAWN_EXPLODE_ANI && room.area.contains(x) && x.distance() <= 2);
        if (spawn != null) {
            log("Exploding spawn run away");
            Tile safe = getSafe();
            if (safe == null) {
                log("Failed to find safe");
            } else {
                if (Walking.shouldWalk()) Walking.walkExact(getSafe());
            }
            return true;
        }

        // alch
        if (Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY) && Inventory.contains(x -> alchables.contains(x.getId()))) {
            log("Alch sumn");
            Item alchable = Inventory.get(x -> alchables.contains(x.getId()));
            Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, alchable);
        }

        if ((Equipment.contains(ItemID.MAGIC_SHORTBOW) || Equipment.contains(ItemID.MAGIC_SHORTBOW_I))
                && Combat.getSpecialPercentage() >= 55) {
            Combat.toggleSpecialAttack(true);
        }

        // retaliate over attacking
        attackingUs = Players.getLocal().getCharacterInteractingWithMe();
        Character target = Players.getLocal().getInteractingCharacter();
        if (target == null && attackingUs != null && attackingUs.getId() == 8565) {
            log("twack homie");
            attackingUs.interact();
            return true;
        }
        log("No op");

        return false;
    }

    Tile getSafe() {
        return room.tiles.stream()
                // filter locs with a lizardman next to it
                .filter(x -> x.distance(Players.getLocal()) > 2)
                .filter(x -> {
                    NPC a = NPCs.closest(n -> n.isHealthBarVisible() && n.getName().equals("Lizardman shaman"), x);
                    return a == null || a.distance(x) > 2;
                })
                // todo filter from near spawns

                .max(Comparator.comparingDouble(Tile::distance))
                .orElse(null);
    }

    @Override
    public void onNPCSpotAnimation(NPC npc, SpotAnimation animation) {
        if (!room.area.contains(Players.getLocal()) || !room.area.contains(npc)) {
            return;
        }

        if (animation.getAnimationId() == LIZARD_PRE_JUMP_ANI) {
            log("Lizard jumping");
            jumpTiles.put(Players.getLocal().getServerTile(), new Timer(4000));
        }
    }

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (!room.area.contains(Players.getLocal()) || !room.area.contains(npc)) {
            return;
        }

        if (animation == LIZARD_PRE_JUMP_ANI) {
            log("Lizard jumping");
            jumpTiles.put(Players.getLocal().getServerTile(), new Timer(4000));
        }
    }
}
