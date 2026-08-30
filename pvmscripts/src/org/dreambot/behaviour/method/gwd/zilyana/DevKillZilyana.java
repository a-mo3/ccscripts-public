package org.dreambot.behaviour.method.gwd.zilyana;

import org.dreambot.alerts.Alerts;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
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
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
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
import org.dreambot.scriptdata.ZilyanaSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class DevKillZilyana extends Fractal implements AnimationListener, ItemContainerListener, SpawnListener {
    HashMap<Tile, Tile> fightTiles = new HashMap<>();
    public static int zilyanaGP = 0;

    final ZilyanaSettings settings;
    private final String BREE = "Bree";
    private final String GROWLER = "Growler";
    private final String STARLIGHT = "Starlight";

    public DevKillZilyana(Supplier<Boolean> acceptCondition, ZilyanaSettings settings) {
        super(acceptCondition);
        this.settings = settings;

        paintArraySupplier = () -> {
            NPC zil = NPCs.closest("Commander Zilyana");
            NPC starlight = NPCs.closest("Starlight");
            return new String[]{
                    String.format("B: %d R: %d P: %d S: %d", ItemVariants.SARADOMIN_BREW.getInventoryCount(),
                            ItemVariants.SUPER_RESTORE.getInventoryCount(),
                            ItemVariants.PRAYER_POTION.getInventoryCount(),
                            ItemVariants.STAMINA_POTION.getInventoryCount()
                    ),
                    "Bree cycle: " + breeTickTiming,
                    "Growler Cycle: " + growlerTickTiming,
                    "Startlight cycle " + starlightTickTiming,
                    "Current " + Client.getGameTick() % 5,
                    "Ping " + Worlds.getCurrent().getPing(),
                    String.format("Zil dist %.2f Ser: %.2f t %d",
                            zil == null ? 0 : zil.distance(),
                            zil == null ? 0 : zil.getServerTile().distance(),
                            zil == null ? 0 : zil.getServerTile().tileDistance(Players.getLocal().getServerTile())
                    ),
                    String.format("star dist %.2f Ser: %.2f", starlight == null ? 0 : starlight.distance(), starlight == null ? 0 : starlight.getServerTile().distance()),
                    "Since lst atk " + (Client.getGameTick() - lastPlayerAttackTick),
                    "Curr Tile " + currentTileTarget,
            };
        };


        fightTiles.put(t1, t2);
        fightTiles.put(t2, t3);
        fightTiles.put(t3, t4);
        fightTiles.put(t4, t1);

        Client.getInstance().addEventListener(this);
    }

    List<Integer> food = Arrays.asList(
            ItemID.MONKFISH,
            ItemID.SUMMER_PIE,
            ItemID.MANTA_RAY
    );

    Timer desyncAttemptTimer = new Timer(20_000);

    Tile t1 = new Tile(2907, 5258);
    Tile t2 = new Tile(2889, 5258);
    Tile t3 = new Tile(2889, 5275);
    Tile t4 = new Tile(2907, 5275);
    // it seems that damage is rolled on the animation not projectile hitting
    // animation when bree fires bow
    final int BREE_ANI_ID = 7026;
    // growlers mage atk
    final int GROWLER_ANI_ID = 7037;
    final int STARLIGHT_ANI_ID = 6376;

    final int BREE_ARROW_PROJECTILE = 1190;
    final int GROWLER_MAGIC_PROJECTILE = 1183;

    // should start here when zil is spawning
    Tile startTile = ZilyanaTile.AWAIT_TILE.location;

    Timer consumeDelay = new Timer(1000);
    public static final Area ZILYANA_BOSS_ROOM = new Area(2883, 5276, 2908, 5257);
    int lastPlayerAttackTick = -1;


    Tile currentTileTarget = t1;
    Tile outsideDoor = new Tile(2909, 5265, 0);

    Timer takeTimer = new Timer(300);
    Timer equipTimer = new Timer(600);

    // what tile we should be on next
    ZilyanaTile targetZilTile = ZilyanaTile.tileGraph.get(0);

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.VIAL, ItemID.PIE_DISH)) {
            log("drop vials n waste");
            Inventory.dropAll(ItemID.PIE_DISH, ItemID.VIAL);
        }

        ZilyanaSettings zs = SettingsRepository.findInstanceOf(new ZilyanaSettings());
        Player lp = Players.getLocal();
        // you can be walking to room
        if (GetZilyanaKC.ROCK_THROW_AREA.contains(lp)) {
            log("Prot missle for troll rocks");
            toggle(true, Prayer.PROTECT_FROM_MISSILES);
        } else if (lp.getCharactersInteractingWithMe().stream().anyMatch(x -> x.distance() < 3 && x.getName().toLowerCase().contains("wolf"))) {
            log("Pray against wolf");
            toggle(true, Prayer.PROTECT_FROM_MELEE);
        } else {
            // todo consider how this will effect flicking, should be fine if are only killing priests
            toggle(false, Prayer.PROTECT_FROM_MELEE);
        }

        if (Combat.getCombatStyle() != CombatStyle.RANGED_DEFENCE) {
            log("Switch to ranged rapid current style: " + Combat.getCombatStyle());
            Combat.setCombatStyle(CombatStyle.RANGED_DEFENCE);
        }

        if (!ZILYANA_BOSS_ROOM.contains(Players.getLocal())) {
            // ensure crossbow equipped not darts/blowpipe
            Item primary = Inventory.get(x -> ZilyanaConsts.primaryWeapons.contains(x.getId()));
            if (primary != null && equipTimer.finished()) {
                log("Equip primary weapon " + primary);
                primary.interact();
                equipTimer.reset();
                return 50;
            }
            // todo ensure we are fully potted and boosted
            // todo pray magic if we are still being attacked by a priest
            // go outside of door
            if (!outsideDoor.equals(Players.getLocal().getTile())) {
                slowLog("Walking to zil door");
                toggle(false, Prayer.PROTECT_FROM_MELEE);
                if (Walking.shouldWalk()) Walking.walkExact(outsideDoor);
                return 50;
            }

            // if someone is in the arena, hop worlds
            boolean someoneElse = Players.all().stream().anyMatch(ZILYANA_BOSS_ROOM::contains);
            if (someoneElse) {
                log("Someone else is fighting Zilyana in this world, hopping.");
                // hop
                World hopTuah = Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getPing() < zs.maxWorldPing && x.getMinimumLevel() < Skills.getTotalLevel());
                if (hopTuah == null) {
                    log("Could not find a world with a low enough ping, try increasing maxWorldPing setting");
                    Alerts.addAlert(6_000, Color.YELLOW, "Could not find a world with a low enough ping, try increasing maxWorldPing setting");
                    return 50;
                }
                // todo make sure the reconnect doesnt invoke any inv loadouts
                WorldHopper.hopWorld(hopTuah);
                return 12_500;
            }
            // hop to a world with acceptable ping
            if (Worlds.getCurrent().getPing() > zs.maxWorldPing) {
                log("This worlds ping is too high, swapping to a world with better ping");
                World hopTuah = Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getPing() < zs.maxWorldPing);
                if (hopTuah == null) {
                    log("Could not find a world with a low enough ping, try increasing maxWorldPing setting");
                    Alerts.addAlert(6_000, Color.YELLOW, "Could not find a world with a low enough ping, try increasing maxWorldPing setting");
                    return 50;
                }
                // todo make sure the reconnect doesnt invoke any inv loadouts
                WorldHopper.hopWorld(hopTuah);
                return 12_500;
            }


            // if no one is in the arena, wait until zil is north of door then enter
            NPC zilyana = NPCs.closest("Commander Zilyana");
            if (zilyana != null) {
                log("Spotted zilyana");
                if (zilyana.getY() < Players.getLocal().getY()) {
                    log("Zilyana is in the wrong spot, waiting for her to wander somewhere better");
                    return 50;
                }

                if (Combat.isAutoRetaliateOn()) {
                    log("Turn off auto retaliate");
                    Combat.toggleAutoRetaliate(false);
                    return ReactionGenerator.getNormal();
                }

                // enter arena
                GameObject door = GameObjects.closest("Big door");
                if (door != null) {
                    log("Enter zil");
                    // reset timer because they wont be synced on entrance
                    desyncAttemptTimer.reset();
                    door.interact("Open");
                    currentTileTarget = t1; // set to T1 here so we instantly run down
                    Sleep.sleepUntil(() -> ZILYANA_BOSS_ROOM.contains(Players.getLocal()), 2400);
                }
            }
            return 50;
        }

//        if (zs.leaveZilIfCrashed && Players.closest(x -> !x.equals(Players.getLocal()) && ZILYANA_BOSS_ROOM.contains(x)) != null) {
//            log("We've been crashed, leave");
//            exitToGE();
//            return ReactionGenerator.getQuick();
//        }

        if (!Walking.isRunEnabled()) {
            log("Toggle run");
            Walking.toggleRun();
        }

        // food & pots
        if ((Client.getGameTick() - lastPlayerAttackTick < 3) && consumeDelay.finished()) {
            // stamina
            Item staminaPot = ItemVariants.STAMINA_POTION.getItem();
            if (Walking.getRunEnergy() < 20) {
                if (staminaPot != null) {
                    log("Drinking stamina");
                    staminaPot.interact("Drink");
                    consumeDelay.reset();
                } else {
                    log("No more stamina, leaving");
                    exitToGE();
                }
                return 50;
            }

            // eat
            // todo handle using brews
            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 50) {
                if (Inventory.contains(x -> food.contains(x.getId()))) {
                    log("Eating");
                    Inventory.interact(x -> food.contains(x.getId()), "Eat");
                    lastPlayerAttackTick += 3;
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
        }

        NPC zil = NPCs.closest("Commander Zilyana");
        NPC starlight = NPCs.closest("Starlight");
        // looting and killing guards
        if (zil == null && starlight == null) {
            log("Deal with guards");
            Prayer appropriate = getPrayerFlick();

            NPC bree = NPCs.closest("Bree");
            NPC growler = NPCs.closest("Growler");


            if (growler == null && bree == null) {
                Item alchable = Inventory.get(x -> ZilyanaConsts.ALCH_ITEMS.contains(x.getId()));
                if (equipTimer.finished() && Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY) && alchable != null) {
                    log("Alch");
                    Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, alchable);
                    equipTimer.reset();
                }

                // todo recharge at prayer jaunt
                toggle(false, Prayer.PROTECT_FROM_MAGIC);
                toggle(false, Prayer.PROTECT_FROM_MISSILES);
                toggle(false, getBestRangePray());
                log("Get on start tile");
                if (!ZilyanaTile.AWAIT_TILE.location.equals(Players.getLocal().getTile()) && Walking.shouldWalk())
                    Walking.walkExact(ZilyanaTile.AWAIT_TILE.location);
                return 50;
            }

            if (bree == null) appropriate = Prayer.PROTECT_FROM_MAGIC; // bree is dead then only magic for growler
            if (growler == null) appropriate = Prayer.PROTECT_FROM_MISSILES; // bree is dead then only magic for growler
            toggle(true, appropriate);

            // loot
            GroundItem loot = GroundItems.all(ZILYANA_BOSS_ROOM::contains)
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

            if (equipTimer.finished() && Inventory.contains(x -> ZilyanaConsts.secondaryWeapons.contains(x.getId()))) {
                if (Inventory.isFull() && Inventory.contains(ItemID.TOXIC_BLOWPIPE)) {
                    log("Drop cheapest for BP equip");
                    PVMUtil.dropCheapest();
                }
                log("Equip secondary");
                Inventory.interact(x -> ZilyanaConsts.secondaryWeapons.contains(x.getId()));
                equipTimer.reset();
                return 50;
            }

            if (Combat.getSpecialPercentage() >= 50 && Equipment.contains(ItemID.TOXIC_BLOWPIPE))
                Combat.toggleSpecialAttack(true);

            // if the loots not worth that much & inv is full just leave it

//            if (breeTickTiming != growlerTickTiming && zs.dontKillOffsetGuards) {
//                log("Not killing just flicking");
//                toggle(false, getBestRangePray());
//                if (!startTile.equals(Players.getLocal().getTile()) && Walking.shouldWalk())
//                    Walking.walkExact(startTile);
//                currentTileTarget = t1;
//                return 50;
//            }
            Character tgt = Players.getLocal().getInteractingCharacter();
            // kill bree
            if (bree != null && (tgt == null || !tgt.equals(bree))) {
                bree.interact("Attack");
                // todo these sleeps prevent pray flicking
                Sleep.sleepUntil(() -> tgt != null && tgt.equals(bree), 1200);
                return 50;
            }

            // kill growler
            if (growler != null && (tgt == null || !tgt.equals(bree))) {
                growler.interact("Attack");
                Sleep.sleepUntil(() -> tgt != null && tgt.equals(bree), 1200);
                return 50;
            }
            return 50;
        }

        // pray
        // consider flicking for Growler (mage) & Bree (range)
        Prayer appropriate = getPrayerFlick();
        toggle(true, appropriate);

        if (Inventory.contains(x -> ZilyanaConsts.primaryWeapons.contains(x.getId()))) {
            log("Equpping primary");
            Equipment.equip(EquipmentSlot.WEAPON, x -> ZilyanaConsts.primaryWeapons.contains(x.getId()));
        }

        if (Inventory.contains(ItemID.ODIUM_WARD)) {
            log("Equip odium ward");
            Equipment.equip(EquipmentSlot.SHIELD, ItemID.ODIUM_WARD);
        }

        Tile lpServerTile = Players.getLocal().getServerTile();
        ZilyanaTile onPhaseTile = null;
        int phaseNumber = -1;

        // check if we are on a phase tile
        for (int i = 0; i < ZilyanaTile.tileGraph.size(); i++) {
            if (ZilyanaTile.tileGraph.get(i).location.equals(lpServerTile)) {
                onPhaseTile = ZilyanaTile.tileGraph.get(i);
                phaseNumber = i;
                break;
            }
        }

        if (onPhaseTile != null) {
            if (onPhaseTile.attackTile && Client.getGameTick() - lastPlayerAttackTick >= 4) {
                if (zil != null && zil.distance() < 9) {
                    log("Zil attack " + zil.getServerTile().distance());
                    zil.interact("Attack");
                    Sleep.sleep(100);
                }
            }
            targetZilTile = ZilyanaTile.tileGraph.get((phaseNumber + 1) % ZilyanaTile.tileGraph.size());
        }

        if (Walking.getDestination() == null || !targetZilTile.location.equals(Walking.getDestination())) {
            log("Walk to next tile " + targetZilTile.location);
            // is walkTile is true (should walk to it) and run is enabled, then true, must be toggled to walk
            boolean forceRun = !Walking.isRunEnabled() && ((zil != null && zil.getServerTile().distance() < 4));
            if (forceRun) {
                if (!Walking.isRunEnabled()) {
                    log("Force run");
                    Walking.toggleRun();
                    Sleep.sleep(50);
                }
            } else if (Walking.isRunEnabled() == targetZilTile.walkTile) {
                log("Walk toggle " + targetZilTile.location + " " + targetZilTile.walkTile + " " + Walking.isRunEnabled());
                Walking.toggleRun();
                Sleep.sleep(50);
            }
            log("Walk to " + targetZilTile.location);
            Walking.clickTileOnMinimap(targetZilTile.location);
            return 50;
        }

        return 50;
    }

    public static final int RIGOUR_UNLOCKED = 5451;

    public static Prayer getBestRangePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 74 && PlayerSettings.getBitValue(RIGOUR_UNLOCKED) == 1) return Prayer.RIGOUR;
        return Prayer.EAGLE_EYE;
    }

    int breeTickTiming = -1;
    // this is really just to see if bree and growler and synced, we will always pray mage so we dont get smacked due to lag
    int growlerTickTiming = -1;
    int starlightTickTiming = -1;

    long lagModeBreeTiming = -1;

    /**
     * @return the prayer you should be using based on the growler and bree tick timings, and depending on lag adjust setting
     */
    private Prayer getPrayerFlick() {
//        if (settings.sweatPrayer) {
//            NPC bree = NPCs.closest(BREE);
//            NPC growler = NPCs.closest(GROWLER);
//            NPC starlight = NPCs.closest(STARLIGHT);
//
//            int tick = Client.getGameTick();
//            if (bree != null && (tick - breeTickTiming) % 5 == 0) return Prayer.PROTECT_FROM_MISSILES;
//            if (growler != null && (tick - growlerTickTiming) % 5 == 0) return Prayer.PROTECT_FROM_MAGIC;
//            if (starlight != null && starlight.distance() < 3 && (tick - starlightTickTiming) % 5 == 0)
//                return Prayer.PROTECT_FROM_MELEE;
//            return null;
//        }
//
        return breeTickTiming != growlerTickTiming && (Client.getGameTick() - breeTickTiming) % 5 == 0 ?
                Prayer.PROTECT_FROM_MISSILES : Prayer.PROTECT_FROM_MAGIC;
    }

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        String name = npc.getName();
        if (name.equals("Bree")) {
            if (animation == BREE_ANI_ID) {
                int tick = Client.getGameTick() % 5;
                breeTickTiming = tick;
                lagModeBreeTiming = System.currentTimeMillis();
            }
            return;
        }

        if (name.equals("Growler")) {
            if (animation == GROWLER_ANI_ID) {
                int tick = Client.getGameTick() % 5;
                growlerTickTiming = tick;
            }
        }

        if (STARLIGHT.equals(name)) {
            log("Starlight animated " + animation);
            if (animation == STARLIGHT_ANI_ID) {
                int tick = Client.getGameTick() % 5;
                starlightTickTiming = tick;
            }
        }
    }

    private void exitToGE() {
        GroundItem expensiveLoot = GroundItems.closest(x -> x.getItem().getLivePrice() > 50_000);
        if (expensiveLoot != null) {
            log("We need to leave but theres something expensive im gonna try and get " + expensiveLoot);
            expensiveLoot.interact("Take");
            return;
        }

        log("Out of resource leaving zilyana");
        Walking.walk(BankLocation.GRAND_EXCHANGE);
    }

    List<Integer> ignoreList = Arrays.asList(
            ItemID.RUNE_DART, // you cant get these are drops but they're pretty insignificant
            ItemID.ODIUM_WARD,
            ItemID.DRAGON_CROSSBOW,
            ItemID.TOXIC_BLOWPIPE
    );

    public void onInventoryItemAdded(Item item) {
        if (!ZILYANA_BOSS_ROOM.contains(Players.getLocal())) return;
        if (ignoreList.contains(item.getId())) return;
        // todo acb ignore but only when you have 2
        zilyanaGP += (item.getLivePrice() + 1) * item.getAmount();
    }


    @Override
    public void onNpcSpawn(NPC npc) {
        if (npc == null || npc.getName() == null) return;
        if (npc.getName().equals("Commander Zilyana")) {
            log("");
            currentTileTarget = t1;
        }

        if (npc.getName().equals("Bree") || npc.getName().equals("Growler")) {
            desyncAttemptTimer.reset();
        }
    }


    private void toggle(boolean active, Prayer targetPrayer) {
        ZilyanaSettings zs = SettingsRepository.findInstanceOf(new ZilyanaSettings());
        if (targetPrayer == null) {
            log("null disable overheads");
            toggle(false, Prayer.PROTECT_FROM_MISSILES);
            toggle(false, Prayer.PROTECT_FROM_MELEE);
            toggle(false, Prayer.PROTECT_FROM_MAGIC);
            return;
        }

//        if (!Menu.isMenuManipulationActive() || zs.useDBPrayerToggle) {
//            slowLog("Enable menu manipulation for a better experience");
//            Prayers.toggle(active, targetPrayer);
//            return;
//        }

        if (Prayers.isActive(targetPrayer) == active) return;

        WidgetChild wc = targetPrayer.getWidgetChild();
        if (wc == null) {
            Tabs.open(Tab.PRAYER);
            wc = targetPrayer.getWidgetChild();
        }

        if (wc == null) return;
        wc.interact();
    }

    final List<Integer> playerAtkAnimations = Arrays.asList(
            7552, // cross bow
            426 // crystal bow
    );

    @Override
    public void onPlayerAnimation(Player player, int animation, int animationDelay) {
        if (!player.equals(Players.getLocal())) return;

        if (playerAtkAnimations.contains(animation))
            lastPlayerAttackTick = Client.getGameTick();
    }
}
