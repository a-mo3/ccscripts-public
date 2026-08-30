package util;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;

import java.util.Arrays;
import java.util.List;

public class EatUtil {
    private static final List<Integer> goodEatingFood = Arrays.asList(
            ItemID.JUG_OF_WINE,
            ItemID.DARK_CRAB,
            ItemID.TUNA_POTATO,
            ItemID.MANTA_RAY,
            ItemID.SEA_TURTLE,
            ItemID.PINEAPPLE_PIZZA,
            ItemID.SHARK,
            ItemID.MUSHROOM_POTATO,
            ItemID.UGTHANKI_KEBAB_1885,
            ItemID.CURRY,
            ItemID.COOKED_KARAMBWAN,
            ItemID.ANCHOVY_PIZZA,
            ItemID.ANGLERFISH,
            ItemID.MONKFISH,
            ItemID.POTATO_WITH_CHEESE,
            ItemID.MEAT_PIZZA,
            ItemID.POTATO_WITH_BUTTER,
            ItemID.SWORDFISH,
            ItemID.PLAIN_PIZZA,
            ItemID.BASS,
            ItemID.LOBSTER,
            ItemID.CHOCOLATE_CAKE,
            ItemID.CAKE,
            ItemID.STEW,
            ItemID.TUNA,
            ItemID.SALMON,
            ItemID.PIKE,
            ItemID.COD,
            ItemID.TROUT,
            ItemID.MACKEREL,
            ItemID.HERRING,
            ItemID.BREAD,
            ItemID.SARDINE,
            ItemID.COOKED_MEAT,
            ItemID.COOKED_CHICKEN,
            ItemID.SHRIMPS
    );

    public static boolean eat() {
        for (Integer foodID : goodEatingFood) {
            if (Inventory.contains(foodID)) {
                if (foodID == ItemID.JUG_OF_WINE) {
                    if (Inventory.interact(foodID, "Drink")) {
                        return true;
                    }
                }
                if (Inventory.interact(foodID, "Eat")) {
                    return true;
                }
            }
        }
    return false;
    }

    public static boolean hasFood() {
        for (Integer foodID : goodEatingFood) {
            if (Inventory.contains(foodID)) {
                return true;
            }
        }
        return false;
    }

    public static int bestFoodInBank() {
        if (Bank.isOpen()) {
            for (Integer foodID : goodEatingFood) {
                if (Bank.contains(foodID)) {
                    return foodID;
                }
            }
        }
        return -1;
    }
}
