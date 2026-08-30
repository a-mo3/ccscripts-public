package org.dreambot.behaviour.quests.demonslayer;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.fractals.util.OwnedItems;

import java.util.HashMap;

public class DemonSlayer extends Fractal {
    public static final int DELRITH_SILVERLIGHT_CASE = 2567;
    public static final int DELRITH_DRAIN_KEY = 2568;
    VarbitRequirement hasPouredWaterIntoDrain = new VarbitRequirement(DELRITH_DRAIN_KEY, 1);
    VarbitRequirement obtainedSilverlight = new VarbitRequirement(DELRITH_SILVERLIGHT_CASE, 1);
//    VarbitRequirement delrithNearby = new NpcCondition(NpcID.DELRITH);
//    VarbitRequirement delrithWeakenedNearby = new NpcCondition(NpcID.DELRITH_WEAKENED);
//    VarbitRequirement inInstance = new VarbitRequirement(VarbitID.DELRITH_SEEN_SUMMONING_CUTSCENE, 1);

//    talkToAris = new NpcStep(this, NpcID.ARIS, new WorldPoint(3204, 3424, 0), "Talk to Aris in her tent in Varrock Square.", coin);
//    talkToPrysin = new NpcStep(this, NpcID.SIR_PRYSIN, new WorldPoint(3203, 3472, 0), "Talk to Sir Prysin in the south west corner of Varrock Castle.");
//    talkToRovin = new NpcStep(this, NpcID.CAPTAIN_ROVIN, new WorldPoint(3205, 3498, 2), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");


    public DemonSlayer() {
        super(() -> !FreeQuest.DEMON_SLAYER.isFinished());
        setSimpleName("Demon slayer");

        addChildren(
                // Once you have silverlight its time to kill delrith
                new FightDelrith(() -> OwnedItems.contains(ItemID.SILVERLIGHT)),

                new TalkToFractal(() -> FreeQuest.DEMON_SLAYER.getConfigValue() == 0,
                        new Tile(3204, 3424, 0),
                        () -> NPCs.closest("Aris"))
                        .setDialogueOptions(
                                "The Demon Slayer Quest",
                                "Yes.",
                                "Ok, here you go.",
                                "Okay, where is he?",
                                "So how did Wally kill Delrith?"
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 1, 10)
                                .addItem(ItemID.BONES, 25)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.NECKLACE_OF_PASSAGE)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH))
                        .setSimpleName("Start at Aris"),

                new TalkToFractal(() -> FreeQuest.DEMON_SLAYER.getConfigValue() == 1,
                        new Tile(3203, 3472, 0),
                        () -> NPCs.closest("Sir Prysin"))
                        .setDialogueOptions(
                                "press on with it.",// exit dialogue from aris
                                "Aris said I should come and talk to you.",
                                "I need to find Silverlight.",
                                "He's back and unfortunately I've got to deal with him.",
                                "So give me the keys!",
                                "Can you give me your key?",
                                "Rovin"
                        )
                        .setSimpleName("Talk to Prysin"),

                new TalkToFractal(() -> !OwnedItems.contains(ItemID.SILVERLIGHT_KEY_1),
                        new Tile(3114, 3163, 1),
                        () -> NPCs.closest("Wizard Traiborn"))
                        .setDialogueOptions(
                                "Talk about Demon Slayer.",
                                "I need to get a key given to you by Sir Prysin.",
                                "Well, have you got any keys knocking around?",
                                "I'll get the bones for you."
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.BONES, 25)
                                .addItem(ItemID.BUCKET_OF_WATER)
                        )
                        .setSimpleName("Traiborn key"),

                new TalkToFractal(() -> !OwnedItems.contains(ItemID.SILVERLIGHT_KEY_2),
                        new Tile(3205, 3498, 2),
                        () -> NPCs.closest("Captain Rovin"))
                        .setDialogueOptions(
                                "Yes I know, but this is important.",
                                "There's a demon who wants to invade this city.",
                                "Yes, very.",
                                "It's not them who are going to fight the demon, it's me.",
                                "Sir Prysin said you would give me the key.",
                                "Why did he give you one of the keys then?"
                        )
                        .setSimpleName("Rovin key"),

                new UseOnFractal(() -> hasPouredWaterIntoDrain.isNotComplete() && !OwnedItems.contains(ItemID.SILVERLIGHT_KEY_3),
                        () -> Inventory.get(ItemID.BUCKET_OF_WATER),
                        () -> GameObjects.closest("Drain"), true)
                        .setArea(new Tile(3225, 3496, 0))
                        .setSimpleName("Drain"),
//                3225, 9897, 0

                new TalkToFractal(() -> !OwnedItems.contains(ItemID.SILVERLIGHT_KEY_3),
                        new Tile(3225, 9897, 0),
                        () -> GameObjects.closest("Rusty key"))
                        .setInteraction("Take")
                        .setSimpleName("Sewer key"),

                new TalkToFractal(() -> true,
                        new Tile(3203, 3472, 0),
                        () -> NPCs.closest("Sir Prysin"))
                        .setDialogueOptions()
                        .setSimpleName("Talk to Prysin for silverlight")


        );

    }

}
