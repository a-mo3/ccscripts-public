package org.dreambot.behaviour.runecraft;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.behaviour.combat.GenericCombat;
import org.dreambot.behaviour.mining.MiningDTO;
import org.dreambot.behaviour.mining.MiningMode;
import org.dreambot.behaviour.smithing.FurnaceLocation;
import org.dreambot.behaviour.smithing.SmithingBar;
import org.dreambot.behaviour.smithing.SmithingDTO;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.loadouts.InventoryLoadout;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.behavior.generic.TransactAtStore;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.Items;
import org.dreambot.loadouts.data.ShopLocation;
import org.dreambot.utility.OwnedItems;

public class RunecraftItems {
    public static final Area WIZARD_LOC = new Area(2984, 3277, 2999, 3264);
    public static final Area WIZARD_CABBAGE = new Area(2946, 3260, 2953, 3255);

    public static final Area[] COW_PEN = new Area[]{
            // lum
            new Area(
                    new Tile(3240, 3298, 0),
                    new Tile(3265, 3298, 0),
                    new Tile(3266, 3255, 0),
                    new Tile(3253, 3254, 0),
                    new Tile(3253, 3273, 0),
                    new Tile(3249, 3277, 0),
                    new Tile(3240, 3285, 0)
            ),
            // falador
            new Area(3021, 3313, 3042, 3297),


    };

    public static final Area[] REST_AREAS = new Area[]{
            // lum potato field
            new Area(
                    new Tile(3260, 3320, 0),
                    new Tile(3265, 3318, 0),
                    new Tile(3265, 3299, 0),
                    new Tile(3242, 3300, 0),
                    new Tile(3242, 3304, 0)
            ),
            // falador cabbage patch
            new Area(3047, 3297, 3060, 3288)
    };
    private static int random = Calculations.random(Math.min(REST_AREAS.length, COW_PEN.length));

    public static final InventoryLoadoutItem AIR_TALI = new InventoryLoadoutItem(ItemID.AIR_TALISMAN)
            .setRestockMethod(
                    new GenericCombat(() -> !OwnedItems.contains(ItemID.AIR_TALISMAN), WIZARD_LOC,
                            x -> "Air wizard".equals(x.getName()))
                            .setRestLocation(WIZARD_CABBAGE)
                            .setLootFilter(x -> ItemID.AIR_TALISMAN == x.getId())
                            .setRunAwayThreshold(3)
            );

    // air wizard max hit 1, earth max hit 6, fire 4, water 2

    public static final InventoryLoadoutItem WATER_TALI = new InventoryLoadoutItem(ItemID.WATER_TALISMAN)
            .setRestockMethod(
                    new IronFractal(() -> !OwnedItems.contains(ItemID.WATER_TALISMAN)).addChildren(
                            new GenericCombat(() -> Skill.HITPOINTS.getLevel() < 25,
                                    COW_PEN[random],
                                    x -> !x.isInCombat() && "Cow".equals(x.getName()))
                                    .setLootFilter(x -> x.getId() == ItemID.COWHIDE)
                                    .setRunAwayThreshold(2)
                                    .setRestLocation(REST_AREAS[random])
                                    .setTrainPrayer(true)
                                    .setSimpleName("Kill cow for 25hp"),

                            new GenericCombat(() -> true,
                                    WIZARD_LOC,
                                    x -> "Water wizard".equals(x.getName()))
                                    .setRestLocation(WIZARD_CABBAGE)
                                    .setLootFilter(x -> ItemID.WATER_TALISMAN == x.getId())
                                    .setRunAwayThreshold(3)
                    )
            );

    public static final InventoryLoadoutItem FIRE_TALI = new InventoryLoadoutItem(ItemID.FIRE_TALISMAN)
            .setRestockMethod(
                    new IronFractal(() -> !OwnedItems.contains(ItemID.FIRE_TALISMAN)).addChildren(
                            new GenericCombat(() -> Skill.HITPOINTS.getLevel() < 25,
                                    COW_PEN[random],
                                    x -> !x.isInCombat() && "Cow".equals(x.getName()))
                                    .setLootFilter(x -> x.getId() == ItemID.COWHIDE)
                                    .setRunAwayThreshold(2)
                                    .setRestLocation(REST_AREAS[random])
                                    .setTrainPrayer(true)
                                    .setSimpleName("Kill cow for 25hp"),

                            new GenericCombat(() -> true,
                                    WIZARD_LOC,
                                    x -> "Fire wizard".equals(x.getName()))
                                    .setRestLocation(WIZARD_CABBAGE)
                                    .setLootFilter(x -> ItemID.FIRE_TALISMAN == x.getId())
                                    .setRunAwayThreshold(3)
                    )
            );

    public static final InventoryLoadoutItem EARTH_TALI = new InventoryLoadoutItem(ItemID.EARTH_TALISMAN)
            .setRestockMethod(() -> !OwnedItems.contains(ItemID.EARTH_TALISMAN),
                    new GenericCombat(() -> Skill.HITPOINTS.getLevel() < 25,
                            COW_PEN[random],
                            x -> !x.isInCombat() && "Cow".equals(x.getName()))
                            .setLootFilter(x -> x.getId() == ItemID.COWHIDE)
                            .setRunAwayThreshold(2)
                            .setRestLocation(REST_AREAS[random])
                            .setTrainPrayer(true)
                            .setSimpleName("Kill cow for 25hp"),

                    new GenericCombat(() -> true,
                            WIZARD_LOC,
                            x -> "Earth wizard".equals(x.getName()))
                            .setRestLocation(WIZARD_CABBAGE)
                            .setLootFilter(x -> ItemID.EARTH_TALISMAN == x.getId())
                            .setRunAwayThreshold(3)
            );

    public static final InventoryLoadoutItem SILVER_ORE = new InventoryLoadoutItem(ItemID.SILVER_ORE)
            .setInventoryMax(28).setInventoryMin(1)
            .setRestockMethod(new MiningDTO()
                    .setMode(MiningMode.SILVER)
                    .toFractal()
            );

    public static final InventoryLoadoutItem SILVER = new InventoryLoadoutItem(ItemID.SILVER_BAR)
            .setRestockMethod(
                    new GenericEntityInteraction(() -> !OwnedItems.contains(ItemID.SILVER_BAR),
                            () -> GameObjects.closest(FurnaceLocation.EDGE.getFurnaceFilter()))
                            .setEntityLocation(FurnaceLocation.EDGE.getArea())
                            .setInventoryLoadout(new InventoryLoadout().addItem(SILVER_ORE))
                            .setSimpleName("Smelt silver bar")
            );

    public static final InventoryLoadoutItem TIARA_MOULD = new InventoryLoadoutItem(ItemID.TIARA_MOULD)
            .setRestockMethod(
                    new TransactAtStore(() -> !OwnedItems.contains(ItemID.TIARA_MOULD), ShopLocation.ROMMIK_CRAFTY_SUPPLIES,
                            x -> x.getId() == ItemID.TIARA_MOULD)
                            // net fishing for coins not beautiful but ez
                            .addInventoryItem(Items.coinsNetFishing(120))

            );

    public static final InventoryLoadoutItem TIARA = new InventoryLoadoutItem(ItemID.TIARA)
            .setRestockMethod(
                    // todo crafting training to 23
                    new GenericEntityInteraction(() -> !OwnedItems.contains(ItemID.TIARA),
                            () -> GameObjects.closest(FurnaceLocation.EDGE.getFurnaceFilter()))
                            .setEntityLocation(FurnaceLocation.EDGE.getArea())
                            .setInventoryLoadout(new InventoryLoadout().addItem(SILVER_ORE))
                            .setSimpleName("Smelt tiara")
            );
}
