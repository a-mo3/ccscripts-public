package org.dreambot.behaviour;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.quests.fishingcontest.FishingFractal;
import org.dreambot.behaviour.training.mining.MixedMining;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;

import java.util.function.Supplier;

public class TotalLevel extends ShuffleFractal {
    public static final Area SHRIMP_AREA = new Area(3240, 3159, 3246, 3141);
    public static final Area BARB_VILLAGE_FISHING = new Area(3101, 3422, 3111, 3435);

    public TotalLevel(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        addChildren(
                new Fractal(() -> Skills.getRealLevel(Skill.FISHING) < 33).setSimpleName("Fishing")
                        .addChildren(
                                new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) < 20,
                                        SHRIMP_AREA, () -> NPCs.closest(n -> n.hasAction("Net") && SHRIMP_AREA.contains(n)))
                                        .setShouldBank(false)
                                        .setInteraction("Net")
                                        .setSimpleName("Shrimp")
                                        .setInventoryLoadout(
                                                new InventoryLoadout()
                                                        .strictIgnore(ItemID.RAW_SHRIMPS, ItemID.RAW_ANCHOVIES)
                                                        .addItem(ItemID.SMALL_FISHING_NET, 1)
                                                        .setStrict(true)
                                        ),
                                new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) < 82,
                                        BARB_VILLAGE_FISHING, () -> NPCs.closest(n -> n.hasAction("Lure") && BARB_VILLAGE_FISHING.contains(n)))
                                        .setShouldBank(false)
                                        .setInteraction("Lure")
                                        .setSimpleName("Salmon/Trout")
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .setStrict(true)
                                                .strictIgnore(ItemID.RAW_SALMON, ItemID.RAW_TROUT)
                                                .addItem(ItemID.FLY_FISHING_ROD, 1)
                                                .addItem(ItemID.FEATHER, 1, 5000)
                                                .setMuleRequestAmount(30_000)
                                        )
                        ),
                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chopping"),
                new MixedMining(() -> Skills.getRealLevel(Skill.MINING) < 33).setSimpleName("Mining")
        );
    }
}
