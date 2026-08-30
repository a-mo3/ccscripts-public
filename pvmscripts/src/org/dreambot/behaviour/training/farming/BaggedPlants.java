package org.dreambot.behaviour.training.farming;

import org.dreambot.behaviour.training.prayer.GetHouse;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

/**
 * Branch that will
 * get a house
 * buy bagged plants
 * fill 3 watering cans
 * build & remove plant spots
 */
public class BaggedPlants extends Fractal {
    public BaggedPlants(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        addChildren(
                new GetHouse().setSimpleName("Get a house"),
//                new FillWateringCans().setSimpleName("Get water"),
                new BuildPlantSpots(() -> true).setSimpleName("Make plants")
        );
    }
}
