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
import org.dreambot.api.utilities.Sleep;
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
import org.dreambot.settings.script.ScriptSettings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class SlayerTaskMap {
    private static Map<String, Fractal> slayerTasks;

    public static boolean execSlayerTask(String task) {
        if (slayerTasks != null) {
            if (!slayerTasks.containsKey(task)) {
                Logger.warn("Unmapped slayer task " + SlayerBranch.task);
                Logger.info("Cancel task " + new CancelTaskEvent().executed());
                return false;
            } else {
                slayerTasks.get(task).run();
                return true;
            }
        }

        slayerTasks = new HashMap<>();

        slayerTasks.put("minotaurs",
                new StandardCombat(new Area(1886, 5200, 1907, 5186), "Minotaur", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setAfterLoadouts(() -> enterSOSFirstFloor.get() || handleGateOfWarAnswers.get())
                        .setSimpleName("Minotaurs"));


        slayerTasks.put("cave slimes",
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


        slayerTasks.put("cave crawlers",
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

        slayerTasks.put("cave bugs",
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
        slayerTasks.put("wolves",
                new StandardCombat(new Area(1884, 5228, 1896, 5214), "Wolf", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setAfterLoadouts(() -> enterSOSFirstFloor.get() || handleGateOfWarAnswers.get())
                        .setSimpleName("Wolves"));


        slayerTasks.put("icefiends",
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
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("IceFiend"));


        slayerTasks.put("skeletons",
                new StandardCombat(new Area(3093, 9915, 3102, 9904), "Skeleton", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Skeletons"));

        slayerTasks.put("kalphites",
                new StandardCombat(new Area(3318, 9510, 3329, 9493), "Kalphite worker", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        // todo this will need some custom logic.
                        .setSimpleName("Kalphites"));

        slayerTasks.put("dogs",
                // todo this area might be mcgrubors woods
                new StandardCombat(new Area(2625, 3318, 2642, 3313), "Guard dog", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Dogs"));

        slayerTasks.put("ghosts",
                new StandardCombat(new Area(3244, 9918, 3238, 9911), "Ghost", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Ghosts"));


        slayerTasks.put("bears",
                new StandardCombat(new Area(3282, 3355, 3299, 3342), "Black bear", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Bears"));

        slayerTasks.put("zombies",
                new StandardCombat(new Area(3139, 9908, 3151, 9889), "Zombie", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Zombies"));

        slayerTasks.put("scorpions",
                new StandardCombat(new Area(3306, 3273, 3290, 3295), "Scorpion", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Scorpions"));

        Area dwarves = new Area(3008, 3461, 3025, 3455);

        slayerTasks.put("dwarves",
                new StandardCombat(dwarves, "Dwarf", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Dwarves"));

        slayerTasks.put("bats",
                new StandardCombat(new Area(3337, 3496, 3357, 3474), "Bat", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Bats"));

        slayerTasks.put("rats",
                new StandardCombat(new Area(3201, 3208, 3217, 3201), "Rat", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Rats"));

        slayerTasks.put("monkeys",
                new StandardCombat(new Area(2872, 3169, 2886, 3141), "Monkey", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Monkeys"));

        slayerTasks.put("goblins",
                new StandardCombat(new Area(3244, 3243, 3260, 3225), "Goblin", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("goblins"));

        slayerTasks.put("spiders",
                new StandardCombat(new Area(3207, 9623, 3219, 9615), "Spider", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Spiders"));

        slayerTasks.put("birds",
                new StandardCombat(new Area(3225, 3300, 3236, 3287), "Chicken", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Birds/chicken"));

        slayerTasks.put("cows",
                new StandardCombat(new Area(3253, 3255, 3265, 3296), "Cow", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Cows"));

        slayerTasks.put("crawling hands",
                new StandardCombat(new Area(new Tile(3408, 3552, 0),
                        new Tile(3424, 3552, 0),
                        new Tile(3425, 3547, 0),
                        new Tile(3431, 3546, 0),
                        new Tile(3433, 3541, 0),
                        new Tile(3415, 3541, 0),
                        new Tile(3415, 3544, 0),
                        new Tile(3409, 3544, 0)), "Crawling Hand", ItemID.SHARK)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
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


        slayerTasks.put("banshees",
                new StandardCombat(bansheeArea, "Banshee", ItemID.SHARK)
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                                .addItem(EquipmentSlot.HAT, ItemID.EARMUFFS))
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Banshees"));


        slayerTasks.put("trolls",
                new StandardCombat(new Area(2862, 3589, 2880, 3584), "Mountain troll", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootValue)
//                        .setCannonTile(2867, 3587)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.CANNON_LOADOUT)
                        .setSimpleName("Trolls"));


        slayerTasks.put("hellhounds",
                new StandardCombat(new Area(2404, 9805, 2420, 9782), "Hellhound", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootValue)
//                        .setCannonTile(2867, 3587)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
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
        slayerTasks.put("fire giants",
                new StandardCombat(fireGiants, "Fire giant", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootValue)
//                        .setCannonTile(2867, 3587)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.CANNON_LOADOUT)
                        .setSimpleName("Fire giants"));

        slayerTasks.put("ankou", new StandardCombat(new Area(2464, 9812, 2488, 9794), "Ankou", ItemID.SHARK)
                .setLootFilter(x -> LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootValue)
//                        .setCannonTile(2867, 3587)
                .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                .setInventoryLoadout(SlayerLoadouts.CANNON_LOADOUT)
                .setSimpleName("Ankous"));

//        new Area(1654, 10003, 1677, 9991)
        slayerTasks.put("dagannoth",
                new StandardCombat(new Area(1664, 10002, 1676, 9994), "Dagannoth", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootValue)
//                        .setCannonTile(2867, 3587)
                        .setAfterLoadouts(enterKourendSlayerCave)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                        .setSimpleName("Dags"));

        Area blacks = new Area(1715, 10094, 1725, 10073);
        slayerTasks.put("black demons",
                new StandardCombat(blacks, "Black demon", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootValue)
                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
                        .setAfterLoadouts(enterKourendSlayerCave)
                        .setAppendLogic(() -> {
                            if (BankLocation.GRAND_EXCHANGE.getArea(50).contains(Players.getLocal())) {
                                if (Prayers.isActive(Prayer.PROTECT_FROM_MELEE))
                                    Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
                            }
                            return false;
                        })
                        .setEquipmentLoadout(SlayerLoadouts.PRAYER_LOADOUT)
                        .setInventoryLoadout(SlayerLoadouts.PRAYER_GEM_INVENTORY)
                        .setSimpleName("Black D"));

        Area steelDragons = new Area(1601, 10063, 1615, 10048);
        slayerTasks.put("steel peenor dragons",
                new StandardCombat(BRIMHAVEN_DUNGEON_ENTRANCE, "Steel dragon", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootValue)
                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
                        .setAppendLogic(() -> {
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
                                .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)
                        )
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY)
                                .addItem(ItemVariants.PRAYER_POTION, 1, 4)
                                .addItem(ItemVariants.ANTI_FIRE_POTION, 1, 2)
                        )
                        .setSimpleName("Steel drags"));


        slayerTasks.put("kalphite",
                new StandardCombat(KALPHITE_WORKERS, "Kalphite Worker", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootValue)
//                        .setCannonTile(2867, 3587)
                        .setAfterLoadouts(enterKalphiteCave)
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(new InventoryLoadout(SlayerLoadouts.FOOD_GEM_INVENTORY).addItem(ItemID.COINS_995, 1, 2000))
                        .setSimpleName("Kalphite"));

        return execSlayerTask(task);
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
}
