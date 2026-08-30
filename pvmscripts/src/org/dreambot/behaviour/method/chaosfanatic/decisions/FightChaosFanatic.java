package org.dreambot.behaviour.method.chaosfanatic.decisions;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.LoadoutMapEntry;
import org.dreambot.scriptdata.ChaosFanaticSettings;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FightChaosFanatic extends TickDecision implements SpawnListener {
    List<Integer> weaponIds = Arrays.asList(
            ItemID.WEBWEAVER_BOW,
            ItemID.CRAWS_BOW,
            ItemID.MAGIC_SHORTBOW,
            ItemID.RUNE_CROSSBOW
    );

    final ChaosFanaticSettings settings;

    public FightChaosFanatic(ChaosFanaticSettings settings) {
        Client.getInstance().addEventListener(this);
        this.settings = settings;
    }

    Map<Tile, Timer> dodgeTiles = new ConcurrentHashMap<>();

    Area FANATIC_AREA = new Area(2986, 3851, 2969, 3839);

    @Override
    public boolean evaluate() {
        // re equip
        for (LoadoutMapEntry equipmentItem : settings.loadout.equipmentLoadout.getLoadoutList()) {
            if (!Equipment.isSlotEmpty(equipmentItem.getSlot())) continue;
            int id = equipmentItem.getItem().getItemId();
            log("Slot empty " + id);
            if (Inventory.contains(id)) {
                log("Equip " + id);
                Inventory.interact(id);
            }
        }

        if (Skill.PRAYER.getBoostedLevel() == 0) {
            Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
            if (prayerPot == null) prayerPot = ItemVariants.BLIGHTED_SUPER_RESTORE.getItem();
            if (prayerPot != null) {
                log("Drink prayer");
                prayerPot.interact();
            } else {
                log("No prayer no potions");
            }
        }

        if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
            log("Set to range rapid");
            Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
        }

        // pray flick
        if (settings.flickPrayer) {
            if (Skills.getBoostedLevel(Skill.PRAYER) > 0) {
                Prayers.toggleQuickPrayer(false);
                Sleep.sleep(50);
                Prayers.toggleQuickPrayer(true);
            }
        } else {
            Prayers.toggleQuickPrayer(true);
        }


        GroundItem loot = GroundItems.closest(x -> FANATIC_AREA.contains(x)
                && (x.getItem().getLivePrice() * (x.getItem().isStackable() ? x.getAmount() : 1)) > LivePrices.get(ItemID.BLIGHTED_MANTA_RAY));
        if (loot != null) {
            log("Take loot " + loot.getName());
            if (Inventory.isFull()) {
                log("Drop a manta ray");
                Inventory.drop(ItemID.BLIGHTED_MANTA_RAY);
            }

            loot.interact();
            return true;
        }

        // dodge projectile
        NPC fanatic = NPCs.closest("Chaos Fanatic");
        Iterator<Map.Entry<Tile, Timer>> it = dodgeTiles.entrySet().iterator();
        if (fanatic != null) {
            while (it.hasNext()) {
                Map.Entry<Tile, Timer> entry = it.next();
                Tile t = entry.getKey();
                Timer timer = entry.getValue();
                if (timer.finished()) {
                    log("Remove expired tile");
                    it.remove(); // safe removal
                } else {
                    // run away from the tile
                    if (t.distance() < 3) {
                        int dx = fanatic.getX() - t.getX();
                        int dy = fanatic.getY() - t.getY();
                        log("Walk across");
                        if (!Walking.isRunEnabled()) Walking.toggleRun();
                        Walking.walkExact(fanatic.getTile().translate(dx, dy));
                        return true;
                    }
                }
            }
        }

        // eat
        int missingHp = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
        if (missingHp >= 20) {
            log("Eat manta ray");
            Inventory.interact(ItemID.BLIGHTED_MANTA_RAY);
        }

        // potion
        int rangeBoost = Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel();
        if (rangeBoost < 4) {
            log("Should boost range");
            Item pot = ItemVariants.RANGING_POTION.getItem();
            if (pot != null) {
                log("Drinking Pot: " + pot);
                pot.interact();
            } else {
                log("No potion");
            }
        }

        // attack
        if (fanatic == null) {
            log("No fanatic");
            return true;
        }

        Character target = Players.getLocal().getInteractingCharacter();
        if (target == null) {
            Character fanaticAttacking = fanatic.getInteractingCharacter();
            if (fanaticAttacking != null && !fanaticAttacking.equals(Players.getLocal())) {
                log("Fanatic is attacking someone else, hop");
                WorldHopper.hopWorld(Worlds.getRandomWorld(GetOff330.MEMBERS_WORLD_FILTER));
                return true;
            }

            log("Attack fanatic");
            fanatic.interact();
        }
        return false;
    }

    @Override
    public void onProjectileSpawn(Projectile projectile) {
        if (projectile.getId() == 551) {
            // note tiles to stay away from
            log("Targeted tile " + projectile.getTargetTile());
            dodgeTiles.put(projectile.getTargetTile(), new Timer(2_000));
        }
    }
}
