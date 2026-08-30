package org.dreambot.behaviour.method.gwd.kree.group;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.comms.impl.gwd.GodWarsBosses;
import org.dreambot.comms.impl.gwd.GodWarsClient;
import org.dreambot.comms.impl.gwd.GodWarsTeam;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.scriptdata.KreearraSettings;

import static org.dreambot.behaviour.method.gwd.kree.GetKreeKC.ARMADYL_EYRiE;
import static org.dreambot.behaviour.method.gwd.kree.GetKreeKC.THROW_GRAPPLE_AREA;

public class EnterGroupKree extends TickDecision {
    public static final Area KREE_BOSS_ROOM = new Area(2823, 5309, 2842, 5296, 2);
    public static final Tile INFRONT_OF_DOOR = new Tile(2839, 5294, 2);
    final KreearraSettings settings;

    // includes boss room and area infront of door
    Area TEAMMATE_AREA = new Area(
            new Tile(2843, 5309, 2),
            new Tile(2822, 5310, 2),
            new Tile(2822, 5294, 2),
            new Tile(2837, 5294, 2),
            new Tile(2838, 5290, 2),
            new Tile(2841, 5290, 2),
            new Tile(2844, 5296, 2));

    public EnterGroupKree(KreearraSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean evaluate() {
        if (!ARMADYL_EYRiE.contains(Players.getLocal()) && Inventory.contains(ItemID.ECUMENICAL_KEY)) {
            PrayerUtils.disableAll();
            log("Key enter");
            if (!THROW_GRAPPLE_AREA.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(THROW_GRAPPLE_AREA);
                return true;
            }

            // equip grapple
            if (!Equipment.contains(ItemID.MITH_GRAPPLE_9419)) {
                log("Equip grapple");
                Equipment.equip(EquipmentSlot.ARROWS, ItemID.MITH_GRAPPLE_9419);
                return true;
            }

            GameObject pillar = GameObjects.closest(x -> x.hasAction("Grapple"));
            if (pillar != null) {
                log("Grapple into eyrie");
                pillar.interact("Grapple");
                Sleep.sleep(300);
            }
            return true;
        }

        if (KREE_BOSS_ROOM.contains(Players.getLocal())) return false;

        if (!INFRONT_OF_DOOR.equals(Players.getLocal().getTile())) {
            if (Walking.shouldWalk()) Walking.walkExact(INFRONT_OF_DOOR);
            return true;
        }

        // todo check we are on the correct team world, and that we are a member of a team in the first place
        GodWarsTeam godWarsTeam = GodWarsClient.getTeam(GodWarsBosses.KREE);
        if (godWarsTeam == null) {
            log("No team found, searching for one");
            Sleep.sleep(2_500);
            // todo request a team
            return true;
        }

        if (Worlds.getCurrentWorld() != godWarsTeam.world) {
            log("Hop to team world " + godWarsTeam.world);
            WorldHopper.hopWorld(godWarsTeam.world);
            Sleep.sleep(25_000);
            return true;
        }


        if (Combat.getHealthPercent() < 100) {
            log("Potting to full");
            // trusting i have these, if we dont we have bigger problems then the NPE
            ItemVariants.SARADOMIN_BREW.getItem().interact("Drink");
            return true;
        }

        int missingRange = Skills.getRealLevel(Skill.RANGED) - Skills.getBoostedLevel(Skill.RANGED);
        if (missingRange > 0 || Skills.getBoostedLevel(Skill.PRAYER) < 20) {
            log("restoring");
            ItemVariants.SUPER_RESTORE.getItem().interact("Drink");
            return true;
        }

        if (Combat.isAutoRetaliateOn()) {
            log("Turn off auto retaliate");
            Combat.toggleAutoRetaliate(false);
            return true;
        }

        GameObject door = GameObjects.closest("Big door");
        if (door != null) {
            PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MISSILES);
            log("Enter Kree Room");

            if (Players.all(x -> TEAMMATE_AREA.contains(x) && !Players.getLocal().equals(x) && godWarsTeam.isMember(x))
                    .size() >= settings.requiredTeammates) {
                log("No team mate present, waiting for someone to show up");
                PrayerUtils.disableAll();
                Sleep.sleep(3000);
                Client.setIdleTime(0);
                return true;
            }

            // reset timer because they wont be synced on entrance
            door.interact("Open");
            Sleep.sleepUntil(() -> KREE_BOSS_ROOM.contains(Players.getLocal()), 1600);
        }
        return true;
    }
}
