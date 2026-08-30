package org.dreambot.behaviour.method.rogueschest;


import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.muling.Log;
import org.dreambot.scriptdata.RoguesChestSettings;
import org.dreambot.settings.timing.ReactionGenerator;

public class RogueChestAntiPK extends Fractal {
    public static final Tile CLOSED_DOOR_TILE = new Tile(3279, 3939, 0);

    final RoguesChestSettings settings;

    public RogueChestAntiPK(RoguesChestSettings settings) {
        super(() -> Client.isLoggedIn() && (CombatUtil.getThreat() != null || attackedWorld == Worlds.getCurrentWorld()));
        this.settings = settings;
    }

    private static int attackedWorld = -1;
    public static int count = 0;

    public static void setAttackedWorld(int atkWorld) {
        if (atkWorld > 0 && atkWorld != attackedWorld) count++;
        attackedWorld = atkWorld;
    }

    int stairCaseY = 3936;
    Timer eatTimer = new Timer(800);
    Timer prayerTimer = new Timer(800);

    @Override
    public int onLoop() {
        if (CombatUtil.getThreat() != null) setAttackedWorld(Worlds.getCurrentWorld()); //
        if (CombatUtil.getThreat() == null && Players.getLocal().getZ() == 0) setAttackedWorld(-1);
        if (Worlds.getCurrentWorld() != attackedWorld) setAttackedWorld(-1); // successfully hopped

        if (Skills.getBoostedLevel(Skill.PRAYER) < 15 && prayerTimer.elapsed() > 1200) {
            Item restore = ItemVariants.BLIGHTED_SUPER_RESTORE.getItem();
            if (restore != null) restore.interact("Drink");
            prayerTimer.reset();
        }

        int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (missingHP >= 5 && eatTimer.elapsed() >= 800) {
            Log.info("Eat");
            if (Inventory.contains(ItemID.BLIGHTED_MANTA_RAY))
                Inventory.interact(ItemID.BLIGHTED_MANTA_RAY, "Eat");
            if (missingHP > 20 && Inventory.contains(ItemID.BLIGHTED_KARAMBWAN))
                Inventory.interact(ItemID.BLIGHTED_KARAMBWAN, "Eat");
            eatTimer.reset();
        }

        // pray prot magic
        if (Skills.getBoostedLevel(Skill.PRAYER) > 0) {
            // dont need to pray if we are just waiting on the stairs
            if (CombatUtil.getThreat() != null) {
                log("Activate prot magic");
                Prayers.toggle(!settings.dmm, Prayer.PROTECT_ITEM);
            } else {
                log("deactivate prot magic");
            }
        }

        // logout if you can
        if (!CombatUtil.get().isOnLogoutTimer() && !settings.dmm) {
            log("logout " + settings.dmm);
            int world = Worlds.getCurrentWorld();
            int newWorld =  Worlds.getRandomWorld(x -> x.isMembers()
                    && x.getMinimumLevel() < Skills.getTotalLevel()
                    && (settings.dmm ? x.isDeadmanMode() : x.isNormal())
                    && x.getWorld() != 401).getWorld();
            log("Choose world " + newWorld);
            WorldHopper.hopWorld(newWorld);
            Sleep.sleepUntil(() -> Client.isLoggedIn() && Worlds.getCurrentWorld() != world, 7800);
            return ReactionGenerator.getNormal();
        }

        GameObject closedDoor = GameObjects.closest(x -> x.getId() == 14749 && x.getTile().equals(CLOSED_DOOR_TILE));
        if (closedDoor != null && closedDoor.hasAction("Open")) {
            log("Opening door to stairs");
            closedDoor.interact("Open");
            Sleep.sleepUntil(() -> GameObjects.closest(x -> x.getId() == 14749
                            && x.getTile().equals(CLOSED_DOOR_TILE)) == null,
                    2000);
            return ReactionGenerator.getNormal();
        }

        // get staircase, climb up/down randomly
        GameObject stairs = GameObjects.closest(x -> x.getY() == stairCaseY && x.getName().equalsIgnoreCase("staircase"));
        if (stairs == null) {
            log("Failed to find staircase");
            return ReactionGenerator.getQuick();
        }

        if (CombatUtil.getThreat() == null) {
            log("No threat found, waiting...");
            return ReactionGenerator.getQuick();
        }

        int plane = Players.getLocal().getZ();
        if (plane == 2) {
            int roll = Calculations.random(0, 1);
            log("Both up and down rolled " + roll);
            stairs.interact(roll > 0 ? "Climb-up" : "Climb-down");
        } else if (plane == 0 || plane == 1) {
            log("Only up");
            stairs.interact("Climb-up");
        } else {
            log("Only down");
            stairs.interact("Climb-down");
        }

        return ReactionGenerator.getNormal();
    }
}
