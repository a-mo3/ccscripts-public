package org.dreambot.behaviour.quest;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.behaviour.combat.GenericCombat;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.BankAllItems;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.generics.GenericItemUse;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.behavior.generic.TransactAtStore;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.Items;
import org.dreambot.loadouts.data.ShopLocation;
import org.dreambot.utility.OwnedItems;

import java.util.function.BooleanSupplier;

public class WitchsPotion extends IronFractal {
    public WitchsPotion(BooleanSupplier acceptCondition) {
        super(acceptCondition);
        setSimpleName("Witchs pot.");

        InventoryLoadoutItem eyeOfNewt = new InventoryLoadoutItem(ItemID.EYE_OF_NEWT)
                .setRestockMethod(
                        new TransactAtStore(ShopLocation.BETTYS_MAGIC_EMPORIUM, ItemID.EYE_OF_NEWT)
                                .addInventoryItem(Items.COINS_SELL_AXE)
                );

        InventoryLoadoutItem onion = new InventoryLoadoutItem(ItemID.ONION)
                .setRestockMethod(
                        new GenericEntityInteraction(() -> !OwnedItems.contains(ItemID.ONION), () -> GameObjects.closest("Onion"))
                                .setAction("Pick")
                                .setEntityLocation(new Area(3186, 3269, 3192, 3265))
                                .setSimpleName("Get onion")
                );

        Area cowPen = new Area(
                new Tile(3240, 3298, 0),
                new Tile(3265, 3298, 0),
                new Tile(3266, 3255, 0),
                new Tile(3253, 3255, 0),
                new Tile(3253, 3272, 0),
                new Tile(3249, 3278, 0),
                new Tile(3245, 3278, 0),
                new Tile(3240, 3286, 0)
        );

        InventoryLoadoutItem rawBeef = new InventoryLoadoutItem(ItemID.RAW_BEEF)
                .setRestockMethod(
                        new IronFractal(() -> !OwnedItems.contains(ItemID.RAW_BEEF)).setSimpleName("Beef")
                                .addChildren(
                                        new BankAllItems(() -> Inventory.contains(ItemID.COINS_995)).setSimpleName("Bank coins"),
                                        new GenericCombat(() -> true, cowPen, x -> "Cow".equals(x.getName()))
                                                .setLootFilter(x -> x.getId() == ItemID.RAW_BEEF)
                                                .setRestLocation(new Area(3238, 3270, 3247, 3264))
                                                .setRunAwayThreshold(3)
                                                .setSimpleName("Kill cow")
                                )
                );

        InventoryLoadoutItem cookedBeef = new InventoryLoadoutItem(ItemID.COOKED_MEAT)
                .setRestockMethod(new GenericItemUse(() -> !Inventory.contains(ItemID.BURNT_MEAT, ItemID.COOKED_MEAT),
                        () -> GameObjects.closest("Range"), ItemID.RAW_BEEF)
                        .setLocation(new Area(3229, 3198, 3237, 3195))
                        .addInventoryItem(rawBeef)
                        .setSimpleName("Cook raw beef")
                );

        InventoryLoadoutItem burntMeat = new InventoryLoadoutItem(ItemID.BURNT_MEAT)
                .setRestockMethod(
                        new GenericItemUse(() -> !OwnedItems.contains(ItemID.BURNT_MEAT),
                                () -> GameObjects.closest("Range"), ItemID.COOKED_MEAT)
                                .setLocation(new Area(3229, 3198, 3237, 3195))
                                .addInventoryItem(cookedBeef)
                                .setSimpleName("Burn beef")
                );

        Area lumRats = new Area(3202, 3208, 3216, 3203);
        InventoryLoadoutItem ratTail = new InventoryLoadoutItem(ItemID.RATS_TAIL)
                .setRestockMethod(
                        new GenericCombat(() -> !OwnedItems.contains(ItemID.RATS_TAIL), lumRats, x -> "Rat".equals(x.getName()))
                                .setLootFilter(x -> ItemID.RATS_TAIL == x.getId())
                                .setSimpleName("Kill rat for tail")
                );

        addChildren(
                new GenericEntityInteraction(() -> !FreeQuest.WITCHS_POTION.isStarted(),
                        "Hetty",
                        new Tile(2968, 3205, 0))
                        .setDialogueChoices("quest", "Yes"),

                new GenericEntityInteraction(() -> FreeQuest.WITCHS_POTION.getConfigValue() == 1,
                        "Hetty",
                        new Tile(2968, 3205, 0))
                        .setDialogueChoices("quest", "Yes")
                        .addInventoryItem(onion)
                        .addInventoryItem(eyeOfNewt)
                        .addInventoryItem(ratTail)
                        .addInventoryItem(burntMeat), // do meat first because you might die to a cow

                new GenericEntityInteraction(() -> FreeQuest.WITCHS_POTION.getConfigValue() == 2,
                        "Cauldron",
                        new Tile(2968, 3205, 0))
                        .setAction("Drink-from")

        );
    }
}
