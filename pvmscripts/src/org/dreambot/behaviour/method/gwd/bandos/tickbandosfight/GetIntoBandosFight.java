package org.dreambot.behaviour.method.gwd.bandos.tickbandosfight;

import org.dreambot.alerts.Alerts;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.bandos.BandosConsts;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.scriptdata.BandosSettings;

import java.awt.*;

public class GetIntoBandosFight extends TickDecision {
    public static final Area BANDOS_ROOM = new Area(2863, 5373, 2878, 5350, 2);
    final Tile OUTSIDE_DOOR = new Tile(2862, 5354, 2);
    final BandosSettings settings;

    public GetIntoBandosFight(BandosSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean evaluate() {
        // pray against rocks and wolves and such otw in
        if (!BANDOS_ROOM.contains(Players.getLocal())) {
            log("Get into bandos room");
            // gear switch
            if (Inventory.contains(x -> !Equipment.contains(x.getId()) && BandosConsts.primaryWeapons.contains(x.getId()))) {
                log("Equpping primary");
                Inventory.interact(x -> !Equipment.contains(x.getId()) && BandosConsts.primaryWeapons.contains(x.getId()));
                return true;
            }

            if (Inventory.contains(ItemID.ODIUM_WARD)) {
                Inventory.interact(ItemID.ODIUM_WARD);
            }

            log("Not in boss room");
            if (!OUTSIDE_DOOR.equals(Players.getLocal().getTile())) {
                log("Get to outside door");
                PrayerUtils.disable(Prayer.values());
                if (Walking.shouldWalk()) Walking.walk(OUTSIDE_DOOR);
                return true;
            }

            if (Combat.isAutoRetaliateOn()) {
                log("Toggle off auto retaliate");
                Combat.toggleAutoRetaliate(false);
                return true;
            }

            // world ping enforcement
            boolean someoneElse = Players.all().stream().anyMatch(BANDOS_ROOM::contains);
            NPC bandos = NPCs.closest(BandosConsts.BANDOS);
            Character bandosTarget = null;
            if (bandos != null) {
                bandosTarget = bandos.getInteractingCharacter();
            }
            // check to see if anyone is targetted by bandos
            boolean canEnter = bandos != null && (bandosTarget == null || bandosTarget.equals(Players.getLocal()));
            if (!canEnter || Worlds.getCurrent().getPing() > settings.maxWorldPing) {
                log("Someone else is fighting Bandos in this world, or the world ping is over 400, hopping.");
                // hop
                World hopTuah = Worlds.getRandomWorld(x -> x.isNormal()
                        && x.isMembers()
                        && x.getWorld() != 330
                        && x.getMinimumLevel() < Skills.getTotalLevel()
                        && x.getPing() < settings.maxWorldPing
                );
                if (hopTuah == null) {
                    log("Could not find a world with a low enough ping, try increasing maxWorldPing setting");
                    Alerts.addAlert(6_000, Color.YELLOW, "Could not find a world with a low enough ping, try increasing maxWorldPing setting");
                    return true;
                }
                // todo make sure the reconnect doesnt invoke any inv loadouts
                WorldHopper.hopWorld(hopTuah);
                Sleep.sleepUntil(() -> Worlds.getCurrentWorld() != hopTuah.getWorld() && !GetMoreAvas.shouldGetMore(), 14_000);
                return true;
            }

            GameObject door = GameObjects.closest("Big door");
            if (door != null) {
                log("Enter door");
                BandosWalkDecision.firstEnter = true;
                BandosWalkDecision.firstLap = true;
                door.interact();
                Sleep.sleepUntil(() -> BANDOS_ROOM.contains(Players.getLocal()), 3400);
            }
            return true;
        }
        return false;
    }


}
