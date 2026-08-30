package org.dreambot.behaviour.method.pirates;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.filter.Filter;
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
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.misc.SmartLootEvent;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.EventExitCondition;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.scriptdata.ZombieChestSettings;
import org.dreambot.scriptdata.ZombiePirateSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class KillPirates extends Fractal {
    public static final Area CHAOS_TEMPLE = new Area(
            new Tile(3232, 3631, 0),
            new Tile(3241, 3632, 0),
            new Tile(3250, 3619, 0),
            new Tile(3258, 3611, 0),
            new Tile(3252, 3601, 0),
            new Tile(3247, 3591, 0),
            new Tile(3241, 3595, 0),
            new Tile(3234, 3593, 0),
            new Tile(3226, 3599, 0),
            new Tile(3220, 3609, 0),
            new Tile(3217, 3615, 0),
            new Tile(3221, 3621, 0),
            new Tile(3228, 3622, 0),
            new Tile(3229, 3629, 0));

    ZombiePirateSettings settings;

    public KillPirates(Supplier<Boolean> acceptCondition, ZombiePirateSettings settings) {
        super(acceptCondition);
        this.settings = settings;
    }

    Timer eatTimer = new Timer(1200);
    // todo something for loot bag
    Filter<GroundItem> lootFilter = x -> CHAOS_TEMPLE.contains(x) && LivePrices.get(x.getId()) * x.getAmount() > settings.minLootValue;
    List<Integer> foodIDs = Arrays.asList(
            ItemID.JUG_OF_WINE,
            ItemID.BLIGHTED_MANTA_RAY,
            ItemID.LOBSTER
    );

    Area FEROX_BRIDGE = new Area(3150, 3640, 3161, 3629);

    @Override
    public int onLoop() {
        if (CombatUtil.get().isTeleblocked() && (!Combat.isInWild() || FEROX_BRIDGE.contains(Players.getLocal()))) {
            log("Not in wild & teleblocked, removing that.");
            WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isMembers() && x.isNormal() && x.getWorld() != 401 && x.getMinimumLevel() < Skills.getTotalLevel()));
        }

        if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)
                && Skills.getBoostedLevel(Skill.PRAYER) > 1
                && CHAOS_TEMPLE.distance(Players.getLocal().getTile()) < 35) {
            log("Enable protect from magic");
            Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            return ReactionGenerator.getNormal();
        }

        if (!CHAOS_TEMPLE.contains(Players.getLocal())) {
            slowLog("Walk to chaos temple");
            if (Walking.shouldWalk()) Walking.walk(CHAOS_TEMPLE);
            return ReactionGenerator.getNormal();
        }

        if (Walking.getRunEnergy() > 5 && !Walking.isRunEnabled()) Walking.toggleRun();

        if (Skills.getBoostedLevel(Skill.PRAYER) < 15) {
            log("Recharge prayer ");
            rechargePrayer();
            return ReactionGenerator.getNormal();
        }

        // eat
        int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (eatTimer.finished() && missingHP >= 20) {
            log("Eating.");
            Inventory.interact(x -> foodIDs.contains(x.getId()));
            return ReactionGenerator.getQuick();
        }

        if (Skill.ATTACK.getLevel() < 70 && Equipment.contains(x -> x.getName().contains("scim") && Combat.getCombatStyle() != CombatStyle.ATTACK)) {
            log("Set attack style");
            Combat.setCombatStyle(CombatStyle.ATTACK);
        }

        if (Equipment.contains(ItemID.SARADOMIN_SWORD)) {
            if (Skills.getRealLevel(Skill.STRENGTH) < 99) {
                if (Combat.getCombatStyle() != CombatStyle.STRENGTH) Combat.setCombatStyle(CombatStyle.STRENGTH);
            } else if (Skills.getRealLevel(Skill.ATTACK) < 99) {
                if (Combat.getCombatStyle() != CombatStyle.ATTACK) Combat.setCombatStyle(CombatStyle.ATTACK);
            } else {
                if (Combat.getCombatStyle() != CombatStyle.DEFENCE) Combat.setCombatStyle(CombatStyle.DEFENCE);
            }
        }

        // boost
        ZombiePirateSettings s = SettingsRepository.findInstanceOf(new ZombiePirateSettings());
        if (s.useBoostPrayer) {
            Prayers.toggle(true, s.boostPrayer);
        }

        // loot
        Supplier<List<GroundItem>> lootSupplier = () -> GroundItems.all(lootFilter);
        if (!lootSupplier.get().isEmpty()) {
            log("Looting");
            new SmartLootEvent(lootSupplier, ItemID.JUG, ItemID.JUG_OF_WINE, ItemID.BLIGHTED_MANTA_RAY, ItemID.LOBSTER)
                    .addExitCondition(new EventExitCondition(() -> Skills.getBoostedLevel(Skill.PRAYER) < 15, "Needs prayer"))
                    .executed();
            return ReactionGenerator.getNormal();
        }

        // attack zombie
        Character c = Players.getLocal().getInteractingCharacter();
        if (c == null || !c.getName().equalsIgnoreCase("zombie pirate")) {
            NPC zPirate = NPCs.closest(x -> CHAOS_TEMPLE.contains(x) && x.getName().equalsIgnoreCase("zombie pirate"));
            if (zPirate != null) {
                log("Attacking " + zPirate);
                zPirate.interact("Attack");
                Sleep.sleepUntil(() -> zPirate.equals(Players.getLocal().getInteractingCharacter()), 2400);
            }
        }

        return ReactionGenerator.getNormal();
    }

    private boolean isAttackingMe(NPC npc) {
        Character c = npc.getInteractingCharacter();
        return c == null || c.equals(Players.getLocal());
    }

    private void rechargePrayer() {
        log("Altar mode recharge prayer");
        GameObject altar = GameObjects.closest(x -> x.hasAction("Pray-at"));
        if (altar != null) {
            altar.interact();
            Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) == Skills.getRealLevel(Skill.PRAYER), 1400);
        }

//        Item pot = ItemVariants.BLIGHTED_SUPER_RESTORE.getItem();
//        if (pot != null) {
//            log("Blighted restore recharge");
//            pot.interact();
//            Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) == Skills.getRealLevel(Skill.PRAYER), 2300);
//        }
    }
}
