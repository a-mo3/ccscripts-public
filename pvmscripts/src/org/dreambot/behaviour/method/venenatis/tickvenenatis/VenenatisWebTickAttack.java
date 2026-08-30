package org.dreambot.behaviour.method.venenatis.tickvenenatis;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.*;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.venenatis.VenenatisData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

import java.util.*;
import java.util.stream.Collectors;

public class VenenatisWebTickAttack extends TickDecision implements AnimationListener, ChatListener {

    public VenenatisWebTickAttack() {
        setSimpleName("Venenatis attack");
        Client.getInstance().addEventListener(this);

    }

    List<Integer> switchBack = Arrays.asList(
            ItemID.GLACIAL_TEMOTLI,
            ItemID.SARACHNIS_CUDGEL,
            ItemID.SARADOMIN_SWORD,
            ItemID.VIGGORAS_CHAINMACE,
            ItemID.URSINE_CHAINMACE
    );
    Filter<Item> reequip = x -> switchBack.contains(x.getId());

    @Override
    public boolean evaluate() {
        NPC venenatis = NPCs.closest(VenenatisData.VENENATIS_NAME);
        if (venenatis == null) return false;
        if (!Equipment.contains(reequip) && Inventory.contains(reequip)) {
            log("Re equip");
            Inventory.interact(reequip);
        }

        if (!Walking.isRunEnabled() && Walking.getRunEnergy() >= 5) Walking.toggleRun();

        Character target = Players.getLocal().getInteractingCharacter();
        if (venenatis.isMoving()) {
            log("Wait for vene to stop moving");
            if (target != null) {
                log("Untarget vene");
                Walking.walk(Players.getLocal().getTile());
            }
            return true;
        }

        // walk around web or off / out of predicted area
        Area predicted = VenenatisData.getWebLandingArea();
        List<GameObject> webObjects = GameObjects.all(x -> VenenatisData.isWeb(x.getId()));
        if (predicted != null && predicted.contains(Players.getLocal())) {
            log("In predicted area, exit");
            if (Walking.shouldWalk()) {
                log("Walk");
                Walking.walkExact(getAttackable(venenatis,
                        webObjects.stream().map(GameObject::getTile).toArray(Tile[]::new),
                        predicted
                ));
            }
            return true;
        }

        if (webObjects.stream().anyMatch(x -> x.getTile().equals(Players.getLocal().getTile()))) {
            log("On web obj");
            if (Walking.shouldWalk()) {
                log("Walk");
                Walking.walkExact(
                        getAttackable(venenatis,
                                webObjects.stream().map(GameObject::getTile).toArray(Tile[]::new),
                                predicted
                        ));
            }
            return true;
        }

        // not on web or in the predicted area, fetch the clickable tiles and path around the web
        Tile at = getAttackable(venenatis,
                webObjects.stream().map(GameObject::getTile).toArray(Tile[]::new),
                predicted
        );

        Tile[] t = findAPath(
                Players.getLocal().getTile(),
                at,
                webObjects.stream().map(GameObject::getTile).collect(Collectors.toSet())
        );

        if (t.length == 0) {
            log("Walk directly");
            if (!venenatis.equals(target)) {
                log("Attack venenatis");
                venenatis.interact();
            }
            return true;
        }

        clickPath = clickableTiles(t);
        if (clickPath == null || clickPath.isEmpty()) {
            log("C empty walk direct");
            if (!venenatis.equals(target)) {
                log("Attack venenatis");
                venenatis.interact();
            }
        } else {
            log("Follow c path");
            Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            Walking.walkExact(clickPath.pop());
        }
        return true;
    }

    /**
     * @return gets the attackable tile of venenatis closest to you or a tile closest to you that is safe, if vene is all over the web.
     */
    public static Tile getAttackable(NPC vene, Tile[] web, Area predicted) {
        Tile[] ta = PVMUtil.attackableTiles(vene, 3);

        Tile at = Arrays.stream(ta)
                .filter(Locatable::canReach)
                .filter(x -> predicted == null || !predicted.contains(x))
                .filter(x -> Arrays.stream(web).noneMatch(x::equals))
                .min(Comparator.comparingDouble(Tile::distance))
                .orElse(null);
        if (at == null) at = Arrays.stream(Players.getLocal().getSurroundingArea(10).getTiles())
                .filter(Locatable::canReach)
                .filter(x -> predicted == null || !predicted.contains(x))
                .filter(x -> Arrays.stream(web).noneMatch(x::equals))
                .min(Comparator.comparingDouble(Tile::distance))
                .orElse(null);

        return at;
    }

    public static Stack<Tile> clickPath = null;

    private Tile[] findAPath(Tile start, Tile dest, Set<Tile> obstacles) {
        Stack<Tile> path = new Stack<>();
        move(start, dest, obstacles, path);
        return path.toArray(new Tile[0]);
    }

    private void move(Tile start, Tile dest, Set<Tile> obstacles, Stack<Tile> path) {
        if (start == null) return;

        int dy = dest.getY() - start.getY();
        int dx = dest.getX() - start.getX();
        if (dy < 0) dy *= -1;
        if (dx < 0) dx *= -1;
        // 0 = diagonal, 1 = vertical, 2 = horizontal
        int moveMode = Integer.compare(dy, dx);
        Tile visit;
        int verticalMove = Integer.compare(dest.getY(), start.getY());
        int horizontalMove = Integer.compare(dest.getX(), start.getX());
        if (moveMode == 0) {
            visit = start.clone().translate(horizontalMove, verticalMove);
        } else if (moveMode == 1) {
            visit = start.clone().translate(0, verticalMove);
        } else {
            visit = start.clone().translate(horizontalMove, 0);
        }

        if (visit.equals(dest)) return;

        if (obstacles.contains(visit)) {
            // scan out, lets grab the 1 tile radius around the start and find a tile that isn't an obstacle
            // this is not robust at all but should be okay for zalcano portals, maybe.
            visit = Arrays.stream(start.getArea(1)
                            .getTiles())
                    .filter(tile -> !obstacles.contains(tile))
                    .min(Comparator.comparingDouble(x -> x.distance(dest)))
                    .orElse(null);
        }

        path.push(visit);
        move(visit, dest, obstacles, path);
    }

    private Stack<Tile> clickableTiles(Tile[] wholePath) {
        if (wholePath == null) return null;
        Stack<Tile> clickPoints = new Stack<>();
        Tile previous = null;
        boolean movingDiag = false;
        for (Tile t : wholePath) {
            if (previous == null) {
                previous = t;
                continue;
            }
            boolean md = t.getX() != previous.getX() && t.getY() != previous.getY();
            if (!md & movingDiag) {
                clickPoints.push(previous);
                previous = t;
                movingDiag = false;
                continue;
            }
            movingDiag = md;
            previous = t;
        }
        // always add the last tile in the path
        clickPoints.push(wholePath[wholePath.length - 1]);
        return clickPoints;
    }
}
