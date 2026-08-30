package org.dreambot.behaviour.training.smithing;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;

import java.util.function.Supplier;

public class SmithingBranch extends Fractal {
    public SmithingBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        setSimpleName("Smithing");
        addChildren(
                new SmeltBars(() -> Skills.getRealLevel(Skill.SMITHING) < 19, ItemID.BRONZE_BAR).setSimpleName("Bronze bar")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.TIN_ORE, 14)
                                .setRefill(300)
                                .addItem(ItemID.COPPER_ORE, 14)
                                .setRefill(300)
                        ),
                new SmeltBars(() -> Skills.getRealLevel(Skill.SMITHING) < 19, ItemID.IRON_BAR)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.IRON_ORE, 28)
                                .setRefill(2400)
                        )
                        .setSimpleName("Iron bar"),
                new AnvilSmithing(() -> Skills.getRealLevel(Skill.SMITHING) < 34, ItemID.IRON_NAILS, ItemID.IRON_BAR)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.HAMMER, 1)
                                .addItem(ItemID.IRON_BAR, 1, 27)
                                .setRefill(400)
                        )
                        .setSimpleName("Iron Nails"),
                new AnvilSmithing(() -> true, ItemID.STEEL_NAILS, ItemID.STEEL_BAR)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.HAMMER, 1)
                                .addItem(ItemID.STEEL_BAR, 1, 27)
                                .setRefill(1400)
                        )
                        .setSimpleName("Steel Nails")
        );
    }
}
