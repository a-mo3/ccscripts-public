package org.dreambot.behaviour.goldfarming;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.behaviour.combat.GenericCombat;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.BankAllItems;
import org.dreambot.loadouts.data.ItemID;

import java.util.function.BooleanSupplier;

public class KillCowsForHide extends IronFractal {

    public KillCowsForHide(BooleanSupplier acceptCondition) {
        super(acceptCondition);

        Area[] cowPens = new Area[]{
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

        Area[] restAreas = new Area[]{
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

        // math min just incase i fuck with it later.
        int random = Calculations.random(Math.min(restAreas.length, cowPens.length));
        setSimpleName("cow");

        addChildren(
                new BankAllItems(Inventory::isFull),
                new BankAllItems(ItemID.POTATO, ItemID.COWHIDE).setSimpleName("Safe bank"),
                // kill cows
                new GenericCombat(() -> true,
                        cowPens[random],
                        x -> !x.isInCombat() && "Cow".equals(x.getName()))
                        .setLootFilter(x -> x.getId() == ItemID.COWHIDE)
                        .setRunAwayThreshold(2)
                        .setRestLocation(restAreas[random])
                        .setTrainPrayer(true)
                        .setSimpleName("Kill cow")
        );
    }
}
