package org.dreambot.behaviour.quests.druidicritual;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;

import java.util.Arrays;
import java.util.List;

public class DruidicRitual extends Fractal {
    public DruidicRitual() {
        this.acceptCondition = () -> !PaidQuest.DRUIDIC_RITUAL.isFinished();
        List<Integer> kaqemeexState = Arrays.asList(0, 3);

        InventoryLoadout meats = new InventoryLoadout()
                .addItem(ItemID.RAW_BEAR_MEAT)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.ENCHANTED_BEAR))
                .addItem(ItemID.RAW_CHICKEN)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.ENCHANTED_CHICKEN))
                .addItem(ItemID.RAW_RAT_MEAT)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.ENCHANTED_RAT))
                .addItem(ItemID.RAW_BEEF)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.ENCHANTED_BEEF))
                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995));

        this.paintArraySupplier = () -> new String[]{
                "Druidic Ritual: " + PaidQuest.DRUIDIC_RITUAL.getConfigValue()
        };

        addChildren(
                new TalkToFractal(() -> !PaidQuest.DRUIDIC_RITUAL.isStarted(),
                        new Tile(2925, 3486, 0).getArea(5),
                        () -> NPCs.closest("Kaqemeex"))
                        .setDialogueOptions("I'm in search of a quest", "Okay, I will try and help.", "Yes.")
                        .setInventoryLoadout(meats)
                        .setSimpleName("Start @ Kagemeex"),

                new TalkToFractal(() -> PaidQuest.DRUIDIC_RITUAL.getConfigValue() == 1
                        || Inventory.containsAll(ItemID.ENCHANTED_BEAR, ItemID.ENCHANTED_CHICKEN, ItemID.ENCHANTED_RAT, ItemID.ENCHANTED_BEEF),
                        new Tile(2899, 3429, 1).getArea(5),
                        () -> NPCs.closest("Sanfew"))
                        .setDialogueOptions("I've been sent to help purify the Varrock stone circle.", "Ok, I'll do that then.")
                        .setSimpleName("Sanfew"),

                new UseOnFractal(() -> PaidQuest.DRUIDIC_RITUAL.getConfigValue() == 2,
                        () -> Inventory.get(x -> x.getName().contains("Raw")),
                        () -> GameObjects.closest("Cauldron of thunder"),
                        true)
                        .setArea(new Tile(2893, 9831, 0).getArea(3))
                        .setInventoryLoadout(meats)
                        .setSimpleName("Enchant meats"),

                new TalkToFractal(() -> PaidQuest.DRUIDIC_RITUAL.getConfigValue() == 3,
                        new Tile(2925, 3486, 0).getArea(5),
                        () -> NPCs.closest("Kaqemeex"))
                        .setSimpleName("Finish @ Kagemeex")
        );
    }
}
