package org.dreambot.loadouts.data;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.behaviour.combat.GenericCombat;
import org.dreambot.behaviour.fishing.FishingDTO;
import org.dreambot.behaviour.fishing.FishingMode;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.behavior.generic.TransactAtStore;
import org.dreambot.utility.OwnedItems;

/**
 * class to store static items and their associated methods
 * this is going to grow into a disgusting amount of fractal objects existing for no reason, idk if that will matter.
 */
public class Items {
    public static final InventoryLoadoutItem SMALL_FISHING_NET = new InventoryLoadoutItem(ItemID.SMALL_FISHING_NET)
            .setRandomRestockMethod(
                    // this is a game object not an item spawn.
                    new GenericEntityInteraction(() -> !OwnedItems.contains(ItemID.SMALL_FISHING_NET),
                            () -> GameObjects.closest(674))
                            .setEntityLocation(new Area(3243, 3159, 3246, 3153))
                            .setAction("Take")
                            .setSimpleName("Take lum net")
            );

    public static final InventoryLoadoutItem BRONZE_AXE_STUMP = new InventoryLoadoutItem()
            .setItemId(ItemID.BRONZE_AXE)
            // defaults are all 1
            // todo make a take item generic that hops worlds
            .setRestockMethod(new GenericEntityInteraction(() -> !OwnedItems.contains(ItemID.BRONZE_AXE),
                    // theres a log pile object "Logs" that has a bronze axe on it, this only works if you dont own an axe
                    new Tile(3186, 3277).getArea(2),
                    () -> GameObjects.closest(5581))
                    .setAction("Take-axe")
                    .setSimpleName("Get bronze axe at stump"));

    /**
     * for use when you only need like 1 coin, various ways
     */
    public static final InventoryLoadoutItem RANDOM_ONE_COIN = new InventoryLoadoutItem(ItemID.COINS_995)
            .setInventoryMax(2000).setInventoryMin(1).setRefill(1)
            .setRandomRestockMethod(new TransactAtStore(() -> OwnedItems.count(ItemID.COINS_995) < 1,
                            new Tile(3211, 3246).getArea(8),
                            () -> NPCs.closest(x -> x.getName().contains("Shop")),
                            i -> i.getId() == ItemID.BRONZE_AXE)
                            .setBuyMode(false)
                            .addInventoryItem(BRONZE_AXE_STUMP)
                            .setSimpleName("Sell an axe"),

                    // pick up 1 coin in the dwarven mine, not at all hcim safe.
                    new GenericEntityInteraction(ItemSpawn.DWARVEN_MINE_COIN)
            );

    public static final InventoryLoadoutItem BRONZE_PICKAXE_STORE = new InventoryLoadoutItem(ItemID.BRONZE_PICKAXE)
            .setRestockMethod(new TransactAtStore(ShopLocation.NURMOF_PICKAXES, ItemID.BRONZE_PICKAXE)
                    .addInventoryItem(RANDOM_ONE_COIN));

    public static final InventoryLoadoutItem RANDOM_BRONZE_PICAAXE = new InventoryLoadoutItem(ItemID.BRONZE_PICKAXE)
            .setRandomRestockMethod(
                    new TransactAtStore(ShopLocation.NURMOF_PICKAXES, ItemID.BRONZE_PICKAXE)
                            .addInventoryItem(RANDOM_ONE_COIN),
                    // pick one up at lum castle
                    new GenericEntityInteraction(ItemSpawn.LUM_BRONZE_PICKAXE),
                    new GenericEntityInteraction(ItemSpawn.RIMMINGTON_BRONZE_PICKAXE)
            );

    public static final InventoryLoadoutItem COINS_SELL_AXE = new InventoryLoadoutItem()
            .setItemId(ItemID.COINS_995)
            .setInventoryMax(20).setInventoryMin(5).setRefill(5)
            .setRestockMethod(new TransactAtStore(() -> OwnedItems.count(ItemID.COINS_995) < 5,
                    new Tile(3211, 3246).getArea(8),
                    () -> NPCs.closest(x -> x.getName().contains("Shop")),
                    i -> i.getId() == ItemID.BRONZE_AXE)
                    .setBuyMode(false)
                    .addInventoryItem(BRONZE_AXE_STUMP)
                    .setSimpleName("Sell an axe")
            );

    public static final InventoryLoadoutItem TINDERBOX = new InventoryLoadoutItem(ItemID.TINDERBOX)
            .setRestockMethod(
                    new TransactAtStore(ShopLocation.LUM_GENERAL, ItemID.TINDERBOX).addInventoryItem(COINS_SELL_AXE)
                            .setSimpleName("Buy tinderbox")
            );

    // todo higher tier axes

    public static final InventoryLoadoutItem FISHING_ROD = new InventoryLoadoutItem(ItemID.FISHING_ROD)
            .setRestockMethod(
                    new TransactAtStore(ShopLocation.GERRANTS_FISHY_BUSINESS, ItemID.FISHING_ROD)
                            .addInventoryItem(COINS_SELL_AXE)
            );

    public static final InventoryLoadoutItem FISHING_BAIT = new InventoryLoadoutItem(ItemID.FISHING_BAIT)
            .setRestockMethod(
                    new OpenPackFractal(ItemID.BAIT_PACK, new TransactAtStore(ShopLocation.GERRANTS_FISHY_BUSINESS, ItemID.BAIT_PACK),
                            ItemID.FISHING_BAIT, 100)
                            .addInventoryItem(coinsNetFishing(320))
            );

    public static InventoryLoadoutItem coinsNetFishing(int coinReq) {
        return new InventoryLoadoutItem(ItemID.COINS_995)
                .setInventoryMin(coinReq)
                .setInventoryMax(coinReq + 200)
                .setRestockMethod(
                        new FishingDTO()
                                .setLevelTaget(101)
                                .setMode(FishingMode.SMALL_NET)
                                .setSellAll(true)
                                .toFractal()
                                .setAcceptCondition(() -> OwnedItems.count(ItemID.COINS_995) < coinReq)
                                .setSimpleName("Net fish for " + coinReq + " gp")
                );
    }

    public static InventoryLoadoutItem FEATHERS_KILL_CHICKEN = new InventoryLoadoutItem(ItemID.FEATHER)
            .setRestockMethod(
                    new GenericCombat(() -> OwnedItems.count(ItemID.FEATHER) < 300,
                            new Area(3170, 3303, 3184, 3289),
                            x -> "Chicken".equals(x.getName()))
                            .setLootFilter(x -> x.getId() == ItemID.FEATHER)
                            .setRunAwayThreshold(2)
                            .setRestLocation(new Area(3167, 3282, 3173, 3276))
                            .setSimpleName("Kill chicken for feathers")
            );

    public static final InventoryLoadoutItem FLY_FISHING_ROD = new InventoryLoadoutItem(ItemID.FLY_FISHING_ROD)
            .setRestockMethod(
                    new TransactAtStore(ShopLocation.GERRANTS_FISHY_BUSINESS, ItemID.FLY_FISHING_ROD)
                            .addInventoryItem(COINS_SELL_AXE)
            );
}
