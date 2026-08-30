package org.dreambot.loadouts.data;

import lombok.Getter;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

/**
 * Items that spawn places so i dont have to write the full fractal constructor.
 */
@Getter
public enum ItemSpawn {
    RIMMINGTON_BRONZE_PICKAXE(new Area(2962, 3216, 2966, 3214), ItemID.BRONZE_PICKAXE, "Rimmington pickaxe"),
    LUM_BRONZE_PICKAXE(new Area(3226, 3225, 3231, 3213, 2), ItemID.BRONZE_PICKAXE, "Lum pickaxe"),
    DWARVEN_MINE_COIN(new Area(2998, 9803, 3007, 9794), ItemID.COINS_995, "Dwarf coins"),
    //    EGG(new Area(3167, 3308, 3186, 3288), ItemID.EGG, "Egg"), // egg in north lum farm, left of river
    SPADE(new Area(2980, 3370, 2984, 3368), ItemID.SPADE, "Spade"),
    LUM_BUCkET(new Area(3225, 3301, 3236, 3287), ItemID.BUCKET, "Bucket"),
    LUM_EGG(new Area(3225, 3301, 3236, 3287), ItemID.EGG, "Egg"),
    LUM_KITCHEN_POT(new Area(3204, 3217, 3212, 3212), ItemID.POT, "Pot"),
    // this is not actually an item spawn, but because of how many people kill cows we just hope theres one
    LUM_COW_BEEF(new Area(
            new Tile(3240, 3298, 0),
            new Tile(3265, 3298, 0),
            new Tile(3266, 3255, 0),
            new Tile(3253, 3255, 0),
            new Tile(3253, 3272, 0),
            new Tile(3249, 3278, 0),
            new Tile(3245, 3278, 0),
            new Tile(3240, 3286, 0)
    ), ItemID.RAW_BEEF, "Raw beef"),
    SHEARS(new Area(3188, 3275, 3192, 3270), ItemID.SHEARS, "Shears"),
    FALADOR_HAMMER(new Tile(2975, 3368, 1).getArea(2), ItemID.HAMMER, "Hammer")

    // this is a game object actually.
//    LUM_SMALL_NET( new Area(3243, 3159, 3246, 3153), ItemID.SMALL_FISHING_NET, "Lum fish net")
    ;

    final Area spawnLocation;
    final int itemId;
    final String simpleName;

    ItemSpawn(Area spawnLocation, int itemId, String simpleName) {
        this.spawnLocation = spawnLocation;
        this.itemId = itemId;
        this.simpleName = simpleName;
    }
}
