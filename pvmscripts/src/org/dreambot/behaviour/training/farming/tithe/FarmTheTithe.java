package org.dreambot.behaviour.training.farming.tithe;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.map.Region;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class FarmTheTithe extends Fractal {
    public static final int EMPTY_ALLOTMENT_ID = 27383;
    public static final int WATER_BARREL_ID = 5598;
    public static final int FRUIT_DEPOSIT = 2431;
    // golonova
    public static final int UNWATERED_GOLONOVA_SEED = 27384;
    public static final int WATERED_GOLONOVA_SEED = 27385;
    // 2nd stage of growing
    public static final int UNWATERED_GOLONOVA_PLANT = 27387;
    public static final int WATERED_GOLONOVA_PLANT = 27388;
    // 3rd stage of growing
    public static final int UNWATERED_BIG_GOLONOVA_PLANT = 27390;
    public static final int WATERED_BIG_GOLONOVA_PLANT = 27391;
    // finished growing
    public static final int FINISHED_GOLONOVA = 273893;

    // BOLOGANO - lvl 54
    public static final int UNWATERED_BOLOGANO_SEED = 27395;
    public static final int WATERED_BOLOGANO_SEED = 27396;
    // 2nd stage of growing
    public static final int UNWATERED_BOLOGANO_PLANT = 27398;
    public static final int WATERED_BOLOGANO_PLANT = 27399;
    // 3rd stage of growing
    public static final int UNWATERED_BIG_BOLOGANO_PLANT = 27401;
    public static final int WATERED_BIG_BOLOGANO_PLANT = 27402;
    // finished growing
    public static final int FINISHED_BOLOGANO = 273893;

    // ids for plants that if they exist you shouldnt plant new ones
    public static final List<Integer> grownPlants = Arrays.asList(
            WATERED_GOLONOVA_PLANT,
            WATERED_BOLOGANO_PLANT,
            UNWATERED_BIG_BOLOGANO_PLANT,
            UNWATERED_BIG_GOLONOVA_PLANT,
            WATERED_BIG_GOLONOVA_PLANT,
            WATERED_BIG_BOLOGANO_PLANT,
            FINISHED_GOLONOVA,
            FINISHED_BOLOGANO
    );


    /*
    if there is an unwatered seed, water it
    if there is 24 (watered seeds + unwatered  small plant), water the small plants
    if there is 24 (watered small plants + unwatered big plants) water the big plant
    harvest and replant

     */

    // Tiles (uninstanced) for the plots
    List<Tile> allotmentTiles = Arrays.asList(
            // south side
            new Tile(1810, 3488),
            new Tile(1815, 3488),
            new Tile(1810, 3491),
            new Tile(1815, 3491),
            new Tile(1810, 3494),
            new Tile(1815, 3494),
            new Tile(1810, 3497),
            new Tile(1815, 3497),

            // north side
            new Tile(1810, 3503),
            new Tile(1815, 3503),
            new Tile(1810, 3506),
            new Tile(1815, 3506),
            new Tile(1810, 3509),
            new Tile(1815, 3509),
            new Tile(1810, 3512),
            new Tile(1815, 3512),

            // north side 3nd row
            new Tile(1820, 3512),
            new Tile(1820, 3509)
//            new Tile(1820, 3506),
//            new Tile(1820, 3503)
    );

    Filter<Item> fillableCan = x -> x.getId() != ItemID.WATERING_CAN8 && x.getName().contains("Watering");

    public FarmTheTithe(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        if (Walking.getRunEnergy() < 20) {
            Item pot = ItemVariants.STAMINA_POTION.getItem();
            if (pot != null) pot.interact("Drink");
            Sleep.sleep(850);
        }


        if (!Walking.isRunEnabled()) {
            Walking.toggleRun();
        }

        // if nothing is planted, refill
        boolean nothingPlanted = allotmentTiles.stream()
                .map(Region::toInstance)
                .filter(x -> !x.isEmpty())
                .map(x -> x.get(0))
                .noneMatch(x -> {
                    GameObject o = GameObjects.getTopObjectOnTile(x);
                    if (o == null) return false;
                    return o.getId() != EMPTY_ALLOTMENT_ID;
                });

        if (nothingPlanted || !Inventory.contains(x -> x.getName().contains("Watering") && x.getId() != ItemID.WATERING_CAN)) {
            Logger.info("Nothing planted, refilling water jugs");
            // refill water
            if (Inventory.contains(fillableCan)) {
                Item can = Inventory.get(fillableCan);
                ObjectUtil.useOn("Water Barrel", can);
                Sleep.sleepUntil(() -> !Inventory.contains(fillableCan),
                        () -> Players.getLocal().isMoving() || Players.getLocal().isAnimating(),
                        4400, 100);
                return ReactionGenerator.getNormal();
            }
        }

        // seed planting phase
        GameObject firstEmptyPlot = allotmentTiles.stream()
                .map(Region::toInstance)
                .filter(x -> !x.isEmpty())
                .map(x -> x.get(0))
                .filter(x -> {
                    GameObject top = GameObjects.getTopObjectOnTile(x);
                    if (top == null) return false;
                    if (top.getId() == EMPTY_ALLOTMENT_ID) return true;
                    if (top.getId() == getUnwateredSeedID()) return true;
                    return false;
                }).map(GameObjects::getTopObjectOnTile)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        // if its ready to harvest skip this
        if (firstEmptyPlot != null && GameObjects.closest(x -> grownPlants.contains(x.getId()) || x.hasAction("Harvest")) == null) {
            // deposit fruits
            if (Inventory.count(x -> x.getName().contains("fruit")) >= 100) {
                ObjectUtil.interact("Sack");
                Sleep.sleepUntil(() -> !Inventory.contains(x -> x.getName().contains("fruit")), 4400);
                return ReactionGenerator.getNormal();
            }

            // hop on clear
            if (GameObjects.closest(x -> x.hasAction("Clear")) != null) {
                Logger.info("Dead plant present, hopping worlds to reset");
                WorldHopper.hopWorld(Worlds.getRandomWorld(w -> w.isMembers() && w.getMinimumLevel() == 0 && w.isNormal()));
                return ReactionGenerator.getNormal();
            }

            Logger.info("Plant/Water seeds");
            if (firstEmptyPlot.getId() == EMPTY_ALLOTMENT_ID) {
                Item seed = Inventory.get(getSeedItemID());
                if (seed == null) {
                    Logger.info("Cant find seeds");
                    if (Inventory.contains(ItemID.BOLOGANO_FRUIT, ItemID.GOLOVANOVA_FRUIT, ItemID.LOGAVANO_FRUIT)) {
                        log("Still has fruit, deposit");
                        ObjectUtil.interact("Sack");
                        return ReactionGenerator.getNormal();
                    }
                    ObjectUtil.interact("Farm door");
                    return ReactionGenerator.getNormal();
                }

                if (!Menu.isMenuManipulationActive() && firstEmptyPlot.distance() > 10) {
                    log("Walk towards empty plot");
                    Walking.walk(firstEmptyPlot);
                    return ReactionGenerator.getNormal();
                }
                seed.useOn(firstEmptyPlot);
                Sleep.sleepUntil(() -> !firstEmptyPlot.exists() || firstEmptyPlot.getId() != EMPTY_ALLOTMENT_ID, 1000);
                return ReactionGenerator.getNormal();
            }

            firstEmptyPlot.interact(); // i think you can always just click
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 2400);
            return ReactionGenerator.getNormal();
        }

        // 1st plant phase
        GameObject firstPhasePlant = allotmentTiles.stream()
                .map(Region::toInstance)
                .filter(x -> !x.isEmpty())
                .map(x -> x.get(0))
                .filter(x -> {
                    GameObject top = GameObjects.getTopObjectOnTile(x);
                    if (top == null) return false;
                    if (top.getId() == getUnwateredSmallPlantID()) return true;
                    return false;
                }).map(GameObjects::getTopObjectOnTile)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (firstPhasePlant != null) {
            firstPhasePlant.interact();
            Sleep.sleepUntil(() -> !firstPhasePlant.exists() || !firstPhasePlant.hasAction("Water"), 2400);
            return ReactionGenerator.getNormal();
        }

        // 2nd stage
        GameObject secondPhasePlant = allotmentTiles.stream()
                .map(Region::toInstance)
                .filter(x -> !x.isEmpty())
                .map(x -> x.get(0))
                .filter(x -> {
                    GameObject top = GameObjects.getTopObjectOnTile(x);
                    if (top == null) return false;
                    if (top.getId() == getUnwateredBigPlantID()) return true;
                    return false;
                }).map(GameObjects::getTopObjectOnTile)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (secondPhasePlant != null) {
            secondPhasePlant.interact();
            Sleep.sleepUntil(() -> !secondPhasePlant.exists() || !secondPhasePlant.hasAction("Water"), 2400);
            return ReactionGenerator.getNormal();
        }


        // finsihed
        GameObject finished = allotmentTiles.stream()
                .map(Region::toInstance)
                .filter(x -> !x.isEmpty())
                .map(x -> x.get(0))
                .filter(x -> {
                    GameObject top = GameObjects.getTopObjectOnTile(x);
                    if (top == null) return false;
                    if (top.hasAction("Harvest")) return true;
                    return false;
                }).map(GameObjects::getTopObjectOnTile)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (finished != null) {
            finished.interact();
            Sleep.sleepUntil(() -> !finished.exists() || !finished.hasAction("Harvest"), 2400);
            return ReactionGenerator.getNormal();
        }


        return ReactionGenerator.getNormal();
    }

    private int getUnwateredSeedID() {
        int farm = Skills.getRealLevel(Skill.FARMING);
        if (farm >= 54) return UNWATERED_BOLOGANO_SEED;
        return UNWATERED_GOLONOVA_SEED;
    }

    private int getWateredSeedID() {
        int farm = Skills.getRealLevel(Skill.FARMING);
        if (farm >= 54) return WATERED_BOLOGANO_SEED;
        return WATERED_GOLONOVA_SEED;
    }

    private int getUnwateredSmallPlantID() {
        int farm = Skills.getRealLevel(Skill.FARMING);
        if (farm >= 54) return UNWATERED_BOLOGANO_PLANT;
        return UNWATERED_GOLONOVA_PLANT;
    }

    private int getWateredSmallPlantID() {
        int farm = Skills.getRealLevel(Skill.FARMING);
        if (farm >= 54) return WATERED_BOLOGANO_PLANT;
        return WATERED_GOLONOVA_PLANT;
    }

    private int getUnwateredBigPlantID() {
        int farm = Skills.getRealLevel(Skill.FARMING);
        if (farm >= 54) return UNWATERED_BIG_BOLOGANO_PLANT;
        return UNWATERED_BIG_GOLONOVA_PLANT;
    }

    private int getWateredBigPlantID() {
        int farm = Skills.getRealLevel(Skill.FARMING);
        if (farm >= 54) return WATERED_BIG_BOLOGANO_PLANT;
        return WATERED_BIG_GOLONOVA_PLANT;
    }

    private int getGrownPlantID() {
        int farm = Skills.getRealLevel(Skill.FARMING);
        if (farm >= 54) return FINISHED_BOLOGANO;
        return FINISHED_GOLONOVA;
    }

    private int getSeedItemID() {
        int farm = Skills.getRealLevel(Skill.FARMING);
        if (farm >= 54) return ItemID.BOLOGANO_SEED;
        return ItemID.GOLOVANOVA_SEED;
    }
}
