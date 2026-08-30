package org.dreambot.behaviour.method.gwd.bandos.tickbandosfight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.gwd.bandos.BandosConsts;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.scripts.BandosScript;

import java.util.*;

import static org.dreambot.behaviour.method.gwd.bandos.tickbandosfight.GetIntoBandosFight.BANDOS_ROOM;

/**
 * loots only the expensive stuff, happens before killing guards
 */
public class BandosExpensiveLootDecision extends TickDecision implements ItemContainerListener {
    public static final Tile startTile = new Tile(2873, 5352, 2);
    public static final Area ZILYANA_BOSS_ROOM = new Area(2883, 5276, 2908, 5257);

    List<Integer> food = Arrays.asList(
            ItemID.MONKFISH,
            ItemID.SUMMER_PIE,
            ItemID.MANTA_RAY,
            ItemID.HALF_A_SUMMER_PIE
    );

    List<Integer> ignoredItems = Arrays.asList(
            ItemID.MAGIC_POTION3,
            ItemID.SUPER_DEFENCE3,
            ItemID.CHAOS_TALISMAN
    );

    public static final int GODWARS_ALTAR_GOD = 12398;

    public BandosExpensiveLootDecision() {
        Client.getInstance().addEventListener(this);
    }

    @Override
    public boolean evaluate() {
        NPC bandos = NPCs.closest(BandosConsts.BANDOS);
        if (bandos != null) {
            log("Bandos still alive");
            return false;
        }

        // reset tile to stand on the respawn tile
        BandosWalkDecision.targetTile = null;
        BandosWalkDecision.firstLap = true;
        BandosWalkDecision.hasBandosAttacked = false;

        GroundItem expensiveLoot = GroundItems.closest(x -> x.getItem().getLivePrice() > 50_000);
        // loot expensive stuff first
        if (expensiveLoot != null) {
            if (Inventory.isFull()) {
                log("Drop cheapest item in inv, inv is full");
                PVMUtil.dropCheapest();
            }
            log("expensive loot " + expensiveLoot);
            expensiveLoot.interact("Take");
            return true;
        }
        return false;
    }


    List<Integer> ignoreList = Arrays.asList(
            ItemID.RUNE_DART, // you cant get these are drops but they're pretty insignificant
            ItemID.ODIUM_WARD,
            ItemID.DRAGON_CROSSBOW,
            ItemID.TOXIC_BLOWPIPE
    );

    public void onInventoryItemAdded(Item item) {
        if (!BANDOS_ROOM.contains(Players.getLocal())) return;
        if (ignoreList.contains(item.getId())) return;
        // todo acb ignore but only when you have 2
        BandosScript.grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }
}
