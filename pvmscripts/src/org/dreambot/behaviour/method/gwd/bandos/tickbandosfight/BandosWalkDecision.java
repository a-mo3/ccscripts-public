package org.dreambot.behaviour.method.gwd.bandos.tickbandosfight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.gwd.bandos.BandosConsts;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PrayerUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles walking away from bandos
 */
public class BandosWalkDecision extends TickDecision implements AnimationListener {
    Tile startTile = new Tile(2873, 5352, 2);
    // starts at the tile
    Tile t1 = new Tile(2864, 5351, 2); // always shoot
    Tile t2 = new Tile(2864, 5361, 2); // skip first rotation
    Tile t3 = new Tile(2864, 5369, 2);
    Tile t4 = new Tile(2876, 5366, 2);
    Tile t5 = new Tile(2876, 5358, 2);
    Tile t6 = new Tile(2876, 5351, 2);

    Map<Tile, Tile> tileMap = new HashMap<>();

    public BandosWalkDecision() {
        Client.getInstance().addEventListener(this);
        tileMap.put(t1, t2);
        tileMap.put(t2, t3);
        tileMap.put(t3, t4);
        tileMap.put(t4, t5);
        tileMap.put(t5, t6);
        tileMap.put(t6, t1);
        setSimpleName("Bandos walk");
    }

    public static boolean firstLap = false;
    public static Tile bandosAttackTile = null;
    public static Tile targetTile = null;

    public static boolean hasBandosAttacked = false;
    public static boolean firstEnter = false;


    @Override
    public boolean evaluate() {
        NPC bandos = NPCs.closest(BandosConsts.BANDOS);
        if (bandos == null) {
            log("No bandos");
            return false;
        }

        if (Inventory.contains(x -> !Equipment.contains(x.getId()) && BandosConsts.primaryWeapons.contains(x.getId()))) {
            log("Equpping primary");
            Inventory.interact(x -> !Equipment.contains(x.getId()) && BandosConsts.primaryWeapons.contains(x.getId()));
        }
        if (Inventory.contains(ItemID.ODIUM_WARD)) {
            Inventory.interact(ItemID.ODIUM_WARD);
        }

        if (Inventory.contains(ItemID.SARADOMIN_DHIDE_SHIELD)) {
            Inventory.interact(ItemID.SARADOMIN_DHIDE_SHIELD);
        }

        if (firstEnter) {
            if (!t1.equals(Players.getLocal().getTile())) {
                log("Getting onto first tile to configure fight");
                Tile dest = Walking.getDestination();
                if (!t1.equals(dest)) Walking.walkExact(t1);
                return true;
            }

            if (bandos.distance() < 5) {
                log("Bandos close, fight is set up");
                firstEnter = false;
            } else {
                return true;
            }
        }

        if (targetTile == null) targetTile = t1;
        // get on next tile
        // walk to the next tile, when your server tile hit the target tile, attack bandos then set the next target
        Tile lpServerTile = Players.getLocal().getServerTile();
        if (t3.equals(lpServerTile)) {
            log("Hit the first lap");
            firstLap = false;
        }

        if (hasBandosAttacked && bandosAttackTile != null && lpServerTile != bandosAttackTile) {
            log("Bandos atk skip reset");
            bandosAttackTile = null;
            hasBandosAttacked = false;
        }

        PrayerUtils.toggle(Client.getGameTick() - lastPlayerAttack >= 4, PVMUtil.getBestRangePray());
        if (tileMap.containsKey(lpServerTile)) {
            targetTile = tileMap.get(lpServerTile);
            if (decideAttack(bandos)) return true;
        }

        if (Walking.getDestination() == null || !tileMap.containsKey(Walking.getDestination())) {
            log("Walk to next 6-0 tile " + targetTile);
            log("on tgt check " + targetTile.equals(Players.getLocal().getTile()));
            Walking.clickTileOnMinimap(targetTile);
            return true;
        }
        return true;
    }

    private boolean decideAttack(NPC bandos) {
        if (bandos.distance() < 6) {
            log("Bandos too close we skip this attack");
            return false;
        }
        // skip attack because its t2 on the first lap
        if (firstLap && t2.equals(Players.getLocal().getServerTile())) {
            log("First lap t2 skip");
            firstLap = false;
            return false;
        }
        // skip attack because bandos hit us previously
        if (hasBandosAttacked) {
            hasBandosAttacked = false;
            return false;
        }
        // skip attack because we've attacked or eaten too recently
        if (lastPlayerAttack > 0 && Client.getGameTick() - lastPlayerAttack < 4) {
            log("Recent attack skip " + Client.getGameTick() + " " + lastPlayerAttack);
            return false;
        }

        // attack bandos
        log("Attack bandos");
        if (bandos != null) bandos.interact("Attack");
        return true;
    }

    public static int lastPlayerAttack = -1;
    final List<Integer> playerAtkAnimations = Arrays.asList(
            7552, // cross bow
            426 // crystal bow
    );

    public void onPlayerAnimation(Player player, int animation, int animationDelay) {
        if (!player.equals(Players.getLocal())) return;

        if (playerAtkAnimations.contains(animation))
            lastPlayerAttack = Client.getGameTick();
    }
}
