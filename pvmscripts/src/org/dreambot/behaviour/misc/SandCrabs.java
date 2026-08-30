package org.dreambot.behaviour.misc;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.CondHelper;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.pathaware.ShipWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.training.slayer.Helper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Accessors(chain = true)
public class SandCrabs extends Fractal {
    public SandCrabs(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Sandcrabs");
        addCrabClaw();
    }

    public static SandCrabs getRange(Supplier<Boolean> cond) {
        SandCrabs rangeCrabs = new SandCrabs(cond);
        rangeCrabs.setEquipmentLoadout(new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.SHORTBOW)

                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 20)
                .addItem(EquipmentSlot.WEAPON, ItemID.WILLOW_SHORTBOW)

                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 40, 20))

                .addItem(EquipmentSlot.WEAPON, ItemID.YEW_SHORTBOW)

                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50 && Skills.getRealLevel(Skill.RANGED) >= 40)

                .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW)

                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 50)

                .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                //  .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)
                // .setRefill(5)
                // .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 61)

                .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.IRON_ARROW, 1, 500))
                .setRefill(2000)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 20)

                .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.MITHRIL_ARROW, 1, 500))
                .setRefill(2000)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 1000, 20))

                // legs
                .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
                .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40))
                .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50))
                .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60))
                .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70))


                .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY).setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50)
                .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
                .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY).setRefill(5)
                .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

                .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                .setEnabledCondition(() -> OwnedItems.containsAny(
                        Arrays.stream(ItemVariants.AVAS.getIds()).mapToInt(x -> x).toArray())
                )

                .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

                .addItem(EquipmentSlot.AMULET, ItemVariants.SKILLS_NECKLACE)
                .setRefill(5)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .setRefill(5)
        );

        rangeCrabs.setInventoryLoadout(
                new InventoryLoadout()
                        .addItem(ItemID.JUG_OF_WINE, 1, 12)
                        .setRefill(1000)
//                .addItem(ItemVariants.RANGE_POTION)
//                .setRefill(50)
//                .setEnabledCondition(() -> ScriptSettings.getSettingsData().useBoostPotions)
        );
        return rangeCrabs;
    }

    private boolean melee;

    public static SandCrabs getMelee(Supplier<Boolean> cond) {
        SandCrabs melee = new SandCrabs(cond);
        melee.melee = true;

        melee.setEquipmentLoadout(
                new EquipmentLoadout()
                        // rune, adamant, mithril, iron chestplate
                        .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
                        .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 20)
                        .setRefill(5)
                        .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY)
                        .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 30, 20))
                        .setRefill(5)
                        .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY)
                        .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 40, 30))
                        .setRefill(5)
                        .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY)
                        .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 100, 40))
                        .setRefill(5)

                        // rune adamant mithril iron platelegs
                        .addItem(EquipmentSlot.LEGS, ItemID.IRON_PLATESKIRT)
                        .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 20)
                        .setRefill(5)
                        .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT)
                        .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 30, 20))
                        .setRefill(5)
                        .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT)
                        .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 40, 30))
                        .setRefill(5)
                        .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT)
                        .setEnabledCondition(() -> Helper.skillBetween(Skill.DEFENCE, 100, 40))
                        .setRefill(5)

                        // dragon sword or rune sword or mithril
                        .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
                        .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)
                        .setRefill(5)
                        .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
                        .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 30, 20))
                        .setRefill(5)
                        .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR)
                        .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 40, 30))
                        .setRefill(5)
                        .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
                        .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 40)
                        .setRefill(5)
                        // todo dragon sword
//            .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SWORD)
//            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 100, 60) && !ScriptSettings.getSettingsData().useWhip)
//            .setRefill(5)
//                        .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
//                        .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 100, 70))

                        // glory
                        .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                        .setRefill(5)

                        .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                        .setRefill(5)

        );

        melee.setInventoryLoadout(new InventoryLoadout()
                .addItem(ItemID.SHARK, 1, 20)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.HITPOINTS) > 40)
                .setRefill(200)
                .addItem(ItemID.LOBSTER, 1, 20)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.HITPOINTS) <= 40)
                .setRefill(200)
                .addItem(ItemVariants.STRENGTH_POTION, 1, 6)
                .setRefill(50)
        );

        return melee;
    }


    private static boolean hasAddedCrabclawNode = false;

    private void addCrabClaw() {
        if (hasAddedCrabclawNode) return;
        hasAddedCrabclawNode = true;
        log("Add crabclaw isle nodes");
        AbstractWebNode webNode0 = new BasicWebNode(1762, 3443, 0);
        AbstractWebNode webNode1 = new BasicWebNode(1758, 3436, 0);
        AbstractWebNode webNode2 = new BasicWebNode(1759, 3430, 0);
        AbstractWebNode webNode3 = new BasicWebNode(1762, 3424, 0);
        AbstractWebNode webNode4 = new BasicWebNode(1768, 3420, 0);
        AbstractWebNode webNode5 = new BasicWebNode(1773, 3415, 0);
        AbstractWebNode webNode6 = new BasicWebNode(1777, 3412, 0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);
        webNode1.addDualConnections(webNode2);
        webNode2.addDualConnections(webNode1);
        webNode2.addDualConnections(webNode3);
        webNode3.addDualConnections(webNode2);
        webNode3.addDualConnections(webNode4);
        webNode4.addDualConnections(webNode3);
        webNode4.addDualConnections(webNode5);
        webNode5.addDualConnections(webNode4);
        webNode5.addDualConnections(webNode6);
        webNode6.addDualConnections(webNode5);

        AbstractWebNode[] webNodes = {webNode0, webNode1, webNode2, webNode3, webNode4, webNode5, webNode6,};
        WebFinder wf = WebFinder.getWebFinder();
        wf.addWebNodes(webNodes);

        ShipWebNode offCrabClaw = new ShipWebNode(new Tile(1779, 3418), new Tile(1784, 3458), "Sandicrahb", "Travel", () -> true);
        wf.getNearest(new Tile(1784, 3458), 15).addDualConnections(offCrabClaw);
        wf.addWebNode(offCrabClaw);

    }

    @Setter
    int defenceTarget = 0;

    Tile[] spots = new Tile[]{
            new Tile(1738, 3468),
            new Tile(1734, 3469),
            new Tile(1749, 3469),
            //new Tile(1865, 3468),
            new Tile(1773, 3461),
            new Tile(1776, 3468)
    };
    Tile choosenSpot = spots[ShuffleFractal.getLoginValue() % spots.length];
    Timer timeAroundCompetition = new Timer(30 * 1000);
    List<Integer> sleepyRockIds = Arrays.asList(7207, 5936);
    List<Integer> crabIds = Arrays.asList(7206, 5935);
    boolean resetLock = false;


    @Getter
    @Setter
    public Supplier<CombatStyle> styleSupplier = () -> {
        int atk = Skills.getRealLevel(Skill.ATTACK);
        int str = Skills.getRealLevel(Skill.STRENGTH);
        int def = Skills.getRealLevel(Skill.DEFENCE);
        if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
        if (atk <= def) return CombatStyle.ATTACK;
        return CombatStyle.DEFENCE;
    };

    Timer mouseMove = new Timer(130 * 1000);

    @Override
    public int onLoop() {
        if (mouseMove.finished()) {
            mouseMove.reset();
            Point p = Mouse.getPosition();
            p.translate(Calculations.random(-20, 20), Calculations.random(-20, 20));
            Mouse.move(p);
            Tabs.open(Tab.values()[Calculations.random(1, 4)]);
        }

        if (!Combat.isAutoRetaliateOn()) {
            Logger.info("Toggle auto retaliate");
            if (Widgets.isOpen()) Widgets.closeAll();
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getNormal();
        }

        if (Combat.getHealthPercent() <= 50) {
            Logger.info("Eating");
            Inventory.interact(x -> x.getId() == ItemID.SHARK || x.getId() == ItemID.LOBSTER || x.getId() == ItemID.JUG_OF_WINE);
            return ReactionGenerator.getNormal();
        }

        if (choosenSpot.distance() < 10 && Skills.getBoostedLevel(Skill.STRENGTH) == Skills.getRealLevel(Skill.STRENGTH)) {
            Item strPot = ItemVariants.STRENGTH_POTION.getItem();
            if (strPot != null) {
                strPot.interact("Drink");
                Antiban.sleepUntil(() -> Skills.getBoostedLevel(Skill.STRENGTH) > Skills.getRealLevel(Skill.STRENGTH), 2400);
            }

        }

        if (choosenSpot.distance(Players.getLocal().getTile()) >= 32) {
            Logger.info("Away from spot, reset");
            resetLock = false;
        }

        if (Players.all(x -> choosenSpot.equals(x.getTile())).size() <= 1) {
            timeAroundCompetition.reset();
        }

        if (!melee) {
            if (Skills.getRealLevel(Skill.DEFENCE) < defenceTarget) {
                if (Widgets.isOpen()) Widgets.closeAll();
                if (Combat.getCombatStyle() != CombatStyle.RANGED_DEFENCE) {
                    Logger.info("Setting to range defence");
                    Combat.setCombatStyle(CombatStyle.RANGED_DEFENCE);
                    return ReactionGenerator.getNormal();
                }
            } else {
                if (Widgets.isOpen()) Widgets.closeAll();
                if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
                    Logger.info("Setting to range rapid");
                    Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
                    return ReactionGenerator.getNormal();
                }
            }
        } else {
            CombatStyle style = styleSupplier.get();
            if (Combat.getCombatStyle() != style && !Equipment.contains(ItemID.ABYSSAL_WHIP)) {
                Logger.info("Set attack style: " + style);
                if (Widgets.isOpen()) Widgets.closeAll();
                Combat.setCombatStyle(style);
                return ReactionGenerator.getNormal();
            }
        }


        if (timeAroundCompetition.finished()) {
            Logger.info("Hopping away from competition");
            WorldHopper.hopWorld(Worlds.getRandomWorld(w -> w.isMembers()
                    && w.isNormal()
                    && w.getMinimumLevel() < Skills.getTotalLevel())
            );
            return ReactionGenerator.getNormal();
        }

        NPC looseCrab = NPCs.closest(x -> x.distance() < 5 && crabIds.contains(x.getId()) && !x.isInCombat());
        if (!Players.getLocal().isInCombat() && looseCrab != null) {
            looseCrab.interact("Attack");
            Antiban.sleepUntil(() -> Players.getLocal().isInCombat(), 2400);
        }

        if (resetLock || (NPCs.all(x -> x.distance() < 3 && sleepyRockIds.contains(x.getId())).size() >= 2 && !Players.getLocal().isInCombat())) {
            Sleep.sleep(1200);
            if (!resetLock && !(NPCs.all(x -> x.distance() < 3 && sleepyRockIds.contains(x.getId())).size() >= 2 && !Players.getLocal().isInCombat())) {
                return ReactionGenerator.getNormal();
            }
            Logger.info("Resetting Agro");
            resetLock = true;
            if (Walking.shouldWalk()) Walking.walk(Players.getLocal().getTile().translate(0, 10));
            return ReactionGenerator.getNormal();
        }


        Logger.info("Walk to spot");
        if (Walking.shouldWalk()) Walking.walk(choosenSpot);
        return ReactionGenerator.getNormal();
    }
}
