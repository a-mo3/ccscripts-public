package org.dreambot.behaviour.method.gwd.zilyana.tickzilyanafight;

import org.dreambot.alerts.Alerts;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
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
import org.dreambot.behaviour.method.gwd.zilyana.ZilyanaConsts;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.scriptdata.ZilyanaSettings;

import java.awt.*;

public class GetIntoZilyanaFight extends TickDecision {
    public static final Area ZILYANA_BOSS_ROOM = new Area(2883, 5276, 2908, 5257);
    public static final Tile OUTSIDE_DOOR = new Tile(2909, 5265, 0);
    final ZilyanaSettings settings;

    Area WILDY_GWD = new Area(3009, 10172, 3073, 10110);

    public GetIntoZilyanaFight(ZilyanaSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean evaluate() {
        // pray against rocks and wolves and such otw in
        if (!ZILYANA_BOSS_ROOM.contains(Players.getLocal())) {
            // gear switch
            if (Inventory.contains(x -> !Equipment.contains(x.getId()) && ZilyanaConsts.primaryWeapons.contains(x.getId()))) {
                log("Equpping primary");
                Inventory.interact(x -> !Equipment.contains(x.getId()) && ZilyanaConsts.primaryWeapons.contains(x.getId()));
                return true;
            }

            log("Not in boss room");
            if (!OUTSIDE_DOOR.equals(Players.getLocal().getTile())) {
                log("Get to outside door");

                if (WILDY_GWD.contains(Players.getLocal())) {
                    log("Go to edge from wildy gwd");
                    PrayerUtils.disable(Prayer.values());
                    if (Walking.shouldWalk()) Walking.walk(BankLocation.EDGEVILLE);
                    return true;
                }

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
            boolean someoneElse = Players.all().stream().anyMatch(ZILYANA_BOSS_ROOM::contains);
            NPC zil = NPCs.closest("Commander Zilyana");
            Character zilsTarget = null;
            if (zil != null) {
                zilsTarget = zil.getInteractingCharacter();
            }
            // check to see zilyana and see shes not targetting anyone before you enter, otherwise you can crash if the player is in the unrendered part of the room
            boolean canEnter = zil != null && (zilsTarget == null || zilsTarget.equals(Players.getLocal()));
            if (!canEnter || Worlds.getCurrent().getPing() > settings.maxWorldPing) {
                log("Someone else is fighting Zilyana in this world, or the world ping is over 400, hopping.");
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
                door.interact();
                Sleep.sleepUntil(() -> ZILYANA_BOSS_ROOM.contains(Players.getLocal()), 3400);
            }
            return true;
        }
        return false;
    }


}
