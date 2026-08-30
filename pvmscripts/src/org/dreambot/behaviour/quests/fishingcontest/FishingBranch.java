package org.dreambot.behaviour.quests.fishingcontest;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;

import java.util.function.Supplier;

public class FishingBranch extends Fractal {
    public static final Area SHRIMP_AREA = new Area(3240, 3159, 3246, 3141);
    public static final Area BARB_VILLAGE_FISHING = new Area(3101, 3422, 3111, 3435);
    public FishingBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        setSimpleName("Fishing training");

        addChildren(
                new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) < 20,
                        SHRIMP_AREA, () -> NPCs.closest(n -> n.hasAction("Net") && SHRIMP_AREA.contains(n)))
                        .setShouldBank(false)
                        .setInteraction("Net")
                        .setSimpleName("Shrimp until lvl 20")
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .strictIgnore(ItemID.RAW_SHRIMPS, ItemID.RAW_ANCHOVIES)
                                        .addItem(FishingFractal.SMALL_FISHING_NET, 1)
                                        .setStrict(true)
                        ),
                // fly fish
                new FishingFractal(() -> true,
                        BARB_VILLAGE_FISHING, () -> NPCs.closest(n -> n.hasAction("Lure") && BARB_VILLAGE_FISHING.contains(n)))
                        .setShouldBank(false)
                        .setInteraction("Lure")
                        .setSimpleName("Salmon/Trout until 82")
                        .setInventoryLoadout(new InventoryLoadout()
                                .setStrict(true)
                                .strictIgnore(ItemID.RAW_SALMON, ItemID.RAW_TROUT)
                                .addItem(FishingFractal.FLY_FISHING_ROD, 1)
                                .addItem(FishingFractal.FEATHER, 1, 5000)
                                .setMuleRequestAmount(30_000)
                        )
        );
    }
}
