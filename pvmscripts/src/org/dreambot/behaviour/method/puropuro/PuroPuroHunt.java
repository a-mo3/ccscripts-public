package org.dreambot.behaviour.method.puropuro;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.scriptdata.PuroPuroSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PuroPuroHunt extends Fractal {
    // we pretty much just want to run around the edges, collecting whatevers on the edges that matches our criteria
    Map<Tile, Tile> corners = new HashMap<>();

    PuroPuroSettings settings;

    public PuroPuroHunt(PuroPuroSettings settings) {
        this.settings = settings;
        setSimpleName("Puro puro");
        PuroPuroNodes.init();
        corners.put(NORTH_EAST, SOUTH_EAST);
        corners.put(SOUTH_EAST, SOUTH_WEST);
        corners.put(SOUTH_WEST, NORTH_WEST);
        corners.put(NORTH_WEST, NORTH_EAST);
    }

    // checks impling name and sees if we have the level and have this type enabled in settings
    private boolean isAcceptableImpling(String implingName) {
        int lvl = Skills.getRealLevel(Skill.HUNTER);
        if (lvl >= 17 && implingName.startsWith("Baby")) return settings.babyImpling;
        if (lvl >= 22 && implingName.startsWith("Young")) return settings.youngImpling;
        if (lvl >= 28 && implingName.startsWith("Gourmet")) return settings.gourmetImpling;
        if (lvl >= 36 && implingName.startsWith("Earth")) return settings.earthImpling;
        if (lvl >= 42 && implingName.startsWith("Essence")) return settings.essenceImpling;
        if (lvl >= 50 && implingName.startsWith("Eclectic")) return settings.eclecticImpling;
        if (lvl >= 58 && implingName.startsWith("Mature")) return settings.natureImpling;
        if (lvl >= 65 && implingName.startsWith("Magpie")) return settings.magpieImpling;
        if (lvl >= 74 && implingName.startsWith("Ninja")) return true;
        if (lvl >= 80 && implingName.startsWith("Crystal")) return true;
        if (lvl >= 83 && implingName.startsWith("Dragon")) return true;
        if (lvl >= 89 && implingName.startsWith("Lucky")) return true;
        return false;
    }


    Tile NORTH_EAST = new Tile(2619, 4347);
    Tile SOUTH_EAST = new Tile(2619, 4292);
    Tile SOUTH_WEST = new Tile(2564, 4292);
    Tile NORTH_WEST = new Tile(2564, 4347);
    Tile spot = NORTH_EAST;

    Area[] impAreas = {
            // north
            new Area(2560, 4351, 2622, 4346),
            // east
            new Area(2618, 4353, 2622, 4289),
            // south
            new Area(2560, 4293, 2623, 4289),
            // west
            new Area(2559, 4351, 2565, 4287)
    };

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.MAGIC_BUTTERFLY_NET)) {
            log("Equip butterfly net");
            Inventory.interact(ItemID.MAGIC_BUTTERFLY_NET);
            return ReactionGenerator.getNormal();
        }

        if (settings.overworldCircles) {
            // make sure we aren't all on the scouted world
            log("Crop world " + CropCircleScouter.getWorld());
            if (Worlds.getCurrentWorld() == CropCircleScouter.getWorld()) {
                log("Get off the scouted world");
                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() == 0));
                return ReactionGenerator.getNormal();
            }
        }

        if (!Inventory.contains(ItemID.IMPLING_JAR)) {
            log("No jars go bank");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getNormal();
        }

        // find an out ring eligible impling
        NPC imp = NPCs.closest(x -> x.distance() < 5
                && Arrays.stream(impAreas).anyMatch(area -> area.contains(x))
                && isAcceptableImpling(x.getName()));

        if (imp != null) {
            log("Found imp " + imp);
            imp.interact();
            Sleep.sleepUntil(() -> !imp.exists() || Arrays.stream(impAreas).anyMatch(a -> a.contains(imp)), 1000);
            return ReactionGenerator.getNormal();
        }

        // run away in a circle, possibly add option to camp a spawn point
        if (spot.equals(Players.getLocal().getTile())) spot = corners.get(spot);
        if (Walking.shouldWalk()) {
            log("Walking to " + spot);
            // minimap is disabled but dreambot walker will try to use that when mouse is enabled.
            Walking.setDisableMinimap(true);
            Walking.walk(spot);
            Walking.setDisableMinimap(false);
        }
        return ReactionGenerator.getNormal();
    }
}
