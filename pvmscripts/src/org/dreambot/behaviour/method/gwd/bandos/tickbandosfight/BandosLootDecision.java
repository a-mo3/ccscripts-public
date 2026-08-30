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

public class BandosLootDecision extends TickDecision implements ItemContainerListener {
    public static final Tile startTile = new Tile(2873, 5352, 2);
    public static final Area ZILYANA_BOSS_ROOM = new Area(2883, 5276, 2908, 5257);

    Set<Integer> alchables = new HashSet<>(Arrays.asList(
            ItemID.RUNE_FULL_HELM,
            ItemID.ADAMANT_PLATEBODY,
            ItemID.RUNE_MED_HELM,
            ItemID.RUNE_SQ_SHIELD,
            ItemID.RUNE_CHAINBODY,
            ItemID.RUNE_BATTLEAXE,
            ItemID.RUNE_KITESHIELD,
            ItemID.RUNE_PLATESKIRT
    ));

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

    public BandosLootDecision() {
        Client.getInstance().addEventListener(this);
    }

    @Override
    public boolean evaluate() {
        NPC bandos = NPCs.closest(BandosConsts.BANDOS);
        if (bandos != null) {
            log("bandos still alive");
            return false;
        }

        Item alchable = Inventory.get(x -> alchables.contains(x.getId()));
        if (Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY) && alchable != null) {
            log("Alch item: " + alchable);
            Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, alchable);
            return true;
        }

        GroundItem normalLoot = GroundItems.all(BANDOS_ROOM::contains)
                .stream()
                .filter(x -> !ignoredItems.contains(x.getId()))
                // if inventory is full still
                .filter(x -> !Inventory.isFull() || (x.getItem().isStackable() && Inventory.contains(x.getId())))
                // take summer pies and eat them for run
                .filter(x -> x.getId() == ItemID.FROZEN_KEY_PIECE_BANDOS || food.contains(x.getId()) || (x.getItem().getLivePrice() + 1) * x.getAmount() > 1500)
                .min(Comparator.comparingDouble(Entity::distance))
                .orElse(null);
        if (normalLoot != null) {
            // checks inventory fullness implicitly here
            log("Take normal loot");
            normalLoot.interact("Take");
            return true;
        }

        Item foodItem = Inventory.get(x -> food.contains(x.getId()));
        if (foodItem != null && Combat.getHealthPercent() < 100) {
            log("Eat food");
            foodItem.interact();
            return true;
        }

        // the amount a super restore would restore ur prayer
        int prayerRecover = 8 + (int) (Skills.getRealLevel(Skill.PRAYER) * 0.25);
        int missingPrayer = Skills.getRealLevel(Skill.PRAYER) - Skills.getBoostedLevel(Skill.PRAYER);
        if (missingPrayer >= prayerRecover) {
            // drink a prayer
            log("Need prayer points");
            Item pot = ItemVariants.PRAYER_POTION.getItem();
            if (pot == null) pot = ItemVariants.SUPER_RESTORE.getItem();
            if (pot != null) {
                log("Found pot");
                pot.interact();
            } else {
                log("No prayer pot needs to leave");
            }
        }

        // reset tile to stand on the respawn tile
        BandosWalkDecision.targetTile = null;
        BandosWalkDecision.firstLap = true;
        BandosWalkDecision.hasBandosAttacked = false;
        if (!startTile.equals(Players.getLocal().getTile())) {
            Tile dest = Walking.getDestination();
            log("Get onto start tile");
            if (!startTile.equals(dest)) Walking.walkExact(startTile);
            return true;
        }
        return true;
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
