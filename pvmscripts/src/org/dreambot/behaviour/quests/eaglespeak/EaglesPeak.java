package org.dreambot.behaviour.quests.eaglespeak;


import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.NpcID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;

import java.util.function.Supplier;

public class EaglesPeak extends Fractal {

    // handle dreambot walker failure
    // once you are on the mountain it walker will get to the door
    public static final Area ON_MOUNTAIN = new Area(
            new Tile(2322, 3499, 0),
            new Tile(2331, 3503, 0),
            new Tile(2350, 3496, 0),
            new Tile(2350, 3468, 0),
            new Tile(2334, 3464, 0),
            new Tile(2315, 3487, 0));


    // walk here if ur not on the mountain
    public static final Area MOUNTAIN_START = new Area(2340, 3474, 2343, 3471);

    final Tile EAGLES_PEAK_ENTRANCE = new Tile(2328, 3496, 0);
    final Area EP_QUEST_START = new Area(2599, 3269, 2612, 3256);
    final Area EAGLE_PEAK_TENT = new Area(2301, 3520, 2319, 3506);

    public EaglesPeak(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setup();
    }

    public EaglesPeak() {
        this.acceptCondition = () -> !PaidQuest.EAGLES_PEAK.isFinished();
        setup();
    }

    private void setup() {
        this.paintArraySupplier = () -> new String[]{
                "Eagles Peak: " + PaidQuest.EAGLES_PEAK.getState()
        };

        addChildren(
                new TalkToFractal(() -> !PaidQuest.EAGLES_PEAK.isStarted(), EP_QUEST_START, () -> NPCs.closest(NpcID.CHARLIE_1495))
                        .setDialogueOptions("quest", "Yes.")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.YELLOW_DYE)
                                .addItem(ItemID.SWAMP_TAR)
                                .addItem(ItemID.VARROCK_TELEPORT, 5)
                                .addItem(ItemID.PISCATORIS_TELEPORT, 5)
                                .addItem(ItemID.COINS_995, 50, 500)
                                .setStrict(true)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                        )
                        .setPrependLogic(() -> {
                            if (Inventory.contains(ItemID.BIRD_SNARE, ItemID.RAW_BIRD_MEAT)) {
                                Inventory.dropAll(ItemID.BIRD_SNARE, ItemID.RAW_BIRD_MEAT);
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Start @ Charlie"),

                new TalkToFractal(() -> PaidQuest.EAGLES_PEAK.getConfigValue() == 5, EAGLE_PEAK_TENT, () -> GameObjects.closest("Books"))
                        .setInteraction("Inspect")
                        .setPrependLogic(() -> {
                            if (Inventory.contains("Bird book")) {
                                Inventory.interact("Bird book", "Read");
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Get Metal Feather"),


                new UseOnFractal(() -> PaidQuest.EAGLES_PEAK.getConfigValue() == 10, () -> Inventory.get(ItemID.METAL_FEATHER), () -> GameObjects.closest("Rocky outcrop"), true)
                        .setDialogueOptions("")
                        .setArea(EAGLES_PEAK_ENTRANCE)
                        .setSimpleName("Use feather on door"),

                new CaveBranch().setSimpleName("Cave branch"),

                new SaveNickolaus(() -> PaidQuest.EAGLES_PEAK.getConfigValue() == 20),

                new TalkToFractal(() -> PaidQuest.EAGLES_PEAK.getConfigValue() == 25 || PaidQuest.EAGLES_PEAK.getConfigValue() == 30,
                        EAGLE_PEAK_TENT, () -> NPCs.closest("Nickolaus"))
                        .setDialogueOptions("sent to find you", "sounds good")
                        .setPrependLogic(() -> {
                            if (Client.isInCutscene()) {
                                Dialog.solve();
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Get ferret"),

                new TalkToFractal(() -> PaidQuest.EAGLES_PEAK.getConfigValue() == 35, EP_QUEST_START, () -> NPCs.closest("Charlie"))
                        .setSimpleName("Finish quest")
        );
    }
}
