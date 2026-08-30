package org.dreambot.behaviour.quests.observatory;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

public class ObservatoryQuest extends Fractal {
    public ObservatoryQuest() {
        super(() -> !PaidQuest.OBSERVATORY_QUEST.isFinished());

        setSimpleName("Observatory");

        addChildren(

                new TalkToFractal(() -> PaidQuest.OBSERVATORY_QUEST.getConfigValue() == 0,
                        new Tile(2442, 3186, 0),
                        () -> NPCs.closest("Observatory Professor"))
                        .setDialogueOptions(
                                "Talk about the Observatory quest.",
                                "An Observatory?", "Yes."
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.PLANK, 3)
                                .addItem(ItemID.BRONZE_BAR)
                                .addItem(ItemID.MOLTEN_GLASS)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING))

                        // todo some combat equipment
                        .setSimpleName("Start quest"),

                new TalkToFractal(() -> PaidQuest.OBSERVATORY_QUEST.getConfigValue() < 4,
                        new Tile(2442, 3186, 0),
                        () -> NPCs.closest("Observatory Professor"))
                        .setDialogueOptions(
                                "Talk about the Observatory quest.",
                                "3 planks",
                                "molten glas",
                                "bronze"
                        )
                        .setSimpleName("Give resources"),

                new ObservatorySearchChests(() -> !OwnedItems.contains(ItemID.GOBLIN_KITCHEN_KEY))
                        .setSimpleName("Find key")

        );
    }
}
