package org.dreambot.behaviour.method.gwd.kree;

import org.dreambot.alerts.Alerts;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.ProjectileListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.scriptdata.KreearraSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static org.dreambot.behaviour.method.gwd.kree.GetKreeKC.ARMADYL_EYRiE;
import static org.dreambot.behaviour.method.gwd.kree.GetKreeKC.THROW_GRAPPLE_AREA;
import static org.dreambot.behaviour.method.spindel.range.RangeAttackSpindel.RIGOUR_UNLOCKED;

public class KillKreearra extends Fractal implements AnimationListener, SpawnListener, ItemContainerListener, ProjectileListener {
    public static final Area KREE_BOSS_ROOM = new Area(2823, 5309, 2842, 5296, 2);
    final String KREE = "Kree'arra";
    final String MELEE_GUARD = "Flight Kilisa";
    final String MAGIC_GUARD = "Wingman Skree";
    final String RANGED_GUARD = "Flockleader Geerin";
    Tile infrontOfDoor = new Tile(2839, 5294, 2);

    // im only certain this is dcb, but probably all cbows
    final int PLAYER_ATK_ANI = 7552;
    final int PLAYER_CHIN_ANI = 7618;

    int meleeGuardTiming = -1;
    int rangeGuardTiming = -1;
    int magicGuardTiming = -1;
    int kreeTiming = -1;

    int lastPlayerAttack = -1;

    public static int killCount = 0;

    public static int kreearraGP;
    final Timer consumeDelay = new Timer(800);
    final KreearraSettings settings;

    Timer takeTimer = new Timer(300);
    List<Integer> food = Arrays.asList(
            ItemID.MUSHROOM_POTATO,
            ItemID.MANTA_RAY
    );

    public KillKreearra(Supplier<Boolean> acceptCondition, KreearraSettings settings) {
        super(acceptCondition);
        this.settings = settings;
        Client.getInstance().addEventListener(this);

        this.paintArraySupplier = () -> {
            NPC kree = NPCs.closest(KREE);
            return new String[]{
                    "Dist: " + (kree == null ? "0 " : String.valueOf(kree.distance())),
                    "Dist: " + (kree == null ? "0 " : String.valueOf(kree.distance())),
                    "Since lst PAtk " + (Client.getGameTick() - lastPlayerAttack),
                    "5Tick " + Client.getGameTick() % 5,
                    "Magic " + magicGuardTiming,
                    "Range " + rangeGuardTiming,
                    "Melee " + meleeGuardTiming,
                    "3Tick " + Client.getGameTick() % 3,
                    "Kree " + kreeTiming

            };
        };
    }

    @Override
    public int onLoop() {
        if (!ARMADYL_EYRiE.contains(Players.getLocal()) && Inventory.contains(ItemID.ECUMENICAL_KEY)) {
            if (!THROW_GRAPPLE_AREA.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(THROW_GRAPPLE_AREA);
                return ReactionGenerator.getQuick();
            }

            // equip grapple
            if (!Equipment.contains(ItemID.MITH_GRAPPLE_9419)) {
                log("Equip grapple");
                Equipment.equip(EquipmentSlot.ARROWS, ItemID.MITH_GRAPPLE_9419);
                return ReactionGenerator.getNormal();
            }

            GameObject pillar = GameObjects.closest(x -> x.hasAction("Grapple"));
            if (pillar != null) {
                log("Grapple into eyrie");
                pillar.interact("Grapple");
                Sleep.sleep(300);
            }
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.DIAMOND_DRAGON_BOLTS_E)) {
            Equipment.equip(EquipmentSlot.ARROWS, ItemID.DIAMOND_DRAGON_BOLTS_E);
        }

        if (Inventory.contains(ItemID.DIAMOND_BOLTS_E)) {
            Equipment.equip(EquipmentSlot.ARROWS, ItemID.DIAMOND_BOLTS_E);
        }

        if (Inventory.contains(ItemID.VIAL, ItemID.PIE_DISH)) {
            log("drop vials n waste");
            Inventory.dropAll(ItemID.PIE_DISH, ItemID.VIAL);
        }

        NPC kree = NPCs.closest(KREE);
        // pot up and enter
        if (!KREE_BOSS_ROOM.contains(Players.getLocal())) {
            // maybe equip crossbow before the attack logic

            if (!infrontOfDoor.equals(Players.getLocal().getTile())) {
                if (Walking.shouldWalk()) Walking.walkExact(infrontOfDoor);
                return ReactionGenerator.getNormal();
            }

            // todo world ping check
            // todo competition check

            if (Combat.getHealthPercent() < 100) {
                log("Potting to full");
                // trusting i have these, if we dont we have bigger problems then the NPE
                ItemVariants.SARADOMIN_BREW.getItem().interact("Drink");
                return ReactionGenerator.getNormal();
            }

            boolean someoneElse = Players.all().stream().anyMatch(KREE_BOSS_ROOM::contains);
            if (someoneElse) {
                log("Someone else is fighting kree in this world, hopping.");
                // hop
                World hopTuah = Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel());
                if (hopTuah == null) {
                    log("Could not find a world with a low enough ping, try increasing maxWorldPing setting");
                    Alerts.addAlert(6_000, Color.YELLOW, "Could not find a world with a low enough ping, try increasing maxWorldPing setting");
                    return 50;
                }
                // todo make sure the reconnect doesnt invoke any inv loadouts
                WorldHopper.hopWorld(hopTuah);
                return 12_500;
            }

            int missingRange = Skills.getRealLevel(Skill.RANGED) - Skills.getBoostedLevel(Skill.RANGED);
            if (missingRange > 0 || Skills.getBoostedLevel(Skill.PRAYER) < 20) {
                log("restoring");
                ItemVariants.SUPER_RESTORE.getItem().interact("Drink");
                return ReactionGenerator.getNormal();
            }

            if (Combat.isAutoRetaliateOn()) {
                log("Turn off auto retaliate");
                Combat.toggleAutoRetaliate(false);
                return ReactionGenerator.getNormal();
            }

            GameObject door = GameObjects.closest("Big door");
            if (door != null) {
                toggle(true, Prayer.PROTECT_FROM_MISSILES);
                log("Enter Kree Room");
                // reset timer because they wont be synced on entrance
                door.interact("Open");
                Sleep.sleepUntil(() -> KREE_BOSS_ROOM.contains(Players.getLocal()), 1600);
            }
            return ReactionGenerator.getNormal();
        }

        // eat food
        if (consumeDelay.finished()) {
            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 50) {
                if (Inventory.contains(x -> food.contains(x.getId()))) {
                    log("Eating");
                    Inventory.interact(x -> food.contains(x.getId()), "Eat");
                    consumeDelay.reset();
                } else {
                    Item brew = ItemVariants.SARADOMIN_BREW.getItem();
                    if (brew != null) {
                        log("Drinking brew");
                        consumeDelay.reset();
                        brew.interact("Drink");
                        return 50;
                    }

                    log("Out of food leaving");
                    exitToGE();
                }
                return 50;
            }

            // super restoring after brew
            int missingRange = Skills.getRealLevel(Skill.RANGED) - Skills.getBoostedLevel(Skill.RANGED);
            if (missingRange > 0) {
                log("reduced range level from brew");
                int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
                int possibleRestore = (int) (Skills.getRealLevel(Skill.RANGED) * 0.25 + 8);
                // the amount by which another brew will reduce range
                int brewReduction = (int) (Skills.getBoostedLevel(Skill.RANGED) * 0.1 + 2);
                log(String.format("Missing %d ranged & %d HP, possible range restore: %d - brew reduction: %d",
                        missingRange, missingHP, possibleRestore, brewReduction));
                Item brew = ItemVariants.SARADOMIN_BREW.getItem();
                if (missingHP > 0 && possibleRestore - (missingRange + brewReduction) >= 0 && brew != null) {
                    log("We can sip another brew here");
                    brew.interact("Drink");
                    consumeDelay.reset();
                    return 50;
                } else {
                    Item restore = ItemVariants.SUPER_RESTORE.getItem();
                    if (restore != null) {
                        log("Drinking restore");
                        consumeDelay.reset();
                        restore.interact("Drink");
                    } else {
                        log("Leave no restore");
                        exitToGE();
                    }
                    return 50;
                }

            }

            // drink prayer
            Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
            if (Skills.getBoostedLevel(Skill.PRAYER) < 20) {
                if (prayerPot == null) {
                    log("No prayer pots using restores");
                    prayerPot = ItemVariants.SUPER_RESTORE.getItem();
                }
                if (prayerPot != null) {
                    log("Drink prayer");
                    Inventory.interact(prayerPot);
                    consumeDelay.reset();
                } else {
                    log("Out of prayer.");
                    exitToGE();
                }
            }

            // stamina
            Item staminaPot = ItemVariants.STAMINA_POTION.getItem();
            if (Walking.getRunEnergy() < 20) {
                if (staminaPot != null) {
                    log("Drinking stamina");
                    staminaPot.interact("Drink");
                    consumeDelay.reset();
                    return 50;
                }
            }
        }

        // if kree'arra is dead, kill guards
        if (kree == null) {
            // this will flick the minions
            GroundItem loot = GroundItems.all(KREE_BOSS_ROOM::contains)
                    .stream()
                    // if inventory is full still
                    .filter(x -> !Inventory.isFull() || (x.getItem().isStackable() && Inventory.contains(x.getId())))
                    // take summer pies and eat them for run
                    .filter(x -> food.contains(x.getId()) || (x.getItem().getLivePrice() + 1) * x.getAmount() > 1500)
                    .max(Comparator.comparingInt(x -> x.getAmount() * (x.getItem().getLivePrice() + 1)))
                    .orElse(null);
            if (loot != null) {
                if (takeTimer.finished()) {
                    loot.interact("Take");
                    takeTimer.reset();
                }
                return 50;
            }
            log("No kree - deal with minions");
            NPC melee = NPCs.closest(MELEE_GUARD);
            NPC magic = NPCs.closest(MAGIC_GUARD);
            NPC range = NPCs.closest(RANGED_GUARD);
            if (melee != null || magic != null || range != null) toggle(true, appropriateProtectionPrayer());

            if (Inventory.contains(x -> KreeConsts.secondaryWeapons.contains(x.getId()))) {
                if (Inventory.isFull() && Inventory.contains(ItemID.TOXIC_BLOWPIPE)) {
                    log("Drop cheapest for BP equip");
                    PVMUtil.dropCheapest();
                }
                log("Equip secondary");
                Inventory.interact(x -> KreeConsts.secondaryWeapons.contains(x.getId()));
            }

            if (Combat.getSpecialPercentage() >= 50) {
                Combat.toggleSpecialAttack(true);
            }
            Character tgt = Players.getLocal().getInteractingCharacter();
            if (magic != null) {
                if ((tgt == null || !tgt.equals(magic))) magic.interact("Attack");
                return 100;
            }

            if (range != null) {
                if ((tgt == null || !tgt.equals(range))) range.interact("Attack");
                return 100;
            }

            if (melee != null) {
                if ((tgt == null || !tgt.equals(melee))) melee.interact("Attack");
                return 100;
            }

            log("Nothing alive");
            toggle(false, Prayer.RIGOUR);
            toggle(false, Prayer.PROTECT_FROM_MISSILES);
            toggle(false, Prayer.PROTECT_FROM_MAGIC);
            toggle(false, Prayer.PROTECT_FROM_MELEE);
            return 100;
        }

        // ------- FIGHT -------
        toggle(true, appropriateProtectionPrayer());
        if (Client.getGameTick() - 3 > lastPlayerAttack) {
            log("Hit Kree");
            // You are off crossbow cooldown
            // equip odium ward & dcb/acb
            //t odo distance check for doing the switch > 1?
            NPC underKree = NPCs.closest(x -> x.getServerTile().distance(kree.getServerTile()) < 2 && !x.getName().equals(KREE));

            if (underKree != null && (Inventory.contains(ItemID.BLACK_CHINCHOMPA) || Equipment.contains(ItemID.BLACK_CHINCHOMPA))) {
                log("Chin switch");
                Equipment.equip(EquipmentSlot.WEAPON, ItemID.BLACK_CHINCHOMPA);
            } else {
                if (Inventory.contains(ItemID.ODIUM_WARD) || Inventory.contains(x -> KreeConsts.primaryWeapons.contains(x.getId()))) {
                    log("Equip primary");
                    Equipment.equip(EquipmentSlot.WEAPON, x -> KreeConsts.primaryWeapons.contains(x.getId()));
                    Equipment.equip(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD);
                    return 100;
                }
            }

            if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 8) {
                log("toggle run");
                Walking.toggleRun();
            }

            // toggle rigour
            toggle(true, getBestRangePray());
            toggle(true, Prayer.PROTECT_FROM_MISSILES);

            if (!kree.equals(Players.getLocal().getInteractingCharacter())) {
                log("Atk kree/under");
                if (underKree == null || (!Inventory.contains(ItemID.BLACK_CHINCHOMPA) && !Equipment.contains(ItemID.BLACK_CHINCHOMPA))) {
                    kree.interact("Attack");
                } else {
                    log("Underkree dist" + underKree.distance(kree));
                    log("UnderKree true dist " + underKree.getTrueTile().distance(kree.getTrueTile()));
                    log("Server dist " + underKree.getServerTile().distance(kree.getServerTile()));

                    underKree.interact("Attack");
                }
            }
        } else {
            // put on dinhs
            if (Inventory.contains(ItemID.DINHS_BULWARK)) {
                log("Equip dinhs");
                Equipment.equip(EquipmentSlot.WEAPON, ItemID.DINHS_BULWARK);
            }

            toggle(false, getBestRangePray());

            // walk under kree, normal tile is fine she doesn't move quickly
            Tile kreeTile = kree.getTile();
            if (kreeTile.distance() >= 2) {
                if (!Players.getLocal().getTile().equals(kreeTile) && (Walking.getDestination() == null || !kreeTile.equals(Walking.getDestination()))) {
                    Walking.walkExact(kreeTile);
                    return 50;
                }
            } else {
                log("Prolly safe");
            }
        }
        return 50;
    }

    /**
     * we want to flick minions, but only when we are under kree'arra as kree hits much harder so we will camp range when she can atk
     *
     * @return the appropriate protection prayer
     */
    private Prayer appropriateProtectionPrayer() {
        NPC kree = NPCs.closest(KREE);
        if (kree == null) {
            int fiveTick = Client.getGameTick() % 5;
            if (rangeGuardTiming == fiveTick) return Prayer.PROTECT_FROM_MISSILES;
            if (magicGuardTiming == fiveTick) return Prayer.PROTECT_FROM_MAGIC;
            if (meleeGuardTiming == fiveTick) return Prayer.PROTECT_FROM_MELEE;
        }

        return Prayer.PROTECT_FROM_MISSILES;
    }


    private void exitToGE() {
        GroundItem expensiveLoot = GroundItems.closest(x -> x.getItem().getLivePrice() > 50_000);
        if (expensiveLoot != null) {
            log("We need to leave but theres something expensive im gonna try and get " + expensiveLoot);
            expensiveLoot.interact("Take");
            return;
        }

        log("Out of resource leaving kree");
        Walking.walk(BankLocation.GRAND_EXCHANGE);
    }

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        String npcName = npc.getName();
        if (npcName.equals(MELEE_GUARD) && animation == 6957) {
            log("Melee guard attacked");
            meleeGuardTiming = Client.getGameTick() % 5;
            return;
        }

        if (npcName.equals(MAGIC_GUARD) && animation == 6955) {
            log("Magic guard attacked");
            magicGuardTiming = Client.getGameTick() % 5;
            return;
        }

        if (npcName.equals(RANGED_GUARD) && animation == 6956) {
            log("Range guard attacked");
            rangeGuardTiming = Client.getGameTick() % 5;
            return;
        }
    }

    @Override
    public void onTargeted(Projectile projectile, Tile tile) {
        if (projectile == null) return;
        int id = projectile.getId();
        if (id == 1199 || id == 1200) {
            kreeTiming = Client.getGameTick() % 3;
        }
    }

    @Override
    public void onPlayerAnimation(Player player, int animation, int animationDelay) {
        if (!player.equals(Players.getLocal())) return;

        if (animation == PLAYER_ATK_ANI || animation == 426 || animation == PLAYER_CHIN_ANI)
            lastPlayerAttack = Client.getGameTick();
    }

    private void toggle(boolean active, Prayer targetPrayer) {
        if (!Menu.isMenuManipulationActive()) {
            slowLog("Enable menu manip for a better experience");
            Prayers.toggle(active, targetPrayer);
            return;
        }
        if (Prayers.isActive(targetPrayer) == active) return;

        WidgetChild wc = targetPrayer.getWidgetChild();
        if (wc == null) {
            Tabs.open(Tab.PRAYER);
            wc = targetPrayer.getWidgetChild();
        }

        if (wc == null) return;
        wc.interact();
    }

    public static Prayer getBestRangePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 74 && PlayerSettings.getBitValue(RIGOUR_UNLOCKED) == 1) return Prayer.RIGOUR;
        return Prayer.EAGLE_EYE;
    }

    @Override
    public void onNpcDespawn(NPC npc) {
        if (npc == null || npc.getName() == null) return;
        String name = npc.getName();
        if (name.equals(MAGIC_GUARD)) {
            magicGuardTiming = -1;
            return;
        }
        if (name.equals(MELEE_GUARD)) {
            meleeGuardTiming = -1;
            return;
        }
        if (name.equals(RANGED_GUARD)) {
            rangeGuardTiming = -1;
            return;
        }
        if (name.equals(KREE)) {
            killCount++;
            kreeTiming = -1;
        }
    }

    List<Integer> ignoreList = Arrays.asList(
            ItemID.RUNE_DART, // you cant get these are drops but they're pretty insignificant
            ItemID.ODIUM_WARD,
            ItemID.DRAGON_CROSSBOW,
            ItemID.TOXIC_BLOWPIPE,
            ItemID.DINHS_BULWARK,
            ItemID.ARMADYL_CROSSBOW,
            ItemID.BOW_OF_FAERDHINEN,
            ItemID.DIAMOND_DRAGON_BOLTS_E
    );

    public void onInventoryItemAdded(Item item) {
        if (!KREE_BOSS_ROOM.contains(Players.getLocal())) return;
        if (ignoreList.contains(item.getId())) return;
        // todo acb ignore but only when you have 2
        kreearraGP += (item.getLivePrice() + 1) * item.getAmount();
    }
}
