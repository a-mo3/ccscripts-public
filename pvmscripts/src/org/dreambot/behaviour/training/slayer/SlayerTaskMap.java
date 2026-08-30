package org.dreambot.behaviour.training.slayer;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebPathQuery;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebPathResponse;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class SlayerTaskMap {
    private static Map<Integer, Fractal> slayerTasks;
    public static int minLootValue = 1000;

    public static boolean execSlayerTask() {
        int taskState = SlayerBranch.getSlayerTaskKey(); // https://chisel.weirdgloop.org/structs/index.html?type=enums&id=693
        if (slayerTasks != null) {
            if (!slayerTasks.containsKey(taskState)) {
                Logger.warn("Unmapped slayer task " + SlayerBranch.getSlayerTaskKey());
                Logger.info("Cancel task " + new CancelTaskEvent().executed());
                return false;
            } else {
                slayerTasks.get(taskState).run();
                return true;
            }
        }

        slayerTasks = new HashMap<>();

        slayerTasks.put(76,
                new StandardCombat(new Area(1886, 5200, 1907, 5186), "Minotaur", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setAfterLoadouts(() -> enterSOSFirstFloor.get() || handleGateOfWarAnswers.get())
                        .setSimpleName("Minotaurs"));


        slayerTasks.put(62,
                new StandardCombat(new Area(3147, 9566, 3162, 9552), "Cave slime", ItemID.SHARK)
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                                .addItem(EquipmentSlot.HAT, ItemID.SPINY_HELMET)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 5)
                        )
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                                .addItem(ItemVariants.SUPER_ANTI_POISON, 1, 2)
                                .addItem(ItemID.TINDERBOX)
                                .addItem(ItemVariants.CANDLE_LANTERN)
                                .addItem(ItemID.ROPE)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.CANDLE, ItemID.LIT_CANDLE)) // cant have these they will explode gas
                        )
                        .setAfterLoadouts(() -> lightCandle.get() || handlePoison.get() || enterLumbridgeSwampDungeon.get())
                        .setSimpleName("Cave slime"));


        slayerTasks.put(37,
                new StandardCombat(new Area(3181, 9593, 3196, 9561), "Cave crawler", ItemID.SHARK)
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                                .addItem(EquipmentSlot.HAT, ItemID.SPINY_HELMET)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 5)
                        )
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                                .addItem(ItemVariants.SUPER_ANTI_POISON, 1, 2)
                                .addItem(ItemID.TINDERBOX)
                                .addItem(ItemVariants.CANDLE_LANTERN)
                                .addItem(ItemID.ROPE)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.CANDLE, ItemID.LIT_CANDLE)) // cant have these they will explode gas
                        )
                        .setAfterLoadouts(() -> lightCandle.get() || handlePoison.get() || enterLumbridgeSwampDungeon.get())
                        .setSimpleName("Cave crawlers"));

        slayerTasks.put(663,
                new StandardCombat(new Area(3147, 9580, 3157, 9569), "Cave bug", ItemID.SHARK)
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                                .addItem(EquipmentSlot.HAT, ItemID.SPINY_HELMET)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 5)
                        )
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                                .addItem(ItemVariants.SUPER_ANTI_POISON, 1, 2)
                                .addItem(ItemID.TINDERBOX)
                                .addItem(ItemVariants.CANDLE_LANTERN)
                                .addItem(ItemID.ROPE)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.CANDLE, ItemID.LIT_CANDLE)) // cant have these they will explode gas
                        )
                        .setAfterLoadouts(() -> lightCandle.get() || handlePoison.get() || enterLumbridgeSwampDungeon.get())
                        .setSimpleName("Cave bug"));

//        new Area(1884, 5228, 1896, 5213)
        slayerTasks.put(9,
                new StandardCombat(new Area(1884, 5228, 1896, 5214), "Wolf", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setAfterLoadouts(() -> enterSOSFirstFloor.get() || handleGateOfWarAnswers.get())
                        .setSimpleName("Wolves"));


        slayerTasks.put(75,
                new StandardCombat(new Area(
                        new Tile(3011, 3471, 0),
                        new Tile(3001, 3468, 0),
                        new Tile(2994, 3468, 0),
                        new Tile(2993, 3473, 0),
                        new Tile(3002, 3479, 0),
                        new Tile(3003, 3481, 0),
                        new Tile(3001, 3486, 0),
                        new Tile(3003, 3489, 0),
                        new Tile(3013, 3483, 0)), "Icefiend", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("IceFiend"));


        slayerTasks.put(11,
                new StandardCombat(new Area(3093, 9915, 3102, 9904), "Skeleton", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Skeletons"));

        Area turoths = new Area(2714, 10014, 2730, 9994);
        slayerTasks.put(36,
                new StandardCombat(turoths, "Turoth", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.LEAF_BLADED)
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 10)
                        )
                        .setSimpleName("Turoths"));


        Area kurask = new Area(2689, 10008, 2708, 9988);
        slayerTasks.put(45,
                new StandardCombat(kurask, "Kurask", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.LEAF_BLADED)
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 10)
                        )
                        .setSimpleName("Turoths"));


        slayerTasks.put(22,
                // todo this area might be mcgrubors woods
                new StandardCombat(new Area(2625, 3318, 2642, 3313), "Guard dog", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Dogs"));

        slayerTasks.put(12,
                new StandardCombat(new Area(3244, 9918, 3238, 9911), "Ghost", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Ghosts"));


        slayerTasks.put(13,
                new StandardCombat(new Area(3282, 3355, 3299, 3342), "Black bear", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Bears"));

        slayerTasks.put(10,
                new StandardCombat(new Area(3139, 9908, 3151, 9889), "Zombie", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Zombies"));

        slayerTasks.put(7,
                new StandardCombat(new Area(3306, 3273, 3290, 3295), "Scorpion", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Scorpions"));

        Area dwarves = new Area(3008, 3461, 3025, 3455);

        slayerTasks.put(57,
                new StandardCombat(dwarves,  Arrays.asList("Dwarf", "Guard"), ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Dwarves"));

        slayerTasks.put(8,
                new StandardCombat(new Area(3337, 3496, 3357, 3474), "Bat", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Bats"));

        slayerTasks.put(3,
                new StandardCombat(new Area(3201, 3208, 3217, 3201), "Rat", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Rats"));

        slayerTasks.put(1,
                new StandardCombat(new Area(2872, 3169, 2886, 3141), "Monkey", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Monkeys"));

        slayerTasks.put(2,
                new StandardCombat(new Area(3244, 3243, 3260, 3225), "Goblin", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("goblins"));

        slayerTasks.put(4,
                new StandardCombat(new Area(3207, 9623, 3219, 9615), "Spider", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Spiders"));

        slayerTasks.put(5,
                new StandardCombat(new Area(3225, 3300, 3236, 3287), "Chicken", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Birds/chicken"));

        slayerTasks.put(6,
                new StandardCombat(new Area(3253, 3255, 3265, 3296), "Cow", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Cows"));

        slayerTasks.put(39,
                new StandardCombat(new Area(new Tile(3408, 3552, 0),
                        new Tile(3424, 3552, 0),
                        new Tile(3425, 3547, 0),
                        new Tile(3431, 3546, 0),
                        new Tile(3433, 3541, 0),
                        new Tile(3415, 3541, 0),
                        new Tile(3415, 3544, 0),
                        new Tile(3409, 3544, 0)), "Crawling Hand", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Crawling hand"));

        Area bansheeArea = new Area(
                new Tile(3432, 3540, 0),
                new Tile(3436, 3541, 0),
                new Tile(3437, 3547, 0),
                new Tile(3431, 3547, 0),
                new Tile(3431, 3556, 0),
                new Tile(3437, 3556, 0),
                new Tile(3436, 3551, 0),
                new Tile(3441, 3551, 0),
                new Tile(3450, 3551, 0),
                new Tile(3448, 3542, 0),
                new Tile(3452, 3536, 0),
                new Tile(3448, 3530, 0),
                new Tile(3442, 3534, 0),
                new Tile(3433, 3534, 0));


        slayerTasks.put(38,
                new StandardCombat(bansheeArea, "Banshee", ItemID.SHARK)
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                                .addItem(EquipmentSlot.HAT, ItemID.EARMUFFS))
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Banshees"));


        slayerTasks.put(18,
                new StandardCombat(new Area(2862, 3589, 2880, 3584), "Mountain troll", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
//                        .setCannonTile(2867, 3587)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.CANNON_LOADOUT)
                        .setSimpleName("Trolls"));


        slayerTasks.put(31,
                new StandardCombat(new Area(2404, 9805, 2420, 9782), "Hellhound", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
//                        .setCannonTile(2867, 3587)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.CANNON_LOADOUT)
                        .setSimpleName("HellHounds"));


        Area fireGiants = new Area(
                new Tile(2405, 9788, 0),
                new Tile(2407, 9781, 0),
                new Tile(2421, 9778, 0),
                new Tile(2420, 9766, 0),
                new Tile(2392, 9765, 0),
                new Tile(2386, 9782, 0),
                new Tile(2394, 9795, 0));
        slayerTasks.put(16,
                new StandardCombat(fireGiants, "Fire giant", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
//                        .setCannonTile(2867, 3587)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.CANNON_LOADOUT)
                        .setSimpleName("Fire giants"));

        slayerTasks.put(79, new StandardCombat(new Area(2464, 9812, 2488, 9794), "Ankou", ItemID.SHARK)
                .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
//                        .setCannonTile(2867, 3587)
                .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                .setInventoryLoadout(SlayerLoadouts.CANNON_LOADOUT)
                .setSimpleName("Ankous"));

//        new Area(1654, 10003, 1677, 9991)
        slayerTasks.put(35,
                new StandardCombat(new Area(1664, 10002, 1676, 9994), "Dagannoth", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
                        .setAfterLoadouts(enterKourendSlayerCave)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Dags"));

        Area blacks = new Area(1715, 10094, 1725, 10073);
//        slayerTasks.put(30,
//                new StandardCombat(blacks, "Black demon", ItemID.SHARK)
//                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
//                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
//                        .setAfterLoadouts(enterKourendSlayerCave)
//                        .setPrependLogic(() -> {
//                            if (BankLocation.GRAND_EXCHANGE.getArea(50).contains(Players.getLocal())) {
//                                if (Prayers.isActive(Prayer.PROTECT_FROM_MELEE))
//                                    Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
//                            }
//                            return false;
//                        })
//                        .setEquipmentLoadout(SlayerLoadouts.PRAYER_LOADOUT)
//                        .setInventoryLoadout(SlayerLoadouts.PRAYER_GEM_INVENTORY)
//                        .setSimpleName("Black D"));
//
        Area greaterDemonsArea = new Area(
                new Tile(1679, 10083, 0),
                new Tile(1680, 10090, 0),
                new Tile(1683, 10092, 0),
                new Tile(1694, 10092, 0),
                new Tile(1693, 10087, 0),
                new Tile(1686, 10081, 0));

        slayerTasks.put(29,
                new StandardCombat(greaterDemonsArea, "Greater demon", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
                        .setAfterLoadouts(enterKourendSlayerCave)
                        .setPrependLogic(() -> {
                            if (BankLocation.GRAND_EXCHANGE.getArea(50).contains(Players.getLocal())) {
                                if (Prayers.isActive(Prayer.PROTECT_FROM_MELEE))
                                    Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
                            }
                            return false;
                        })
                        .setEquipmentLoadout(SlayerLoadouts.PRAYER_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.PRAYER_GEM_INVENTORY)
                        .setSimpleName("Greater Demons"));

        Area steelDragons = new Area(1603, 10062, 1613, 10049);
        slayerTasks.put(-88888, // this is val: 60 but its an unsupported task until i map the annoying brimhaven dungeon
                new StandardCombat(BRIMHAVEN_DUNGEON_ENTRANCE, "Steel dragon", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
                        .setPrependLogic(() -> {
                            if (steelDragons.contains(Players.getLocal())) {
                                Item antiFire = ItemVariants.ANTI_FIRE_POTION.getItem();
                                if (antiFire != null && PlayerSettings.getBitValue(3981) < 3) {
                                    Logger.info("Drinking antifire");
                                    antiFire.interact("Drink");
                                    return true;
                                }
                            }

                            if (BankLocation.GRAND_EXCHANGE.getArea(50).contains(Players.getLocal())) {
                                if (Prayers.isActive(Prayer.PROTECT_FROM_MELEE))
                                    Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
                            }
                            return false;
                        })
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                                .remove(x -> x.getSlot() == EquipmentSlot.SHIELD)
                                .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)
                        )
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                                .addItem(ItemVariants.PRAYER_POTION, 1, 4)
                                .addItem(ItemVariants.ANTI_FIRE_POTION, 1, 2)
                        )
                        .setSimpleName("Steel drags"));


        slayerTasks.put(53,
                new StandardCombat(KALPHITE_WORKERS, "Kalphite Worker", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
//                        .setCannonTile(2867, 3587)
                        .setAfterLoadouts(enterKalphiteCave)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_BLACK_MASK)
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                                .addItem(ItemID.COINS_995, 2000)
                                .setEnabledCondition(() -> Inventory.count(ItemID.COINS_995) < 200)
                                .setStrictSupplier(() -> Players.getLocal().getY() < 5000 && Inventory.isFull())
                        )
                        .setSimpleName("Kalphite"));


        Area bloodVeldArea = new Area(2432, 9826, 2457, 9813);
        slayerTasks.put(48,
                new StandardCombat(bloodVeldArea, "Bloodveld", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
//                        .setCannonTile(2437, 9820)
                        .setEquipmentLoadout(SlayerLoadouts.PRAYER_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.PRAYER_GEM_INVENTORY)
                        .setSimpleName("Bloodvelds"));


        Area wyrm = new Area(1285, 10204, 1255, 10176);
        slayerTasks.put(111,
                new StandardCombat(wyrm, "Wyrm", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
                        .setOverhead(Prayer.PROTECT_FROM_MAGIC)
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.PRAYER_LOADOUT)
                                .remove(x -> x.getSlot() == EquipmentSlot.FEET || x.getSlot() == EquipmentSlot.HAT)
                                .addItem(EquipmentSlot.HAT, ItemID.BLACK_MASK)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.STRENGTH) >= 20
                                        && Skills.getRealLevel(Skill.DEFENCE) >= 10
                                        && Combat.getCombatLevel() >= 40
                                )
                                .addItem(EquipmentSlot.FEET, ItemID.BOOTS_OF_STONE)
                        )
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.PRAYER_GEM_INVENTORY)
                                .addItem(ItemVariants.SKILLS_NECKLACE)
                        )
                        .setSimpleName("Wyrm"));

        Area spectres = new Area(
                new Tile(3408, 3552, 1),
                new Tile(3419, 3552, 1),
                new Tile(3419, 3547, 1),
                new Tile(3431, 3547, 1),
                new Tile(3431, 3536, 1),
                new Tile(3423, 3533, 1),
                new Tile(3415, 3534, 1),
                new Tile(3412, 3531, 1),
                new Tile(3408, 3531, 1),
                new Tile(3405, 3535, 1),
                new Tile(3405, 3538, 1),
                new Tile(3417, 3539, 1),
                new Tile(3417, 3543, 1),
                new Tile(3408, 3543, 1));

        slayerTasks.put(41,
                new StandardCombat(spectres, "Aberrant spectre", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
                        .setOverhead(Prayer.PROTECT_FROM_MAGIC)
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.PRAYER_LOADOUT)
                                .addItem(EquipmentSlot.HAT, ItemID.NOSE_PEG))
                        .setInventoryLoadout(SlayerLoadouts.PRAYER_GEM_INVENTORY)
                        .setSimpleName("Spectres"));

        Area gargs = new Area(3430, 3554, 3452, 3531, 2);
        slayerTasks.put(63,
                new StandardCombat(new Area(3147, 9580, 3157, 9569), "Cave bug", ItemID.SHARK)
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                                .addItem(EquipmentSlot.HAT, ItemID.SPINY_HELMET)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 5)
                        )
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                                .addItem(ItemVariants.SUPER_ANTI_POISON, 1, 2)
                                .addItem(ItemID.TINDERBOX)
                                .addItem(ItemVariants.CANDLE_LANTERN)
                                .addItem(ItemID.ROPE)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.CANDLE, ItemID.LIT_CANDLE)) // cant have these they will explode gas
                        )
                        .setAfterLoadouts(() -> lightCandle.get() || handlePoison.get() || enterLumbridgeSwampDungeon.get())
                        .setSimpleName("Cave bug"));

        slayerTasks.put(46,
                new StandardCombat(gargs, "Gargoyle", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > minLootValue)
                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.PRAYER_LOADOUT)
                                .addItem(EquipmentSlot.HAT, ItemID.NOSE_PEG))
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.PRAYER_GEM_INVENTORY)
                                .addItem(ItemID.ROCK_HAMMER)
                                .addItem(ItemID.NATURE_RUNE, 1, 200)
                                .addItem(ItemID.FIRE_RUNE, 3, 600)
                        )
                        .setSimpleName("Gargoyles"));


        Area lizards = new Area(3383, 3081, 3455, 3033);
        slayerTasks.put(68,
                new StandardCombat(lizards, "Small Lizard", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.DESERT_CLOTHES)
                        .setInventoryLoadout(SlayerLoadouts.DESERT_FOOD_GEM_INVENTORY)
                        .setPrependLogic(() -> {
                            Character garg = Players.getLocal().getInteractingCharacter();
                            if (garg != null && garg.getName().toLowerCase().contains("lizard") && garg.getHealthPercent() < 3) {
                                Item cooler = Inventory.get(ItemID.ICE_COOLER);
                                if (cooler != null) {
                                    Logger.info("cool lizard");
                                    cooler.useOn(garg);
                                }
                                return true;
                            }
                            return SlayerTaskMap.getIceCoolers.get();
                        })
                        .setAfterLoadouts(SlayerTaskMap.enterDesert)
                        .setSimpleName("Lizards"));

        return execSlayerTask();
    }


    private static final Area VAULT_OF_WAR = new Area(1853, 5251, 1921, 5183);
    private static final Area BARBARIAN_VILLAGE = new Area(3077, 3422, 3083, 3417);
    private static final Supplier<Boolean> enterSOSFirstFloor = () -> {
        if (VAULT_OF_WAR.contains(Players.getLocal())) {
            return false;
        }

        if (!BARBARIAN_VILLAGE.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(BARBARIAN_VILLAGE.getRandomTile());
            return true;
        }

        // todo interact with the entrance
        GameObject entrance = GameObjects.closest("Entrance");
        if (entrance != null && entrance.interact("Climb-down")) {
            Sleep.sleepUntil(() -> VAULT_OF_WAR.contains(Players.getLocal()), 4000);
        }
        return true;
    };

    private static final Supplier<Boolean> handleGateOfWarAnswers = () -> {
        if (Dialogues.inDialogue() || Dialogues.areOptionsAvailable()) {
            Logger.info("Solving dialogue - gate of war");

            if (Dialogues.areOptionsAvailable()) {
                boolean anymatch = Arrays.stream(Dialogues.getOptions()).noneMatch(opt -> Arrays.asList(StrongholdAnswers.answers).contains(opt));
                if (anymatch) {
                    Logger.info("None match");
                    Walking.walkExact(Players.getLocal().getTile());
                }

                Dialogues.chooseFirstOption(StrongholdAnswers.answers);
                Sleep.sleep(1200);
                return true;
            }

            Dialogues.continueDialogue();
            Sleep.sleep(1200);
            return true;
        }

        return false;
    };


    private static final Supplier<Boolean> lightCandle = () -> {
        if (Inventory.containsAll(ItemID.CANDLE, ItemID.TINDERBOX)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Inventory.combine(ItemID.CANDLE, ItemID.TINDERBOX);
            Sleep.sleepUntil(() -> Inventory.contains(ItemID.LIT_CANDLE), 2400);
            return true;
        }

        if (Inventory.containsAll(ItemID.CANDLE_LANTERN, ItemID.TINDERBOX)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Inventory.combine(ItemID.CANDLE_LANTERN, ItemID.TINDERBOX);
            Sleep.sleepUntil(() -> Inventory.contains(ItemID.CANDLE_LANTERN_4531), 2400);
            return true;
        }
        return false;
    };

    private static final Area SWAMP_CAVES_ENTRANCE = new Area(3163, 3178, 3176, 3167);
    private static final Supplier<Boolean> enterLumbridgeSwampDungeon = () -> {
        if (Players.getLocal().getY() < 6000) {
            if (!SWAMP_CAVES_ENTRANCE.contains(Players.getLocal())) {
                if (Walking.shouldWalk(8)) Walking.walk(SWAMP_CAVES_ENTRANCE.getCenter());
                return true;
            }
            // todo handle widgets and rope for hole
            WidgetChild darknessWarning = Widgets.get(x -> x.getText().contains("NOT scared of the dark"));
            if (darknessWarning != null) {
                darknessWarning.interact("Yes");
                return true;
            }

            if (Dialogues.inDialogue()) {
                Inventory.get(ItemID.ROPE).useOn(GameObjects.closest("Dark hole"));
                return true;
            }
            GameObject hole = GameObjects.closest("Dark hole");
            if (hole != null && hole.interact("Climb-down")) {
                Sleep.sleepUntil(() -> Players.getLocal().getY() > 7000, 2400);
            }
            return true;
        }
        return false;
    };

    private static Supplier<Boolean> handlePoison = () -> {
        if (Combat.isPoisoned()) {
            // npe but idc
            ItemVariants.SUPER_ANTI_POISON.getItem().interact("Drink");
            Sleep.sleepUntil(() -> !Combat.isPoisoned(), 1300);
            return true;
        }
        return false;
    };

    static final Area KOUREND_CASTLE = new Area(1634, 3678, 1641, 3668);
    static final Area KOUREND_CATACOMBS = new Area(1592, 10116, 1739, 9973);
    private static Supplier<Boolean> enterKourendSlayerCave = () -> {
        if (!KOUREND_CATACOMBS.contains(Players.getLocal())) {
            Logger.info("Entering catacombs");
            if (!KOUREND_CASTLE.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(KOUREND_CASTLE);
                return true;
            }

            GameObject pillar = GameObjects.closest(x -> x.hasAction("Investigate"));
            if (pillar != null && pillar.interact("Investigate")) {
                Sleep.sleepUntil(() -> KOUREND_CATACOMBS.contains(Players.getLocal()), 2400);
            }
            return true;
        }

        return false;
    };

    public static final Area BRIMHAVEN_DUNGEON_ENTRANCE = new Area(2740, 3158, 2748, 3150);

    public static final Area KALPHITE_WORKERS = new Area(3319, 9509, 3329, 9495);
    public static final Area KALPHITE_ENTRANCE = new Area(3300, 3127, 3307, 3118);
    public static final Area SHANTY_PASS = new Area(3300, 3127, 3307, 3118);
    public static final Area PAST_PASS = new Area(
            new Tile(3299, 3116, 0),
            new Tile(3310, 3117, 0),
            new Tile(3321, 3129, 0),
            new Tile(3334, 3126, 0),
            new Tile(3317, 3104, 0),
            new Tile(3293, 3106, 0));
    public static final Area WHOLE_KALPHITE_CAVE = new Area(3259, 9551, 3351, 9470);

    private static Supplier<Boolean> enterKalphiteCave = () -> {

        WebPathResponse path = WebPathQuery.builder()
                .to(KALPHITE_WORKERS.getCenter())
                .withItem(new Item(ItemID.SHANTAY_PASS, 1))
                .build()
                .calculate();

        path.getEntranceItems()
                .stream().filter(Objects::nonNull)
                .forEach(x -> Logger.info("Entrance Item: " + x.getName()));
        path.getRequiredItems()
                .stream().filter(Objects::nonNull)
                .forEach(x -> Logger.info("Required Item: " + x.getName()));

        boolean requiresPass = path.getEntranceItems().stream()
                .filter(Objects::nonNull)
                .anyMatch(x -> x.getName().equals("Shantay pass"));

        if (!PAST_PASS.contains(Players.getLocal())
                && !WHOLE_KALPHITE_CAVE.contains(Players.getLocal())
                && !Inventory.contains(ItemID.SHANTAY_PASS)) {
            if (!SHANTY_PASS.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(SHANTY_PASS);
                return true;
            }

            NPC shantay = NPCs.closest("Shantay");
            if (shantay != null && shantay.interact("Buy-pass")) {
                Sleep.sleepUntil(() -> Inventory.contains(ItemID.SHANTAY_PASS), 2400);
            }
            return true;
        }
        return false;
    };

    // not the whole desert just the entrance and lizard area
    static Area DESERT = new Area(3285, 3115, 3465, 2995);
    public static Supplier<Boolean> enterDesert = () -> {
        if (!DESERT.contains(Players.getLocal())) {
            if (!Inventory.contains(ItemID.SHANTAY_PASS)) {
                if (!SHANTY_PASS.contains(Players.getLocal())) {
                    if (Walking.shouldWalk()) Walking.walk(SHANTY_PASS);
                    return true;
                }

                NPC shantay = NPCs.closest("Shantay");
                if (shantay != null && shantay.interact("Buy-pass")) {
                    Sleep.sleepUntil(() -> Inventory.contains(ItemID.SHANTAY_PASS), 2400);
                }
            }
            if (Walking.shouldWalk()) Walking.walk(DESERT);

            return true;
        }
        return false;
    };

    public static Supplier<Boolean> getIceCoolers = () -> {
        if (!OwnedItems.contains(ItemID.ICE_COOLER)) {
            Logger.info("Getting ice cooler");
            Logger.info("ice coolers" + new GetIceCoolers().executed());
            return true;
        }
        return false;
    };
}
