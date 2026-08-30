package org.dreambot.behaviour.quests.rfd;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.quests.CooksAssistant;
import org.dreambot.behaviour.training.cooking.RoguesDenCook;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.BuyFromShopFractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;

public class AnotherCooksAssistant extends Fractal {
    public AnotherCooksAssistant() {
        super(() -> PaidQuest.RECIPE_FOR_DISASTER.getConfigValue() < 3);

        this.paintArraySupplier = () -> new String[]{
                "State " + PaidQuest.RECIPE_FOR_DISASTER.getConfigValue(),
                ""
        };

        final Area COOKS_AREA = new Area(3205, 3217, 3210, 3212);

        setSimpleName("RFD Cook");

        addChildren(
                new CooksAssistant().setSimpleName("Cooks assistant"),
                new RoguesDenCook(() -> Skills.getRealLevel(Skill.COOKING) < 10, ItemID.RAW_SARDINE, 120, ItemID.SARDINE)
                        .setSimpleName("Sardines to lvl 10"),

                // cant buy rotten tomatoes on GE
                new BuyFromShopFractal(() -> !OwnedItems.contains(ItemID.ROTTEN_TOMATO) && PaidQuest.RECIPE_FOR_DISASTER.getConfigValue() < 1,
                        () -> GameObjects.closest("Crate"),
                        new Tile(3225, 3415, 0).getArea(4),
                        ItemID.ROTTEN_TOMATO)
                        .setAction("Buy")
                        .setInventoryLoadout(new InventoryLoadout().addItem(ItemID.COINS_995, 100))
                        .setSimpleName("Get a rotten tomato"),

                // give chef his shit
                new TalkToFractal(
                        () -> PaidQuest.RECIPE_FOR_DISASTER.getConfigValue() < 2,
                        COOKS_AREA,
                        () -> NPCs.closest("Cook"),
                        "Talk-to",
                        "quests for me", "angry!", "What seems to be the problem", "YES")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.EYE_OF_NEWT)
                                .addItem(ItemID.GREENMANS_ALE)
                                .addItem(ItemID.ASHES)
                                .setEnabledCondition(() -> !OwnedItems.contains(ItemID.DIRTY_BLAST))
                                .addItem(ItemID.FRUIT_BLAST)
                                .setEnabledCondition(() -> !OwnedItems.contains(ItemID.DIRTY_BLAST))
                                .addItem(ItemID.DIRTY_BLAST)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.DIRTY_BLAST))
                                .addItem(ItemID.ROTTEN_TOMATO)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.ROTTEN_TOMATO))
                        )
                        .setPrependLogic(() -> {
                            if (Inventory.containsAll(ItemID.ASHES, ItemID.FRUIT_BLAST)
                                    && !Inventory.contains(ItemID.DIRTY_BLAST)
                                    && PaidQuest.RECIPE_FOR_DISASTER.getConfigValue() == 1) {
                                log("Mixing dirty blast");
                                Inventory.combine(ItemID.ASHES, ItemID.FRUIT_BLAST);
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Talk to chef"),

                new TalkToFractal(
                        () -> PaidQuest.RECIPE_FOR_DISASTER.getConfigValue() < 3,
                        COOKS_AREA,
                        () -> GameObjects.closest(x -> new Tile(3207, 3217).equals(x.getTile()) && "Door".equals(x.getName())),
                        "Open",
                        "")
                        .setSimpleName("Enter banquet")
        );
    }
}
