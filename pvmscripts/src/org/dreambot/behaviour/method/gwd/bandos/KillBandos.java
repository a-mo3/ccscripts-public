package org.dreambot.behaviour.method.gwd.bandos;

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
import org.dreambot.scriptdata.BandosSettings;
import org.dreambot.scripts.BandosScript;
import org.dreambot.settings.timing.ReactionGenerator;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.dreambot.behaviour.method.spindel.range.RangeAttackSpindel.RIGOUR_UNLOCKED;

public class KillBandos extends Fractal implements AnimationListener, SpawnListener, ItemContainerListener {
    Tile startTile = new Tile(2873, 5352, 2);
    // starts at the tile
    Tile t1 = new Tile(2864, 5351, 2); // always shoot
    Tile t2 = new Tile(2864, 5361, 2); // skip first rotation
    Tile t3 = new Tile(2864, 5369, 2);
    Tile t4 = new Tile(2876, 5366, 2);
    Tile t5 = new Tile(2876, 5358, 2);
    Tile t6 = new Tile(2876, 5351, 2);

    Map<Tile, Tile> tileMap = new HashMap<>();

    final BandosSettings settings;

    public KillBandos(Supplier<Boolean> acceptCondition, BandosSettings settings) {
        super(acceptCondition);
        tileMap.put(t1, t2);
        tileMap.put(t2, t3);
        tileMap.put(t3, t4);
        tileMap.put(t4, t5);
        tileMap.put(t5, t6);
        tileMap.put(t6, t1);
        this.settings = settings;

        setSimpleName("Kill Bandos");
        Client.getInstance().addEventListener(this);
        if (BandosScript.shouldUseBonesToPeaches) {
            food = Arrays.asList(
                    ItemID.PEACH,
                    ItemID.MONKFISH,
                    ItemID.SHARK,
                    ItemID.CHILLI_POTATO,
                    ItemID.SUMMER_PIE,
//                    ItemID.BIG_BONES,
//                    ItemID.BONES,
                    ItemID.MANTA_RAY
            );
        }

        this.paintArraySupplier = () -> {
            NPC bandos = NPCs.closest(BANDOS);

            return new String[]{
                    "Range Cycle " + rangeGuardTiming,
                    "Magic Cycle " + magicGuardTiming,
                    "Melee Cycle " + meleeGuardTiming,
                    "Bandos Dist " + (bandos == null ? 0 : bandos.distance()),
                    targetTile == null ? " - " : targetTile.toString(),
                    "Target " + Players.getLocal().getInteractingCharacter(),
                    "First enter " + firstEnter,
                    "First lap  " + firstLap,
                    "Play atk " + lastPlayerAttack + " " + (Client.getGameTick() - lastPlayerAttack),
                    "Consume " + consumeDelay.elapsed(),
                    "Bandos spawn " + bandosRespawn.elapsed() / 1000,
                    "Bandos Atk Skip " + bandosAttack + " " + (bandos == null ? " " : bandos.getAnimation()),
                    ""
            };
        };
    }

    Timer consumeDelay = new Timer(1000);

    List<Integer> food = Arrays.asList(
            ItemID.PEACH,
            ItemID.MONKFISH,
            ItemID.SHARK,
            ItemID.CHILLI_POTATO,
            ItemID.SUMMER_PIE,
            ItemID.MANTA_RAY
    );

    public static final Area BANDOS_ROOM = new Area(2863, 5373, 2878, 5350, 2);
    final Tile OUTSIDE_ROOM_TILE = new Tile(2862, 5354, 2);

    public static final String BANDOS = "General Graardor";

    // todo reset these
    Tile targetTile = null;
    boolean firstLap = true;
    // firstEnter is for the first time you enter bandos room, he needs to be baited onto a tile to start the pattern
    boolean firstEnter = false;

    // range does more damage
    final String RANGE_MINION_NAME = "Sergeant Grimspike";
    final String MAGIC_MINION_NAME = "Sergeant Steelwill";
    final String MELEE_MINION_NAME = "Sergeant Strongstack";

    final int RANGE_ATTACK_ANIMATION = 7073;
    final int MAGIC_ATTACK_ANIMATION = 7071;
    final int MELEE_ATTACK_ANIMATION = 6154;

    int meleeGuardTiming = -1;
    int rangeGuardTiming = -1;
    int magicGuardTiming = -1;

    int lastPlayerAttack = -1;

    // he takes 90 seconds to respawn so at 85 seconds we should get on the square
    Timer bandosRespawn = new Timer(85 * 1000);

    final List<Integer> playerAtkAnimations = Arrays.asList(
            7552, // cross bow
            426 // crystal bow
    );

    final List<Integer> bandosAttackAnimations = Arrays.asList(
            7021, // range
            7018 // melee
    );

    final List<Integer> alchables = Arrays.asList(
            ItemID.RUNE_LONGSWORD,
            ItemID.RUNE_2H_SWORD,
            ItemID.RUNE_PLATEBODY,
            ItemID.RUNE_SWORD,
            ItemID.RUNE_PICKAXE
    );

    // when bandos gets close enough to attack flip this to skip next skippable tile
    boolean bandosAttack = false;
    Tile bandosAttackTile = null;

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (npc == null) return;
        String name = npc.getName();
        if (name == null) return;
        if (BANDOS.equals(name) && bandosAttackAnimations.contains(animation)) {
            bandosAttack = true;
            return;
        }


        if (RANGE_MINION_NAME.equals(name) && animation == RANGE_ATTACK_ANIMATION) {
            rangeGuardTiming = Client.getGameTick() % 5;
            return;
        }

        if (MAGIC_MINION_NAME.equals(name) && animation == MAGIC_ATTACK_ANIMATION) {
            magicGuardTiming = Client.getGameTick() % 5;
            return;
        }

        if (MELEE_MINION_NAME.equals(name) && animation == MELEE_ATTACK_ANIMATION) {
            meleeGuardTiming = Client.getGameTick() % 5;
            return;
        }
    }

    private Prayer getAppropriatePrayer() {
        NPC bandos = NPCs.closest(BANDOS);
        if (bandos != null && firstLap && t1.equals(Players.getLocal().getTile())) {
            log("Bandos close");
            return Prayer.PROTECT_FROM_MELEE;
        }
        int cycle = Client.getGameTick() % 5;
        NPC range = NPCs.closest(RANGE_MINION_NAME);
        NPC mage = NPCs.closest(MAGIC_MINION_NAME);
        if (range != null && (cycle == rangeGuardTiming || rangeGuardTiming < 0)) return Prayer.PROTECT_FROM_MISSILES;
        if (mage != null && cycle == magicGuardTiming) return Prayer.PROTECT_FROM_MAGIC;
        NPC melee = NPCs.closest(MELEE_MINION_NAME);
        if (melee != null && melee.distance() < 2 && cycle == meleeGuardTiming) return Prayer.PROTECT_FROM_MELEE;
        return settings.sweatPrayer ? null : Prayer.PROTECT_FROM_MISSILES;
    }

    @Override
    public int onLoop() {
        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 5) {
            log("Enable run");
            Walking.toggleRun();
        }

        if (!BANDOS_ROOM.contains(Players.getLocal())) {
            if (Inventory.contains(x -> BandosConsts.primaryWeapons.contains(x.getId()))) {
                log("Equip primary");
                Equipment.equip(EquipmentSlot.WEAPON, x -> BandosConsts.primaryWeapons.contains(x.getId()));
                return ReactionGenerator.getNormal();
            }

            if (Inventory.contains(ItemID.ODIUM_WARD)) {
                Inventory.interact(ItemID.ODIUM_WARD);
            }

            if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
                log("Need to change to range rapid current: " + Combat.getCombatStyle());
                Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
                return ReactionGenerator.getNormal();
            }

            // maybe equip crossbow before the attack logic
            if (!OUTSIDE_ROOM_TILE.equals(Players.getLocal().getTile())) {
                if (Walking.shouldWalk()) Walking.walkExact(OUTSIDE_ROOM_TILE);
                return ReactionGenerator.getNormal();
            }

            if (Combat.getHealthPercent() < 100) {
                log("Potting to full");
                // trusting i have these, if we dont we have bigger problems then the NPE
                ItemVariants.SARADOMIN_BREW.getItem().interact("Drink");
                return ReactionGenerator.getNormal();
            }

            boolean someoneElse = Players.all().stream().filter(x -> !Players.getLocal().equals(x)).anyMatch(BANDOS_ROOM::contains);
            if (someoneElse || Worlds.getCurrent().getPing() > settings.maxWorldPing) {
                log("Someone else is fighting Bandos in this world or this world has too high ping, hopping. Max ping " + Worlds.getCurrent().getPing() + " / " + settings.maxWorldPing);
                // hop
                World hopTuah = Worlds.getRandomWorld(x -> x.getPing() < settings.maxWorldPing
                        && x.isNormal() && x.getWorld() != 401 && x.isMembers()
                        && x.getMinimumLevel() < Skills.getTotalLevel());
                if (hopTuah == null) {
                    log("Could not find a world with a low enough ping, try increasing maxWorldPing setting");
                    Alerts.addAlert(6_000, Color.YELLOW, "Could not find a world with a low enough ping, try increasing maxWorldPing setting");
                    return 1;
                }
                // todo make sure the reconnect doesnt invoke any inv loadouts
                log("Hopping to world " + hopTuah.getWorld() + " Ping " + hopTuah.getPing());
                WorldHopper.hopWorld(hopTuah);
                return 1;
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

            if (Walking.getRunEnergy() < 30) {
                log("Stamina up");
                Item stamina = ItemVariants.STAMINA_POTION.getItem();
                if (stamina != null && stamina.interact()) {
                    log("Sip.");
                    return ReactionGenerator.getNormal();
                }
            }

            GameObject door = GameObjects.closest("Big door");
            if (door != null) {
                toggle(true, Prayer.PROTECT_FROM_MISSILES);
                log("Enter Bandos Room");
                // reset timer because they wont be synced on entrance
                door.interact("Open");
                firstEnter = true;
                Sleep.sleepUntil(() -> BANDOS_ROOM.contains(Players.getLocal()), 1600);
            }
            return ReactionGenerator.getNormal();

        }


        if (settings.leaveBossIfCrashed && Players.closest(x -> !x.equals(Players.getLocal()) && BANDOS_ROOM.contains(x)) != null) {
            log("We've been crashed, leave");
            exitToGE();
            return ReactionGenerator.getQuick();
        }

        if (Inventory.isFull() && Inventory.contains(ItemID.VIAL, ItemID.HAMMER)) {
            Inventory.dropAll(ItemID.VIAL, ItemID.HAMMER);
        }

        NPC bandos = NPCs.closest(BANDOS);
        // food & pots
        // check target tile distance here because we dont want to miss an attack
        // check for null tile so we dont try to eat while standing on tile
        if (consumeDelay.finished() && (bandos == null || Walking.getDestination() != null)) {
            if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
                log("Need to change to range rapid current: " + Combat.getCombatStyle());
                Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
                return ReactionGenerator.getNormal();
            }
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
                return 1;
            }

            // eat
            // todo handle using brews
            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 50) {
                if (Inventory.contains(x -> food.contains(x.getId())) && bandos == null) {
                    log("Eating");
                    Inventory.interact(x -> food.contains(x.getId()), "Eat");
                    consumeDelay.reset();
                    lastPlayerAttack += 3;
                } else {
                    Item brew = ItemVariants.SARADOMIN_BREW.getItem();
                    if (brew != null) {
                        log("Drinking brew");
                        consumeDelay.reset();
                        brew.interact("Drink");
                        return 1;
                    }

                    log("Out of food leaving");
                    exitToGE();
                }
                return 1;
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
                    return 1;
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
                    return 1;
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

        if (bandos == null) {
            slowLog("Bandos dead reset state");
            firstLap = true;
            targetTile = null;

            NPC melee = NPCs.closest(MELEE_MINION_NAME);
            NPC magic = NPCs.closest(MAGIC_MINION_NAME);
            NPC range = NPCs.closest(RANGE_MINION_NAME);

            if (magic == null) magicGuardTiming = -1;
            if (range == null) rangeGuardTiming = -1;
            if (melee == null) meleeGuardTiming = -1;

            // todo add a bandos respawn timer here so if you cant kill all 3 you stand on start anyway
            boolean nothingAlive = (melee == null && magic == null && range == null);
            if (bandosRespawn.finished() || nothingAlive) {
                toggle(!nothingAlive, getAppropriatePrayer());
                toggle(!nothingAlive, getBestRangePray());
                // take loot (all unless the timer is close to a respawn)
                GroundItem loot = GroundItems.all(BANDOS_ROOM::contains)
                        .stream()
                        .filter(x -> (1 + x.getItem().getLivePrice()) * x.getAmount() > 5_000 || x.getId() == ItemID.COINS_995 || (food.contains(x.getId())))
                        .findFirst().orElse(null);
                if (!bandosRespawn.finished()
                        && loot != null
                        && (!Inventory.isFull() || loot.getItem().isStackable() && Inventory.contains(loot.getId()))
                ) {
                    log(String.format("Taking item %s value %d", loot.getName(), loot.getAmount() * loot.getItem().getLivePrice()));
                    loot.interact("Take");
                    return 150;
                }

                if (Magic.canCast(Normal.BONES_TO_PEACHES) && Inventory.contains(ItemID.BONES, ItemID.BIG_BONES)) {
                    log("Bones to peach");
                    Magic.castSpell(Normal.BONES_TO_PEACHES);
                    return ReactionGenerator.getQuick();
                }

                if (!startTile.equals(Players.getLocal().getTile())) {
                    Tile dest = Walking.getDestination();
                    log("Get onto start tile");
                    if (!startTile.equals(dest)) Walking.walkExact(startTile);
                    return ReactionGenerator.getNormal();
                }

                if (Inventory.contains(x -> BandosConsts.primaryWeapons.contains(x.getId()))) {
                    if (Inventory.isFull() && Inventory.contains(ItemID.TOXIC_BLOWPIPE)) {
                        log("Drop cheapest for BP equip");
                        PVMUtil.dropCheapest();
                    }
                    Inventory.interact(x -> BandosConsts.primaryWeapons.contains(x.getId()));
                }

                if (Inventory.contains(ItemID.ODIUM_WARD)) {
                    Inventory.interact(ItemID.ODIUM_WARD);
                }

                Item foodItem = Inventory.get(x -> food.contains(x.getId()));
                if (!bandosRespawn.finished() && Combat.getHealthPercent() < 100 && foodItem != null) {
                    log("Eat piece of food");
                    foodItem.interact();
                }

                Item alchable = Inventory.get(x -> alchables.contains(x.getId()));
                if (!bandosRespawn.finished() && Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY) && alchable != null) {
                    log("Gotta alch sumn " + alchable);
                    Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, alchable);
                    Sleep.sleep(600);
                }

                return 150;
            }

            toggle(true, getAppropriatePrayer());

            // deal with minions
            if (equipGuthans()) return 100;

            Item foodItem = Inventory.get(x -> food.contains(x.getId()));
            if (!bandosRespawn.finished() && Combat.getHealthPercent() < 100 && foodItem != null) {
                log("Eat piece of food");
                foodItem.interact();
            }

            if (!Inventory.contains(x -> x.getName().contains("Guthan")) && !Equipment.contains(x -> x.getName().contains("Guthan"))) {
                if (Inventory.contains(x -> BandosConsts.secondaryWeapons.contains(x.getId()))) {
                    log("Equip secondary");
                    Equipment.equip(EquipmentSlot.WEAPON, x -> BandosConsts.secondaryWeapons.contains(x.getId()));
                    return 150;
                }
            }

            // take loot (expensive and food)
            GroundItem loot = GroundItems.all(BANDOS_ROOM::contains)
                    .stream()
                    .filter(x -> (1 + x.getItem().getLivePrice()) * x.getAmount() > Math.max(12_000, settings.expensiveLootThreshold))
                    .findFirst().orElse(null);
            if (loot != null) {
                if (Inventory.isFull()) {
                    log("Drop cheapest item");
                    PVMUtil.dropCheapest();
                }

                log(String.format("Taking item %s value %d", loot.getName(), loot.getAmount() * loot.getItem().getLivePrice()));
                loot.interact("Take");
                return 150;
            }
            loot = GroundItems.closest(x -> food.contains(x.getId()));
            if (!Inventory.isFull() && loot != null) {
                log(String.format("Taking food item %s value %d", loot.getName(), loot.getAmount() * loot.getItem().getLivePrice()));
                loot.interact("Take");
                return 150;
            }


            if (Equipment.contains(ItemID.TOXIC_BLOWPIPE) && Combat.getHealthPercent() < 100 && Combat.getSpecialPercentage() >= 50) {
                log("BP spec");
                Combat.toggleSpecialAttack(true);
            }
            Character tgt = Players.getLocal().getInteractingCharacter();
            if (melee != null) {
                if ((tgt == null || !tgt.equals(melee))) melee.interact("Attack");
                return 100;
            }

            if (magic != null) {
                if ((tgt == null || !tgt.equals(magic))) magic.interact("Attack");
                return 100;
            }

            if (range != null) {
                if ((tgt == null || !tgt.equals(range))) range.interact("Attack");
                return 100;
            }
            return 100;
        }

        if (Skill.PRAYER.getBoostedLevel() > 1) toggle(true, getAppropriatePrayer());

        if (Equipment.contains(x -> x.getName().contains("Guthan") || !Equipment.contains(ItemID.DRAGON_CROSSBOW)))
            equipNormalGear();

        if (firstEnter) {
            if (!t1.equals(Players.getLocal().getTile())) {
                log("Getting onto first tile to configure fight");
                Tile dest = Walking.getDestination();
                if (!t1.equals(dest)) Walking.walkExact(t1);
                return 150;
            }

            if (bandos != null && bandos.distance() < 4) {
                firstEnter = false;
            }
            return 100;
        }

        // for 6 - 0 bandos you need to
        if (targetTile == null) {
            log("t1 start");
            targetTile = t1;
        }


        // walk to the next tile, when your server tile hit the target tile, attack bandos then set the next target
        Tile lpServerTile = Players.getLocal().getServerTile();
        if (firstLap && (t3.equals(lpServerTile) || t4.equals(lpServerTile) || t5.equals(lpServerTile))) {
            log("Hit the first lap");
            firstLap = false;
        }

        if (bandosAttack && bandosAttackTile != null && lpServerTile != bandosAttackTile) {
            log("Bandos atk skip reset");
            bandosAttackTile = null;
            bandosAttack = false;
        }

        toggle(Client.getGameTick() - lastPlayerAttack >= 4, getBestRangePray());
        if (tileMap.containsKey(lpServerTile)) {
            int ticksSinceLast = Client.getGameTick() - lastPlayerAttack;
            targetTile = tileMap.get(lpServerTile);
            if (ticksSinceLast >= 4) {
                if (t2.equals(lpServerTile) && firstLap) {
                    log("Skip");
                } else if (settings.tileSkipping && !t1.equals(lpServerTile) && bandosAttack) {
                    log("Skip bandos attack");
                    bandosAttackTile = lpServerTile;
                } else {
                    if (bandos.distance() < 5) {
                        log("Bandos too close " + bandos.distance());
                        log("or too soon a consume, " + consumeDelay.elapsed());
                    } else {
                        log("Attack bandos " + Arrays.toString(bandos.getActions()));
                        log(String.valueOf(bandos.interact("Attack")));
                        return 1;
                    }
                }
            }
        }

        if (Walking.getDestination() == null || !tileMap.containsKey(Walking.getDestination())) {
            log("Walk to next 6-0 tile " + targetTile);
            log("on tgt check " + targetTile.equals(Players.getLocal().getTile()));
            Walking.clickTileOnMinimap(targetTile);
            return 100;
        }
        return 1;
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

    private void toggle(boolean active, Prayer targetPrayer) {
        if (!Menu.isMenuManipulationActive()) {
            slowLog("Enable menu manipulation for a better experience");
            Prayers.toggle(active, targetPrayer);
            return;
        }

        if (targetPrayer == null) {
            toggle(false, Prayer.PROTECT_FROM_MISSILES);
            toggle(false, Prayer.PROTECT_FROM_MELEE);
            toggle(false, Prayer.PROTECT_FROM_MAGIC);
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

    @Override
    public void onPlayerAnimation(Player player, int animation, int animationDelay) {
        if (!player.equals(Players.getLocal())) return;

        if (playerAtkAnimations.contains(animation))
            lastPlayerAttack = Client.getGameTick();
    }

    boolean equipGuthans() {
        if (Inventory.contains(x -> x.getName().contains("Guthan"))) {
            // check theres sapce shield & weapon swap 1 handed to 2 handed
            log("Equip all guthans");
            Inventory.all(x -> x.getName().contains("Guthan")).forEach(Item::interact);
            return true;
        }
        return false;
    }

    List<Integer> normalGear = Arrays.asList(
            ItemID.GUTHIX_DHIDE_BODY,
            ItemID.ARMADYL_COIF,
            ItemID.BANDOS_BRACERS,
            ItemID.SARADOMIN_DHIDE_BOOTS,
            ItemID.ZAMORAK_CHAPS,
            ItemID.DRAGON_CROSSBOW,
            ItemID.ODIUM_WARD,
            ItemID.KARILS_LEATHERSKIRT,
            ItemID.KARILS_LEATHERSKIRT_100,
            ItemID.KARILS_LEATHERSKIRT_75,
            ItemID.KARILS_LEATHERSKIRT_50,
            ItemID.KARILS_LEATHERSKIRT_25,
            ItemID.KARILS_LEATHERSKIRT_0,
            ItemID.KARILS_LEATHERTOP,
            ItemID.KARILS_LEATHERTOP_100,
            ItemID.KARILS_LEATHERTOP_75,
            ItemID.KARILS_LEATHERTOP_50,
            ItemID.KARILS_LEATHERTOP_25,
            ItemID.KARILS_LEATHERTOP_0
    );

    // switches out of guthans to the normal kits
    void equipNormalGear() {
        Inventory.all(x -> normalGear.contains(x.getId()))
                .forEach(Item::interact);
    }

    @Override
    public void onNpcDespawn(NPC npc) {
        if (npc == null) return;
        if (BANDOS.equals(npc.getName())) bandosRespawn.reset();
    }

    public static Prayer getBestRangePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 74 && PlayerSettings.getBitValue(RIGOUR_UNLOCKED) == 1) return Prayer.RIGOUR;
        return Prayer.EAGLE_EYE;
    }


    List<Integer> ignoreList = Arrays.asList(
            ItemID.RUNE_DART, // you cant get these are drops but they're pretty insignificant
            ItemID.ODIUM_WARD,
            ItemID.DRAGON_CROSSBOW,
            ItemID.TOXIC_BLOWPIPE,
            ItemID.BOW_OF_FAERDHINEN
    );
    public static int bandosGP;

    @Override
    public void onInventoryItemAdded(Item item) {
        if (!BANDOS_ROOM.contains(Players.getLocal())) return;
        if (ignoreList.contains(item.getId())) return;
        // todo acb ignore but only when you have 2
        bandosGP += (item.getLivePrice() + 1) * item.getAmount();
    }
}
