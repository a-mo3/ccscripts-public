package org.dreambot.behaviour.method.moonsofperil.eclipsemoon;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The phase where eclipse spawns every ~2 ticks and you have to face it before it attack you
 * this is a dps phase, should not eat
 * the attacks are not delayed when parrying eclipse moon so we should use a DH greataxe switch, its pretty cheap
 */
public class EclipseMoonClonesPhase extends TickDecision implements SpawnListener {
    public EclipseMoonClonesPhase() {
        setSimpleName("Clones");
        Client.getInstance().addEventListener(this);
    }

    // this shine object only appears under us during this phase
    public static final int CENTER_SHINE_OBJ_ID = 51041;
    public static final int ECLIPSE_MOON_ID = 13012;
    Queue<Tile> eclipseClones = new LinkedList<>();
    public static final Tile ECLIPSE_CENTER = new Tile(1488, 9632);

    @Override
    public boolean evaluate() {
        if (NPCs.closest(x -> ECLIPSE_CENTER.equals(x.getTile())) != null || !Players.getLocal().getTile().equals(ECLIPSE_CENTER)) {
            return false;
        }

        // todo probably want a setting to enable this
        Item dharokSwitch = ItemVariants.DHAROK_GREATAXE.getItem();
        if (dharokSwitch != null) {
            if (Inventory.isFull()) {
                log("Drop net");
                Inventory.drop(ItemID.BIG_FISHING_NET);
            }
            log("Dharok switch");
            dharokSwitch.interact();
            // you can keep going after this.
        }

        Tile nextFace = eclipseClones.poll();
        if (nextFace == null) {
            log("No direction to face next");
        } else {
            log("Looking at tile " + nextFace);
            Walking.walkExact(nextFace); // wont actually walk because you're stuck, but this should make it face there.
        }
        return true;
    }

    @Override
    public void onNpcSpawn(NPC npc) {
        GameObject centerShine = GameObjects.closest(CENTER_SHINE_OBJ_ID);
        if (centerShine == null) return;
        if (npc.getRealID() == ECLIPSE_MOON_ID) {
            log("Spawn " + npc);
            eclipseClones.add(npc.getTile());
        }
    }
}
