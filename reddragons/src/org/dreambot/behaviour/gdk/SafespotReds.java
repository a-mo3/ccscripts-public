package org.dreambot.behaviour.gdk;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.SmartLootEvent;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class SafespotReds extends Fractal {
    Tile NORTHEN_SAFESPOT = new Tile(1823, 9942);
    public static final Area FORTHOS_DUNGEON = new Area(1777, 9996, 1866, 9886);
    public static final Area FORTHOS_EXIT = new Area(1828, 9976, 1831, 9972);
    Area FORTHOS_RUIN = new Area(1699, 3576, 1705, 3572);

    public static final Area SPIDER_AREA = new Area(1829, 9970, 1843, 9945);
    public static final Area DRUID_AREA = new Area(1795, 9944, 1807, 9932);

    Area BAD_DOOR = new Area(1798, 9962, 1804, 9956);

    Area NORTHEN_DRAGON = new Area(1813, 9944, 1821, 9938);

    public static Tile lootTile = null;

    public SafespotReds() {
        LocalPathFinder pathFinder = LocalPathFinder.getLocalPathFinder();

        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Web", "Slash"));
        Arrays.stream(BAD_DOOR.getTiles()).forEach(pathFinder::addBlacklistedTile);
    }

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            Dialog.solve("");
        }

        if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
            Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
        }

        if (Combat.isAutoRetaliateOn()) Combat.toggleAutoRetaliate(false);
        handlePray();

        if (Combat.getHealthPercent() < 80) {
            Inventory.interact(ItemID.JUG_OF_WINE, "Drink");
        }

        if (!FORTHOS_DUNGEON.contains(Players.getLocal())) {
            if (!FORTHOS_RUIN.contains(Players.getLocal())) {
                if (Walking.shouldWalk(8)) Walking.walk(FORTHOS_RUIN);
                return ReactionGenerator.getNormal();
            }

            GameObject stairs = GameObjects.closest("Ladder");
            if (stairs != null && stairs.interact("Climb-down")) {
                Sleep.sleepUntil(() -> FORTHOS_DUNGEON.contains(Players.getLocal()), 2400);
            }
            return ReactionGenerator.getNormal();
        }

        if (!NORTHEN_SAFESPOT.equals(Players.getLocal().getTile())) {
            if (Walking.shouldWalk(8)) Walking.walk(NORTHEN_SAFESPOT);
            return ReactionGenerator.getNormal();
        }

        if (Players.all(x -> NORTHEN_SAFESPOT.distance(x) < 2).size() > 1 && !Players.getLocal().isInCombat()) {
            Logger.info("Hopping from competition");
            int world = Worlds.getCurrentWorld();
            WorldHopper.hopWorld(Worlds.getRandomWorld(w -> w.isMembers()
                    && w.isNormal()
                    && w.getMinimumLevel() < Skills.getTotalLevel())
            );
            Sleep.sleepUntil(() -> Client.isLoggedIn() && world != Worlds.getCurrentWorld(), 12_000);


            return ReactionGenerator.getNormal();
        }

        NPC interactingWith = NPCs.closest(x -> x.isInteracting(Players.getLocal()) && x.getName().equals("Red dragon"));
        if (interactingWith != null) {
            lootTile = interactingWith.getTrueTile();
        }

        if (lootTile != null) {
            GroundItem loot = GroundItems.closest(x -> x.getTile().equals(lootTile)
                    && LivePrices.get(x.getID()) * x.getAmount() > 300);
            if (loot != null) {
                Logger.info("loot: " + new SmartLootEvent(
                        () -> GroundItems.all(x -> x.getTile().equals(lootTile)
                                && LivePrices.get(x.getID()) * x.getAmount() > 300),
                        ItemID.JUG, ItemID.JUG_OF_WINE)
                        .executed());
                return ReactionGenerator.getQuick();
            }
        }

        Character tgt = Players.getLocal().getInteractingCharacter();
        if (Players.getLocal().isInCombat() && tgt != null) {
            return ReactionGenerator.getNormal();
        }

        NPC northDragon = NPCs.closest(x -> NORTHEN_DRAGON.contains(x) && x.getName().equals("Red dragon") && x.canAttack());
        if (northDragon != null && northDragon.interact("Attack")) {
            Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 2400);
        }

        return ReactionGenerator.getNormal();
    }

    public static void handlePray() {
        if (Skills.getBoostedLevel(Skill.PRAYER) == 0) return;

        if (SPIDER_AREA.contains(Players.getLocal())) {
            if (!Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
            return;
        }


        if (DRUID_AREA.contains(Players.getLocal())) {
            if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            return;
        }

        if (Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
        if (Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) Prayers.toggle(false, Prayer.PROTECT_FROM_MAGIC);
    }
}
